import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Server {
    private static final  int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<gameRoom> rooms = new ArrayList<>();
    private final List<ClientSocket> clientes = new LinkedList<>();

    private final Scanner scanner = new Scanner(System.in);

    public void start(){
        try{
            String config;
            System.out.println("configurações do servidor");
            System.out.println("Crie as salas com o seguinte formato:SALA <nome da sala>");
            do{
                config = scanner.nextLine();
                createRoom(config);
            }while(!config.equalsIgnoreCase("pronto"));
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

    private void createRoom(String config){
        if(config.substring(0,4).equalsIgnoreCase("sala")){
            for (gameRoom room : rooms) {
                if (room.getName().equals(config.substring(5))) {
                    System.out.println("Já existe uma sala com esse nome");
                    return;
                }
            }
            rooms.add(new gameRoom(config.substring(5)));
        } else if(config.substring(0,3).equalsIgnoreCase("pronto")){
            return;
        } else {
            System.out.println("comando desconhecido");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
