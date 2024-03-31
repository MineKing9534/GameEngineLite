package de.mineking.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Position {
	private final World world;

	private final int x;
	private final int y;

	public boolean isValid() {
		return x >= 0 && y >= 0 && x <= world.getWidth() - 1 && y <= world.getHeight() - 1;
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}
}
