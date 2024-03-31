import de.mineking.game.advanced.AdvancedPlayer;
import de.mineking.game.advanced.AdvancedWorld;

public class Test {
	public static void main(String[] args) {
		new Test();
	}

	private final AdvancedWorld world;
	private final AdvancedPlayer player;

	public Test() {
		world = new AdvancedWorld(10, 10);
		player = world.createPlayer("test");

		var window = world.createWindow(2);
		window.setDelay(0);

		window.listen(true, "w ArrowUp", player::step);
		window.listen(true, "s ArrowDown", player::stepBack);
		window.listen(true, "d ArrowRight", player::turnRight);
		window.listen(true, "a ArrowLeft", player::turnLeft);

		window.listen(true, "Space", () -> player.iterate(() -> player.placeFront("green")));
	}
}
