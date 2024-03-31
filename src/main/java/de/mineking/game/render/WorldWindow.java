package de.mineking.game.render;

import de.mineking.game.World;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
@Getter
public class WorldWindow extends JFrame {
	public final static Map<String, Predicate<KeyEvent>> eventMappings = Map.of(
			"Space", e -> e.getKeyChar() == ' ',
			"Enter", e -> e.getKeyChar() == '\n',
			"Escape", e -> e.getKeyCode() == KeyEvent.VK_ESCAPE,
			"ArrowUp", e -> e.getKeyCode() == KeyEvent.VK_UP,
			"ArrowDown", e -> e.getKeyCode() == KeyEvent.VK_DOWN,
			"ArrowRight", e -> e.getKeyCode() == KeyEvent.VK_RIGHT,
			"ArrowLeft", e -> e.getKeyCode() == KeyEvent.VK_LEFT
	);

	private final ExecutorService eventExecutor = Executors.newSingleThreadExecutor();
	private Future<?> event;

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

	private final World world;
	private final double zoom;

	private final static ThreadLocal<Boolean> ignoreTimeout = ThreadLocal.withInitial(() -> false);
	private final WorldRenderer renderer;

	@Setter
	private long delay;

	public WorldWindow(@NonNull World world, double zoom) {
		this.world = world;
		this.zoom = zoom;

		this.renderer = new WorldRenderer(this, world);

		setTitle("Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		setSize(renderer.adjust(world.getWidth()), renderer.adjust(world.getHeight()));
		setVisible(true);

		add(renderer);

		setDelay(200);
	}

	public void listen(boolean async, @NonNull Predicate<KeyEvent> filter, @NonNull Consumer<KeyEvent> handler) {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(!filter.test(e)) return;
				if(event != null && !event.isDone() && !async) return;
				event = eventExecutor.submit(() -> handler.accept(e));
			}
		});
	}

	public void listen(boolean async, @NonNull String events, @NonNull Runnable handler) {
		for(String event : events.split(" ")) {
			listen(async, e -> event.isEmpty() || (event.length() == 1 && e.getKeyChar() == event.charAt(0)) || (eventMappings.containsKey(event) && eventMappings.get(event).test(e)), e -> handler.run());
		}
	}

	public void listen(@NonNull Predicate<KeyEvent> filter, @NonNull Consumer<KeyEvent> handler) {
		listen(false, filter, handler);
	}

	public void listen(@NonNull String event, @NonNull Runnable handler) {
		listen(false, event, handler);
	}

	@NonNull
	public synchronized <T> CompletableFuture<T> queue(@NonNull String name, @NonNull Supplier<T> handler, boolean simulate) {
		var future = new CompletableFuture<T>();

		if(ignoreTimeout.get()) future.complete(handler.get());
		else {
			future.thenRun(this::repaint);
			executor.schedule(() -> {
				log.info(name);

				if(!simulate) ignoreTimeout.set(true);
				future.complete(handler.get());
				if(!simulate) ignoreTimeout.set(false);
			}, delay, TimeUnit.MILLISECONDS);
		}

		return future;
	}
}
