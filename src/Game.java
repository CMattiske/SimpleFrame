import java.util.ArrayList;
import java.util.Random;


public class Game {
	
	public final static String NAME = "The Game";

	private GameUI ui;
	private Random rand;
	private ArrayList<GamePlayer> players;
	
	public Game(GameUI ui, Random rand, Player... newPlayers) {
		this.ui = ui;
		this.rand = rand;
		players = new ArrayList<GamePlayer>(0);
		for (Player p : newPlayers) {
			players.add(new GamePlayer(this, p));
		}
	}
	
	public void begin() {
		
	}
	
	public void update() {
		
	}
	
	public GamePlayer getPlayer(int index) {
		return players.get(index);
	}
}
