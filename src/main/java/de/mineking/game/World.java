package de.mineking.game;

import de.mineking.game.render.WorldWindow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.function.Supplier;

@Getter
public class World {
	protected final int width;
	protected final int height;

	protected final Block[][] world;
	protected final List<Player> players = new ArrayList<>();

	@Getter(AccessLevel.NONE)
	private final List<WorldWindow> windows = new ArrayList<>();

	public World(int width, int height) {
		this.width = width;
		this.height = height;

		this.world = new Block[width][height];
		initWorld();
	}

	private void initWorld() {
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				this.world[x][y] = new Block();
			}
		}
	}

	public boolean contains(@NonNull Position pos) {
		return pos.getWorld() == this && pos.isValid();
	}

	@NonNull
	public Player createPlayer(@NonNull String name) {
		var player = new Player(this, name);
		players.add(player);

		return player;
	}

	@NonNull
	public Optional<Block> getBlock(@NonNull Position pos) {
		if(!pos.isValid()) return Optional.empty();
		return Optional.of(getBlock(pos.getX(), pos.getY()));
	}

	@NonNull
	public Block getBlock(int x, int y) {
		return queue("getBlock(" + y + ", " + y + ")", () -> world[x % width][y % height]);
	}

	public boolean setBlock(@NonNull Position pos, @NonNull Block block) {
		if(!pos.isValid()) return false;

		setBlock(pos.getX(), pos.getY(), block);
		return true;
	}

	public void setBlock(int x, int y, @NonNull Block block) {
		queue("Block (" + x + ", " + y + ")", () -> world[x % width][y % height] = block);
	}

	@NonNull
	public WorldWindow createWindow(double zoom) {
		var window = new WorldWindow(this, zoom);
		windows.add(window);
		return window;
	}

	@NonNull
	public WorldWindow createWindow() {
		return createWindow(2);
	}

	public <T> T queue(@NonNull String name, @NonNull Supplier<T> action, boolean simulate) {
		if(windows.isEmpty()) return action.get();

		var future = windows.get(0).queue(name, action, simulate);
		future.thenRun(() -> windows.subList(1, windows.size()).forEach(WorldWindow::repaint));

		return future.join();
	}

	public <T> T queue(@NonNull String name, @NonNull Supplier<T> action) {
		return queue(name, action, false);
	}
}
