package de.mineking.game.advanced;

import de.mineking.game.Direction;
import de.mineking.game.Player;
import de.mineking.game.Position;
import de.mineking.game.World;
import lombok.NonNull;

public class AdvancedPlayer extends Player {
	public AdvancedPlayer(@NonNull World world, @NonNull String name) {
		super(world, name);
	}

	public boolean stepBack(boolean simulate) {
		return queue("stepBack", () -> {
			turnAround();
			var res = step();
			turnAround();

			return res;
		}, simulate);
	}

	public boolean stepBack() {
		return stepBack(true);
	}

	public int step(int n) {
		return queue("step(" + n + ")", () -> {
			if(n == 0) return 0;

			int i = 0;

			if(n < 0) turnAround();
			for(; i < Math.abs(n) && step(); i++);
			if(n < 0) turnAround();

			return i;
		}, true);
	}

	public boolean goTo(int x, int y) {
		return goTo(new Position(world, x, y));
	}

	public boolean goTo(Position pos) {
		return queue("goto(" + pos + ")", () -> {
			if(world.getBlock(pos).filter(b -> b.canContainPlayer(world, pos, this)).isEmpty()) return false;

			var dx = pos.getX() - x;
			var dy = pos.getY() - y;

			if(dx != 0) face(dx > 0 ? Direction.RIGHT : Direction.LEFT);
			for(int i = 0; i < Math.abs(dx); i++) step();

			if(dy != 0) face(dy > 0 ? Direction.UP : Direction.DOWN);
			for(int i = 0; i < Math.abs(dy); i++) step();

			return true;
		}, true);
	}

	public boolean iterate(int x, int y, int dx, int dy, Runnable action) {
		return queue("iterate(" + x + ", " + y + ", " + dx + ", " + dy + ")", () -> {
			if(!goTo(x, y)) return false;
			face(Direction.UP);

			if(dx == 0 || dy == 0) return true;

			int a = 90;
			for(int i = 0; i < dx; i++) {
				for(int j = Math.min(1, i); j < dy - 1; j++) {
					if(action != null) action.run();
					if(!step()) return false;
				}

				if(i == dx - 1) break;

				turn(a);
				if(action != null) action.run();
				if(!step()) return false;
				turn(a);

				a *= -1;
			}

			goTo(x + dx - 1, y + 1);
			face(Direction.DOWN);

			if(action != null) action.run();
			step();
			turnRight();

			for(int i = 1; i < dx; i++) {
				if(action != null) action.run();
				if(!step()) return false;
			}

			return true;
		}, true);
	}

	public boolean iterate(int dx, int dy, Runnable action) {
		return iterate(x, y, dx, dy, action);
	}

	public boolean iterate(Runnable action) {
		return iterate(0, 0, world.getWidth(), world.getHeight(), action);
	}
}
