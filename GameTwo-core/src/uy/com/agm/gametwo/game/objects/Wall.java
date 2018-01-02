package uy.com.agm.gametwo.game.objects;

import uy.com.agm.gametwo.game.Assets;
import uy.com.agm.gametwo.util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Wall extends AbstractGameObject {
	private static final String TAG = Wall.class.getName();
	
	public static float WALL_WIDTH = 0.3f;
	public static float WALL_HEIGHT = 6.0f;
	public static final int WALL_LEFT = 0;
	public static final int WALL_RIGHT = 1;
	public static final int WALL_TOP = 2;
	public static final int WALL_BOTTOM = 3;
	private TextureRegion regWall;
	public Rectangle bounds;
	public int disposition;

	public Wall(float x, float y, int disposition) {
		super();
		// Su rectángulo de colisiones se define en función de su disposición.
		if (disposition == WALL_LEFT || disposition == WALL_RIGHT ) {
			bounds = new Rectangle(x - WALL_WIDTH / 2, y - WALL_HEIGHT / 2,
					WALL_WIDTH, WALL_HEIGHT);
		} else {
			bounds = new Rectangle(x - WALL_HEIGHT / 2, y - WALL_WIDTH / 2,
					WALL_HEIGHT, WALL_WIDTH);
			// Se dibuja rotada 90 grados.
			rotation = 90;
		}
		// Se considera la pared como un rectángulo en donde su altura es mayor que su ancho siempre.
		dimension.set(WALL_WIDTH, WALL_HEIGHT);
		position.set(x, y); // Centro del objeto
		origin.set(dimension.x / 2, dimension.y / 2);
		terminalVelocity.x = 20f; // Debe ser siempre un número positivo
		terminalVelocity.y = 20f; // Debe ser siempre un número positivo
		regWall = Assets.instance.wall.wall;
		this.disposition = disposition;
	}

	public void render(SpriteBatch batch) {
		batch.draw(regWall.getTexture(), position.x - origin.x, position.y
				- origin.y, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, regWall.getRegionX(),
				regWall.getRegionY(), regWall.getRegionWidth(),
				regWall.getRegionHeight(), false, false);
	}

	public void renderDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void update(float deltaTime) {
		super.update(deltaTime);

		// Movemos su rectángulo de colisiones
		bounds.setCenter(position);

		// Evita que el objeto se vaya del mundo, haciéndolo rebotar.
		if (position.x - bounds.width / 2 < 0) {
			position.x = bounds.width / 2;
			velocity.x *= -1;
		}
		if (position.x + bounds.width / 2 > Constants.VIEWPORT_WIDTH) {
			position.x = Constants.VIEWPORT_WIDTH - bounds.width / 2;
			velocity.x *= -1;
		}
		if (position.y - bounds.height / 2 < 0) {
			position.y = bounds.height / 2;
			velocity.y *= -1;
		}
		if (position.y + bounds.height / 2 > Constants.VIEWPORT_HEIGHT) {
			position.y = Constants.VIEWPORT_HEIGHT - bounds.height / 2;
			velocity.y *= -1;
		}
		
		// Cambio aleatorio de sentido (2% de probabilidad)
		if (MathUtils.randomBoolean(0.02f)) {
			velocity.x *= -1;
			velocity.y *= -1;
		}
	}

	public float getAngularVelocity() {
		// Grados por segundo
		return 0;
	}
}
