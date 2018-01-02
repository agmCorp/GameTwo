package uy.com.agm.gametwo.screens;

import uy.com.agm.gametwo.game.WorldController;
import uy.com.agm.gametwo.game.WorldRenderer;
import uy.com.agm.gametwo.util.GamePreferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;

public class GameScreen extends AbstractGameScreen {

	private static final String TAG = GameScreen.class.getName();

	private WorldController worldController;
	private WorldRenderer worldRenderer;

	private boolean paused;

	public GameScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void render(float deltaTime) {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(deltaTime);
		}
		// Sets the clear screen color to: Cornflower Blue
		// Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff
		// / 255.0f);
		Gdx.gl.glClearColor(0x80 / 255.0f, 0x80 / 255.0f, 0xc0 / 255.0f,
				0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render game world to screen
		worldRenderer.render();
	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	@Override
	public void show() {
		GamePreferences.instance.load();
		worldController = new WorldController(game);
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		worldController.dispose();
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		super.resume();
		// Only called on Android!
		paused = false;
	}

	@Override
	public InputProcessor getInputProcessor() {
		/*
		 * WorldController es un InputAdapter porque extiende dicha clase y
		 * también es un GestureListener porque implementa dicha interfaz. En
		 * WorldController entonces puedo reconocer gestos (como fling) y puedo
		 * reconocer eventos como touchUp que no existe dentro de la interfaz
		 * GestureListener sino que existe dentro de un InputAdapter. Como los
		 * métodos de InputAdapter son demasiados, decidí extender de dicha
		 * clase (para implementear dentro de WorldController solo el método que
		 * me interesa) e implemento la interfaz GestureListener porque despues
		 * de todo son pocos métodos extra que debo declarar que no use. Para
		 * trabajar con ambos InputProcessors a la vez, debo usar un
		 * InputMultiplexer. Los eventos fling y touchUp, por ejemplo, se
		 * ejecutan a la vez siempre. Primero registré GestureDetector para que
		 * fling se ejecute antes que touchUp y como están relacionados, al
		 * retornar true en el evento fling se cancela el touchUp. Si retorno
		 * false se ejecutan ambos.
		 */
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new GestureDetector(worldController));
		multiplexer.addProcessor(worldController);
		return multiplexer;
	}

}
