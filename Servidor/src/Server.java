import java.io.*;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Server {
    private static final  int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<ClientSocket> clientes = new LinkedList<>();

    public void start(){
        try{
            System.out.println("Servidor Iniciado");
            serverSocket = new ServerSocket(PORT);
            clientConnectionLoop();
        }
        catch (IOException ex){
            System.out.println("Erros ao iniciar o servidor " + ex.getMessage());
        }
    }

    private void clientConnectionLoop(){
        try {
            while (true){
                ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
                clientes.add(clientSocket);
                new Thread(() -> clientMessageLoop(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao conectar cliente " + e.getMessage());
        }
    }

    private void sendMsgToALL(ClientSocket sender, String msg){
        Iterator<ClientSocket> iterator = clientes.iterator();
        for(ClientSocket clientSocket: clientes){
            if(!sender.equals(clientSocket))
                clientSocket.sendMsg(msg);
        }
    }

    private void clientMessageLoop(ClientSocket clientSocket){
        String msg;
        try {
            while ((msg = clientSocket.getMessage()) != null){
                if(!"sair".equalsIgnoreCase(msg)){
                    System.out.println("Msg recebida do cliente " + clientSocket.getRemoteSocketAddress() + ": " + msg);
                    sendMsgToALL(clientSocket,msg);
                }else {
                    return;
                }
            }
        }
        finally {
            clientSocket.close();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
