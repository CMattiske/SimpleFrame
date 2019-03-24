
public class GamePlayer {

	private Game game;
	private Player player;
	
	public GamePlayer(Game g, Player p) {
		this.game = g;
		this.player = p;
	}
	
	public String getName() {
		return player.getName();
	}
	
	public void doAction(int par, String str) {
		
	}
}
