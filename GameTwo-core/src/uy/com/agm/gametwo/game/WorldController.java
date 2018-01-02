package uy.com.agm.gametwo.game;

import uy.com.agm.gametwo.game.objects.Item;
import uy.com.agm.gametwo.screens.DirectedGame;
import uy.com.agm.gametwo.util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class WorldController extends InputAdapter implements GestureListener,
		Disposable {
	public interface WorldListener {
		public void touchToWorld(Vector3 touch);

		public boolean insideViewPort(Vector3 touch);
	}

	public Level level;
	public int score;
	public float scoreVisual;

	private static final String TAG = WorldController.class.getName();

	// Se va a usar para volver al menu
	private DirectedGame game;
	private WorldListener listener;
	private Vector3 touchPos;
	private float x0, y0;
	private boolean calculateDelta = false;
	private Item target;

	public void setListener(WorldListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Gdx.app.debug(TAG, "DOWN");

		touchPos.set(x, y, 0);
		listener.touchToWorld(touchPos);
		target = null;

		for (Item item : level.items) {
			if (item.bounds.contains(touchPos.x, touchPos.y)) {
				target = item;
				target.freeze();
				target.state = Item.ITEM_STATE_PLAYING;

				// Origen crudo de coordenadas
				x0 = x;
				y0 = y;
				calculateDelta = true;
				break;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Gdx.app.debug(TAG, "UP");
		if (target != null) {
			calculateDelta = false;
			target.setCaidaLibre();
		}
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		Gdx.app.debug(TAG, "PAN");
		float xi, yi, dX = 0, dY = 0;

		if (calculateDelta) {
			// Punto en la camara del render en donde el usuario tiene su dedo
			// ahora.
			touchPos.set(x, y, 0);
			listener.touchToWorld(touchPos);

			if (listener.insideViewPort(touchPos)) {
				xi = touchPos.x;
				yi = touchPos.y;

				// Punto en la camara del render en donde está el origen
				touchPos.set(x0, y0, 0);
				listener.touchToWorld(touchPos);

				// Distancia en metros al origen
				dX = xi - touchPos.x;
				dY = yi - touchPos.y;

				// Nuevo origen crudo de coordenadas
				x0 = x;
				y0 = y;

				// Movemos el centro del target delta metros
				target.position.add(dX, dY);
			} else {
				calculateDelta = false;
				target.setCaidaLibre();
				target = null;
			}
		}
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		Gdx.app.debug(TAG, "PANSTOP");
		if (target != null) {
			calculateDelta = false;
			target.setCaidaLibre();
		}
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		Gdx.app.debug(TAG, "FLING");
		if (target != null) {
			// target.freeze();
			touchPos.set(velocityX, velocityY, 0);
			listener.touchToWorld(touchPos);
			// Porcentaje de cada componente para mantener la dirección correcta
			target.terminalVelocity.set(
					Math.abs(Constants.FLING_POWER_PERCENTAGE * touchPos.x),
					Math.abs(Constants.FLING_POWER_PERCENTAGE * touchPos.y));

			target.velocity.set(touchPos.x, touchPos.y);
		}
		// Evita ejecución de touchUp
		return true;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		level = new Level();
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	public WorldController(DirectedGame game) {
		this.game = game;
		touchPos = new Vector3();
		score = 0;
		scoreVisual = score;
		level = new Level();
	}

	public void update(float deltaTime) {
		handleInputGame(deltaTime);
		level.update(deltaTime);
		score = level.score;
		if (scoreVisual < score) {
			scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);
		}
	}

	public boolean isGameOver () {
		return level.items.size() == 0;
	}
	
	private void handleInputGame(float deltaTime) {
		// me serviria si uso acelerometro o para obtener teclas apretadas sin
		// usar eventos..gdx.input
	}

	@Override
	public void dispose() {
	}
}