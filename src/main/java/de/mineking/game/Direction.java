package de.mineking.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Direction {
	UP(0),
	RIGHT(90),
	DOWN(180),
	LEFT(270);

	private final int angle;

	@NonNull
	public Direction right() {
		return of(angle + 90);
	}

	@NonNull
	public Direction left() {
		return of(angle - 90);
	}

	@NonNull
	public Direction opposite() {
		return of(angle + 180);
	}

	@NonNull
	public static Direction of(int angle) {
		angle += 360;
		angle %= 360;
		angle += 45;

		return values()[angle / 90];
	}
}
