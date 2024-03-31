# GameEngineLite
Very, very lightweight GameEngine for teaching java basics.

## Install
```gradle
repositories {
  maven { url 'https://maven.mineking.dev/snapshots' }
}

dependencies {
  implementation 'de.mineking:GameEngineLite:VERSION'
}
```

## Basic Setup
```java
public class Test {
	public static void main(String[] args) {
		new Test();
	}

	private final World world;
	private final Player player;

	public Test() {
		world = new World(10, 10);
		player = world.createPlayer("test");

		var window = world.createWindow(2);

		window.listen("w", player::step);
		window.listen("s", player::stepBack);
		window.listen("d", player::turnRight);
		window.listen("a", player::turnLeft);
	}
}
```

## Advanced Setup
```java
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
		world.setDelay(0);

		window.listen("w ArrowUp", player::step);
		window.listen("s ArrowDown", player::stepBack);
		window.listen("d ArrowRight", player::turnRight);
		window.listen("a ArrowLeft", player::turnLeft);
	}
}
```