package de.mineking.game.render;

import de.mineking.game.World;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class WorldRenderer extends JComponent {
	private final WorldWindow window;
	private final World world;

	@Override
	public void paint(Graphics graphics) {
		drawFloor(graphics);
		drawPlayers((Graphics2D) graphics);
	}

	public void drawFloor(Graphics graphics) {
		setLocation(
				(getWidth() - world.getWidth() * adjust(1)) / 2,
				-(getHeight() - world.getHeight() * adjust(1)) / 2
		);

		for(int x = 0; x < world.getWidth(); x++) {
			for(int y = 0; y < world.getHeight(); y++) {
				var block = world.getWorld()[x][y];
				graphics.drawImage(Assets.loadTexture("blocks/" + (block.getId().isEmpty() ? "floor" : block.getId())),
						adjust(x),
						getHeight() - adjust(y + 1),
						adjust(1), adjust(1), null
				);
			}
		}
	}

	public void drawPlayers(Graphics2D graphics) {
		var transform = graphics.getTransform();

		graphics.setColor(Color.white);
		graphics.setFont(Assets.loadFont("OpenSans").deriveFont(Font.BOLD, (float) adjust(0.3)));

		var font = graphics.getFontMetrics();

		for(var player : world.getPlayers()) {
			if(!player.isVisible()) continue;

			var playerWidth = adjust(player.getSize());

			var x = adjust(player.getX());
			var y = getHeight() - adjust(player.getY() + 1);

			graphics.rotate(Math.toRadians(player.getDirection().getAngle()),
					x + adjust(0.5),
					y + adjust(0.5)
			);
			graphics.drawImage(Assets.loadTexture(player.getTexture()),
					x + (adjust(1) - playerWidth) / 2,
					y + (adjust(1) - playerWidth) / 2,
					playerWidth, playerWidth, null
			);
			graphics.rotate(0);

			if(player.isShowName()) {
				graphics.drawString(player.getName(), x + (adjust(1) - font.stringWidth(player.getName())) / 2, y + font.getHeight() / 2 + adjust(0.5));
			}
		}

		graphics.setTransform(transform);
	}

	public int adjust(double val) {
		return (int) (window.getZoom() * 32 * val);
	}
}
