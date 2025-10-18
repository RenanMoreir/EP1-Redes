import java.util.ArrayList;
import java.util.List;

public class gameRoom {
    private String name;

    private Game game;
    private List<String> players;

    public gameRoom(String name){
        this.name = name;

        this.game = new Game();
        this.players = new ArrayList<>(2);

        System.out.println("Sala " + this.name + " criada");
    }

    public String getName() {
        return name;
    }
}
