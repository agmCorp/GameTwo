package uy.com.agm.gametwo.game;

import java.util.ArrayList;
import java.util.List;

import uy.com.agm.gametwo.game.objects.Item;
import uy.com.agm.gametwo.game.objects.Wall;
import uy.com.agm.gametwo.util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;

public class Level {
	private static final String TAG = Level.class.getName();
	public static final float LEVEL_WIDTH = Constants.VIEWPORT_WIDTH;
	// 2 Pantallas
	public static final float LEVEL_HEIGHT = Constants.VIEWPORT_HEIGHT * 2.0f;
	public static final int NUMBER_OF_ITEMS = 20;
	public final List<Item> items;
	public final List<Wall> walls;
	public int score;

	private void createItems() {
		int i;
		float randomX, randomY;
		Item item;

		for (i = 0; i < NUMBER_OF_ITEMS; i++) {
			// No sabemos de antemano el ancho del item por lo que puede quedar
			// una parte fuera del mundo
			randomX = MathUtils.random(0, LEVEL_WIDTH);
			randomY = MathUtils.random(Constants.VIEWPORT_HEIGHT, LEVEL_HEIGHT);
			item = new Item(randomX, randomY);
			item.state = Item.ITEM_STATE_INITIAL;
			items.add(item);
		}
	}

	private void createWall(float x, float y, float velX, float velY,
			int disposition) {
		Wall wall;

		wall = new Wall(x, y, disposition);
		wall.velocity.set(velX, velY);
		walls.add(wall);
	}

	private void createWallLeft() {
		float randomX, randomY, velY;

		randomX = 0 + Wall.WALL_WIDTH / 2;
		randomY = MathUtils.random(0 + Wall.WALL_HEIGHT / 2,
				Constants.VIEWPORT_HEIGHT - Wall.WALL_HEIGHT / 2);
		velY = MathUtils.random(5, 15) * MathUtils.randomSign();
		createWall(randomX, randomY, 0, velY, Wall.WALL_LEFT);

	}

	private void createWallRight() {
		float randomX, randomY, velY;

		randomX = Constants.VIEWPORT_WIDTH - Wall.WALL_WIDTH / 2;
		randomY = MathUtils.random(0 + Wall.WALL_HEIGHT / 2,
				Constants.VIEWPORT_HEIGHT - Wall.WALL_HEIGHT / 2);
		velY = MathUtils.random(5, 15) * MathUtils.randomSign();
		createWall(randomX, randomY, 0, velY, Wall.WALL_RIGHT);
	}

	private void createWallBottom() {
		float randomX, randomY, velX;

		randomX = MathUtils.random(0 + Wall.WALL_HEIGHT / 2,
				Constants.VIEWPORT_WIDTH - Wall.WALL_HEIGHT / 2);
		randomY = 0 + Wall.WALL_WIDTH / 2;
		velX = MathUtils.random(5, 15) * MathUtils.randomSign();
		createWall(randomX, randomY, velX, 0, Wall.WALL_BOTTOM);
	}

	private void createWallTop() {
		float randomX, randomY, velX;

		randomX = MathUtils.random(0 + Wall.WALL_HEIGHT / 2,
				Constants.VIEWPORT_WIDTH - Wall.WALL_HEIGHT / 2);
		randomY = Constants.VIEWPORT_HEIGHT - Wall.WALL_WIDTH / 2;
		velX = MathUtils.random(5, 15) * MathUtils.randomSign();
		createWall(randomX, randomY, velX, 0, Wall.WALL_TOP);
	}

	private void createWalls() {
		createWallLeft();
		createWallRight();
		createWallBottom();
		createWallTop();
	}

	private void generateLevel() {
		createItems();
		createWalls();
	}

	public Level() {
		items = new ArrayList<Item>();
		walls = new ArrayList<Wall>();
		score = 0;
		generateLevel();
	}

	public void update(float deltaTime) {
		// Hace update de todos los objetos del nivel y controla colisiones
		updateItems(deltaTime);
		updateWalls(deltaTime);
		checkItemsCollisions();
	}

	private void updateItems(float deltaTime) {
		for (Item item : items) {
			item.update(deltaTime);
		}
	}

	private void updateWalls(float deltaTime) {
		for (Wall wall : walls) {
			wall.update(deltaTime);
		}
	}

	private void checkItemsCollisions() {
		int i, len;
		Item item;
		boolean beyondLeftWall, beyondRigthWall, beyondBottomWall, beyondTopWall;

		len = items.size();
		for (i = 0; i < len; i++) {
			item = items.get(i);
			// Los items rebotan contra las paredes una vez que fueron tocados
			if (item.state == Item.ITEM_STATE_PLAYING) {
				for (Wall wall : walls) {
					if (Intersector.overlaps(item.bounds, wall.bounds)) {
						// Se corrige la posición para que en una próxima
						// evaluación no se detecte colisión nuevamente
						// Así se evita oscilar.
						switch (wall.disposition) {
						case Wall.WALL_LEFT:
							item.position.x = wall.position.x
									+ wall.bounds.width / 2
									+ item.bounds.radius;
							item.velocity.x *= -1;

							break;
						case Wall.WALL_RIGHT:
							item.position.x = wall.position.x
									- wall.bounds.width / 2
									- item.bounds.radius;
							item.velocity.x *= -1;
							break;
						case Wall.WALL_BOTTOM:
							item.position.y = wall.position.y
									+ wall.bounds.height / 2
									+ item.bounds.radius;
							item.velocity.y *= -1;
							break;
						case Wall.WALL_TOP:
							item.position.y = wall.position.y
									- wall.bounds.height / 2
									- item.bounds.radius;
							item.velocity.y *= -1;
							break;
						default:
							break;
						}
					}
				}
			}

			beyondLeftWall = item.position.x + item.dimension.x / 2 < 0;
			beyondRigthWall = item.position.x - item.dimension.x / 2 > Constants.VIEWPORT_WIDTH;
			beyondBottomWall = item.position.y + item.dimension.y / 2 < 0;
			beyondTopWall = item.position.y - item.dimension.y / 2 > Constants.VIEWPORT_HEIGHT;
			// Choques contra bordes de pantalla se elimina el item
			if ((beyondLeftWall && item.state == Item.ITEM_STATE_PLAYING)
					|| (beyondRigthWall && item.state == Item.ITEM_STATE_PLAYING)
					|| beyondBottomWall
					|| (beyondTopWall && item.state == Item.ITEM_STATE_PLAYING)) {

				if (item.state == Item.ITEM_STATE_PLAYING) {
					score += item.getScore();
				}
				items.remove(item);
				len = items.size();
				Gdx.app.debug(TAG, "len! " + len);
			}
		}
	}

	public void render(SpriteBatch batch) {
		renderItems(batch);
		renderWalls(batch);
	}

	public void renderDebug(ShapeRenderer shapeRenderer) {
		renderItemsDebug(shapeRenderer);
		renderWallsDebug(shapeRenderer);
	}

	private void renderItems(SpriteBatch batch) {
		for (Item item : items) {
			item.render(batch);
		}
	}

	private void renderWalls(SpriteBatch batch) {
		for (Wall wall : walls) {
			wall.render(batch);
		}
	}

	private void renderItemsDebug(ShapeRenderer shapeRenderer) {
		for (Item item : items) {
			item.renderDebug(shapeRenderer);
		}
	}

	private void renderWallsDebug(ShapeRenderer shapeRenderer) {
		for (Wall wall : walls) {
			wall.renderDebug(shapeRenderer);
		}
	}

}
