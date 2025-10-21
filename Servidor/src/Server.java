import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Server {
    private static final  int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<gameRoom> rooms = new ArrayList<>();
    private final List<severSocket> clientes = new LinkedList<>();

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
            listRoom();
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
                severSocket severSocket = new severSocket(serverSocket.accept());
                clientes.add(severSocket);
                new Thread(() -> clientMessageLoop(severSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao conectar cliente " + e.getMessage());
        }
    }

    private void sendMsgToALL(severSocket sender, String texto, String sala){
        Iterator<gameRoom> iterator = rooms.iterator();
        for(gameRoom room : rooms){
            if(room.getName().equalsIgnoreCase(sala)){
                for(int i = 0; i< room.getPlayersSize(); i++){
                    if(!room.getPlayers(i).equals(sender)){
                        sender.sendMsgChat(sender, texto, room.getName());
                    }
                }
            }

            //if(!sender.equals(severSocket))
              //  severSocket.sendMsg(texto);
        }
    }

    private void clientMessageLoop(severSocket severSocket){
        String msg;
        try {
            while ((msg = severSocket.getMessage()) != null){
                String[] msgCampos = msg.split("\\|");
                System.out.println("campo 0: " + msgCampos[0] + " campo 1: " + msgCampos[1] + " campo 2: " + msgCampos[2]+ " campo 3: " + msgCampos[3]);
                if(msgCampos[1].equalsIgnoreCase("SERVER")){
                    switch (msgCampos[2]){
                        case "L":
                            severSocket.sendRooms(rooms);
                        case "S":
                            Iterator<gameRoom> iterator = rooms.iterator();
                            for(gameRoom room : rooms){
                                if(room.getName().equalsIgnoreCase(msgCampos[3])){
                                    if(room.getPlayersSize() >= 2){
                                        severSocket.sendErro("<-----ERRO: Sala lotada, por favor tente novamente----->");
                                    } else {
                                        room.addPlayers(severSocket);
                                        severSocket.sendRoomConectionConfirmation("Conexão com sala bem sucedida:" + room.getName());
                                    }
                                }
                            }
                            listRoom();
                    }
                } else {
                    if(!"sair".equalsIgnoreCase((msgCampos[3]))){
                        System.out.println("Msg recebida do cliente " + msgCampos[0] + ": " + msgCampos[3]);
                        sendMsgToALL(severSocket,msgCampos[3], msgCampos[1]);
                    }else {
                        return;
                    }
                }
            }
        }
        finally {
            severSocket.close();
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

    private void listRoom(){
        for (gameRoom room : rooms) {
            System.out.print(room.getName() + ": ");
            room.listPlayers();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
