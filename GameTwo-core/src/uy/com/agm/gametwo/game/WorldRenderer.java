package uy.com.agm.gametwo.game;

import uy.com.agm.gametwo.game.WorldController.WorldListener;
import uy.com.agm.gametwo.util.Constants;
import uy.com.agm.gametwo.util.GamePreferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class WorldRenderer implements Disposable {
	private static final String TAG = WorldRenderer.class.getName();

	private OrthographicCamera camera;
	private OrthographicCamera cameraGUI;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private WorldController worldController;

	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController;
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		camera.position.set(Constants.VIEWPORT_WIDTH / 2,
				Constants.VIEWPORT_HEIGHT / 2, 0);
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();

		WorldListener worldListener = new WorldListener() {
			public void touchToWorld(Vector3 touch) {
				camera.unproject(touch);
			}

			public boolean insideViewPort(Vector3 touch) {
				return camera.frustum.pointInFrustum(touch);
			}
		};
		this.worldController.setListener(worldListener);
	}

	public void render() {
		renderObjects();
		renderGui();
	}

	private void renderObjects() {
		Gdx.app.debug(TAG, "PERFORMANCE: " + Gdx.graphics.getFramesPerSecond());
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();

		if (Constants.DEBUG_BOUNDS) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(1, 1, 0, 1);
			worldController.level.renderDebug(shapeRenderer);
			shapeRenderer.end();
		}
	}

	public void resize(int width, int height) {
		// No hacer nada!
	}

	private void renderGuiScore(SpriteBatch batch) {
		float x = -15;
		float y = 10;
		float offsetX = 50;
		float offsetY = 90;

		if (worldController.scoreVisual < worldController.score) {
			long shakeAlpha = System.currentTimeMillis() % 360;
			float shakeDist = 1.5f;
			offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist;
			offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist;
		}
		batch.draw(Assets.instance.item.items.get(4), x, y, offsetX, offsetY,
				250, 250, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch, ""
				+ (int) worldController.scoreVisual, x + 135, y + 37);
	}

	private void renderGuiFpsCounter(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if (fps >= 45) {
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else if (fps >= 30) {
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		} else {
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}

		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}

	private void renderGui() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();

		// draw collected gold coins icon + text (anchored to top left edge)
		renderGuiScore(batch);
		// draw FPS text (anchored to bottom right edge)
		if (GamePreferences.instance.showFpsCounter) {
			renderGuiFpsCounter(batch);
		}
		// draw game over text
		renderGuiGameOverMessage(batch);

		batch.end();
	}

	private void renderGuiGameOverMessage(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		if (worldController.isGameOver()) {
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1);
			fontGameOver.drawMultiLine(batch, "SE ACABÓ!", x, y, 1,
					BitmapFont.HAlignment.CENTER);
			fontGameOver.setColor(1, 1, 1, 1);
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}