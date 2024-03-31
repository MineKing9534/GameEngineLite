package de.mineking.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public class Player {
	protected final World world;

	protected final String name;

	protected int x = 0;
	protected int y = 0;

	@Setter
	private Direction direction = Direction.UP;

	@Setter
	private double size = 1.3;
	@Setter
	private String texture = "player";

	@Setter
	private boolean visible = true;
	@Setter
	private boolean showName = true;

	@NonNull
	public Position getPosition() {
		return new Position(world, x, y);
	}

	public void turn(int angle) {
		queue("turn(" + angle + ")", () -> direction = Direction.of(direction.getAngle() + angle));
	}

	public void turnRight() {
		turn(90);
	}

	public void turnLeft() {
		turn(-90);
	}

	public void turnAround() {
		turn(180);
	}

	public void face(@NonNull Direction direction) {
		queue("face(" + direction + ")", () -> this.direction = direction);
	}

	public boolean teleport(int x, int y) {
		return teleport(new Position(world, x, y));
	}

	public boolean teleport(@NonNull Position pos) {
		return queue("teleport(" + pos + ")", () -> {
			if(world.getBlock(pos).filter(b -> b.canContainPlayer(world, pos, this)).isEmpty()) return false;

			this.x = pos.getX();
			this.y = pos.getY();

			return true;
		});
	}

	@NonNull
	public Position getFrontPosition() {
		return new Position(world,
				switch (direction) {
					case LEFT -> x - 1;
					case RIGHT -> x + 1;
					default -> x;
				},
				switch (direction) {
					case UP -> y + 1;
					case DOWN -> y - 1;
					default -> y;
				}
		);
	}

	public boolean step() {
		return queue("step", () -> teleport(getFrontPosition()));
	}

	public boolean setFront(@NonNull Block block) {
		return world.setBlock(getFrontPosition(), block);
	}

	public boolean placeFront(@NonNull String id) {
		return queue("placeFront(" + id + ")", () -> world.getBlock(getFrontPosition()).map(b -> {
			b.setId(id);
			return b;
		}).isPresent());
	}

	public boolean placeFront() {
		return queue("placeFront", () -> world.getBlock(getFrontPosition()).map(b -> {
			b.setHeight(b.getHeight() + 1);
			return b;
		}).isPresent());
	}

	public boolean takeFront() {
		return queue("takeFront", () -> world.getBlock(getFrontPosition()).map(b -> {
			if(b.getHeight() == 0) return null;

			b.setHeight(b.getHeight() - 1);
			return b;
		}).isPresent());
	}

	@NonNull
	public Optional<Block> getFront() {
		return world.getBlock(getFrontPosition());
	}

	public void remove() {
		queue("remove", () -> world.getPlayers().remove(this));
	}

	public <T> T queue(@NonNull String name, @NonNull Supplier<T> action, boolean simulate) {
		return world.queue(this.name + ": " + name, action, simulate);
	}

	public <T> T queue(@NonNull String name, @NonNull Supplier<T> action) {
		return queue(name, action, false);
	}
}
