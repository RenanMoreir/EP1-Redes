import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Server {
    private static final  int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<gameRoom> rooms = new ArrayList<>();
    private final List<severSocket> clientes = new LinkedList<>();

    private final Scanner scanner = new Scanner(System.in);

    /*Medoto utilizado para iniciar o servidor, deve-se primeiro
    * criar a salas apartir do comandodo dado e por fim deve-se
    * digitar pronto para que o programa comese a roda*/
    public void start(){
        try{
            String config;
            System.out.println("configurações do servidor");
            System.out.println("<------------ Crie as salas com no formato SALA <nome da sala>, no final digite PRONTO ------------>");
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
    /* está função é responsavel por conectar o cliente com o servidor*/
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
    /*Função responsavel por repassar uma mensagem do chat para os outro usuarios*/
    private void sendMsgToALL(severSocket sender, String texto, String sala) {
        for (gameRoom room : rooms) {
            if (room.getName().equalsIgnoreCase(sala)) {
                for (int i = 0; i < room.getPlayersSize(); i++) {
                    severSocket destinatario = room.getPlayers(i);
                    if (!destinatario.equals(sender)) {
                        destinatario.sendMsgChat(sender, texto, sala);
                    }
                }
            }
        }
    }

    /*Eestá função se trata do controle principal das mesnagens recebidas eplo servidor*/
    private void clientMessageLoop(severSocket severSocket){
        String msg;
        try {
            while ((msg = severSocket.getMessage()) != null){
                //função que divide a menagem enviada do cliente a cada "|"
                String[] msgCampos = msg.split("\\|");
                //imprime a mensagem dividida
                System.out.println("campo 0: " + msgCampos[0] + " campo 1: " + msgCampos[1] + " campo 2: " + msgCampos[2]+ " campo 3: " + msgCampos[3]);
                //Analise se a mensagem é endereçada ao servidor
                if(msgCampos[1].equalsIgnoreCase("SERVER")){
                    switch (msgCampos[2]){
                        //caso o campo 2 seja para listar salas de jogo
                        case "L":
                            severSocket.sendRooms(rooms);
                        //caso a menssagem sejá para se conctar a uma sala
                        case "S":
                            Iterator<gameRoom> iterator = rooms.iterator();

                            for(gameRoom room : rooms){
                                if(room.getName().equalsIgnoreCase(msgCampos[3])){
                                    if(room.getPlayersSize() >= 2){
                                        severSocket.sendErro("<-----ERRO: Sala lotada, por favor tente novamente----->");
                                        break;
                                    }else {
                                        room.addPlayers(severSocket);
                                        severSocket.sendRoomConectionConfirmation("Conexão com sala bem sucedida:" + room.getName());
                                        break;
                                    }
                                }
                            }

                            listRoom();
                    }
                /*caso a menagem não sejá indereçada ao servidor
                  observa se ela é uma mensagem para desconectar, se não,
                  considera que a mensagem é uma menssagem do chat*/
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
    /*função responsavel por criar as salas */
    private void createRoom(String config){
        if(config.length() > 5) {
            if(config.substring(0,4).equalsIgnoreCase("sala")){
                for (gameRoom room : rooms) {
                    if (room.getName().equals(config.substring(5))) {
                        System.out.println("Já existe uma sala com esse nome");
                        return;
                    }
                }
                rooms.add(new gameRoom(config.substring(5)));
                return;
            } else if(config.equalsIgnoreCase("pronto")) {
                return;
            }
        }
            System.out.println("comando desconhecido");
    }

    /*função que lista as salas*/
    private void listRoom(){
        for (gameRoom room : rooms) {
            System.out.print(room.getName() + ": ");
            room.listPlayers();
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
