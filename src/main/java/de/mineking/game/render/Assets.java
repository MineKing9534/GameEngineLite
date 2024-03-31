package de.mineking.game.render;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Assets {
	private final static Map<String, BufferedImage> textureCache = new HashMap<>();
	private final static Map<String, Font> fontCache = new HashMap<>();

	@NonNull
	public static BufferedImage loadTexture(@NonNull String name) {
		return textureCache.computeIfAbsent(name, k -> {
			try {
				return ImageIO.read(Assets.class.getResourceAsStream("/textures/" + name + ".png"));
			} catch (Exception e) {
				log.error("Failed to load texture {}", name, e);
				return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			}
		});
	}

	@NonNull
	public static Font loadFont(@NonNull String name) {
		return fontCache.computeIfAbsent(name, k -> {
			try {
				return Font.createFont(Font.TRUETYPE_FONT, Assets.class.getResourceAsStream("/font/" + name + ".ttf"));
			} catch (Exception e) {
				log.error("Failed to load font {}", name, e);
				return new JLabel().getFont();
			}
		});
	}
}
