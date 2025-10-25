
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSocket {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        System.out.println("Cliente " + socket.getRemoteSocketAddress() + " conectado");
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }

    public void close(){
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar a coneção: " + e.getMessage());
        }
    }
    //função que envia uma mensagem no chat
    public void sendMsgChat(ClientSocket sender, String texto, String room){
        sendMsg(sender + "|" + room + "|M|" + texto);
    }

    public String getMessage() {
        try {
            return (String) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Erro ao ler a mensagem: " + e.getMessage());
            return null;
        }
    }
    //função que envia uma mensagem solicitando as salas
    public void requestRooms(){
        sendMsg(socket.getRemoteSocketAddress() + "|SERVER|L|NULL");
    }
    //função que envia uma mensagem se conectando com uma sala
    public void conectRoom(String room){
        sendMsg(socket.getRemoteSocketAddress()+"|SERVER|S|"+room);
    }
    //função que envia uma mensagem
    public void sendMsg(String msg){
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Erro ao enviar a mensagem: " + e.getMessage());
        }
    }
}
