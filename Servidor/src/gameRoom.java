import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class gameRoom {
    private String name;

    private Game game;
    private List<severSocket> players;

    public gameRoom(String name){
        this.name = name;
        this.game = new Game();
        this.players = new ArrayList<>(2);

        System.out.println("Sala " + this.name + " criada");
    }

    public void listPlayers(){
        if(players.size() == 0){
            System.out.println("Sala vazia");
        }else if (players.size() == 1){
            System.out.print(players.get(0) + ".");
        } else {
            System.out.print(players.get(0) + ", " + players.get(1) + ".");
        }
    }

    public void addPlayers(severSocket players) {
        this.players.add(players);
    }

    public String getName() {
        return name;
    }

    public severSocket getPlayers(int i){return players.get(i);}

    public int getPlayersSize(){return players.size();}
}
