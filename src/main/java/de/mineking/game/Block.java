package de.mineking.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Block {
	private String id = "";
	private int height = 0;

	public boolean canContainPlayer(@NonNull World world, @NonNull Position pos, @NonNull Player player) {
		return true;
	}
}
