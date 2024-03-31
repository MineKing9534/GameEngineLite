package de.mineking.game.advanced;

import de.mineking.game.World;
import lombok.NonNull;

public class AdvancedWorld extends World {
	public AdvancedWorld(int width, int height) {
		super(width, height);
	}

	@NonNull
	@Override
	public AdvancedPlayer createPlayer(@NonNull String name) {
		var player = new AdvancedPlayer(this, name);
		players.add(player);

		return player;
	}
}
