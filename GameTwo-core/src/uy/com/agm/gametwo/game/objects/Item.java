package uy.com.agm.gametwo.game.objects;

import uy.com.agm.gametwo.game.Assets;
import uy.com.agm.gametwo.util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public class Item extends AbstractGameObject {
	private static final String TAG = Item.class.getName();

	public static final int ITEM_STATE_INITIAL = 0;
	public static final int ITEM_STATE_PLAYING = 1;
	private static final int SCORE = 15;
	private TextureRegion regItem;
	private float angularVelocity;
	public Circle bounds;
	public int state;

	public void setCaidaLibre() {
		velocity.set(0, -3.0f);     // Velocidad inicial de caída
		terminalVelocity.x = 0;     // Debe ser siempre un número positivo
		terminalVelocity.y = 5.5f;  // Debe ser siempre un número positivo,
									// máxima velocidad caída libre
	}
	
	public void freeze() {
		velocity.set(0, 0);     
		terminalVelocity.x = 0;  
		terminalVelocity.y = 0;  
	}
	
	public Item(float x, float y) {
		super();
		float dim = MathUtils.random(1.0f, 2.0f);
		dimension.set(dim, dim);
		position.set(x, y); // Centro del objeto
		origin.set(dimension.x / 2, dimension.y / 2);

		// Posición rotada inicial en que es dibujado el objeto
		rotation = MathUtils.random(0, 360);
		angularVelocity = MathUtils.random(0, 360) * MathUtils.randomSign();
		setCaidaLibre();

		int item = MathUtils.random(0, Constants.MAX_TEXTURE_ITEMS - 1);
		regItem = Assets.instance.item.items.get(item);
		bounds = new Circle(x, y, dim / 2);
		state = ITEM_STATE_INITIAL;
	}

	public void render(SpriteBatch batch) {
		batch.draw(regItem.getTexture(), position.x - origin.x, position.y
				- origin.y, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, regItem.getRegionX(),
				regItem.getRegionY(), regItem.getRegionWidth(),
				regItem.getRegionHeight(), false, false);
	}
	
	public void renderDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.circle(bounds.x, bounds.y, bounds.radius);
	}
	

	public int getScore() {
		return SCORE;
	}

	public float getAngularVelocity() {
		// Grados por segundo
		return angularVelocity;
	}

	public void update(float deltaTime) {
		super.update(deltaTime);
		// Movemos su círculo de colisiones
		bounds.setPosition(position);
	}
}
