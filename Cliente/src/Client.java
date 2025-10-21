import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client implements Runnable{
    private static final  String SERVER_ADRESS = "127.0.0.1";
    private  static final int serverSocket = 4000;
    private ClientSocket clientSocket;
    private static String room = "null";

    private Scanner scanner;

    public Client(){
        scanner = new Scanner(System.in);
    }

    public void start(){
        try {
            System.out.println("Cliente conectado ao servidor em "  + SERVER_ADRESS + " Porta: " + serverSocket);
            clientSocket = new ClientSocket(new Socket(SERVER_ADRESS, serverSocket));
            //new Thread(this).start();
            messageLoop();
        }
        catch (IOException ex){
            System.out.println("Erros ao iniciar o Cliente " + ex.getMessage());
        }
        finally {
            clientSocket.close();
        }
    }

    @Override
    public void run(){
        String msg;
        while ((msg = clientSocket.getMessage()) != null ){
            //System.out.print("\033[2K\r");
           // System.out.println("\r Msg recebida:" + msg);
            //System.out.print("Digite uma mensagem: ");
        }
    }

    private void  messageLoop(){
        try{
            String[] msg;
            if(room.equalsIgnoreCase("null")){
               clientSocket.requestRooms();
               msg = (clientSocket.getMessage()).split("/");
               System.out.println("Salas de Jogo Disponiveis:\n" + msg[4]);


            }

            /*String msg;
            do{
                System.out.print("Digite uma mensagem: ");
                msg = scanner.nextLine();
                clientSocket.sendMsg(msg);
            } while (!msg.equalsIgnoreCase("sair"));*/
        } catch (Exception e) {
            System.out.print("Erros ao enviar a mensagem " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
