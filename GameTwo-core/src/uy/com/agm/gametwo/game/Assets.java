package uy.com.agm.gametwo.game;

import java.util.ArrayList;
import java.util.List;

import uy.com.agm.gametwo.util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {

	private static final String TAG = Assets.class.getName();

	public static final Assets instance = new Assets();

	private AssetManager assetManager;

	public AssetFonts fonts;

	public AssetItem item;

	public AssetWall wall;

	public AssetMusic music;

	// singleton: prevent instantiation from other classes
	private Assets() {
	}

	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's built-in 15px bitmap font
			defaultSmall = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			// set font sizes
			defaultSmall.setScale(0.75f);
			defaultNormal.setScale(1.0f);
			defaultBig.setScale(6f);
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	public class AssetItem {
		public final List<AtlasRegion> items;

		public AssetItem(TextureAtlas atlas) {
			items = new ArrayList<AtlasRegion>();
			int n = Constants.MAX_TEXTURE_ITEMS;
			for (int i = 0; i < n; i++) {
				items.add(atlas.findRegion("item" + i));
			}
		}
	}

	public class AssetWall {
		public final AtlasRegion wall;

		public AssetWall(TextureAtlas atlas) {
			wall = atlas.findRegion("wall");
		}
	}

	public class AssetMusic {
		public final Music song01;

		public AssetMusic(AssetManager am) {
			song01 = am.get("music/keith303_-_brand_new_highscore.mp3",
					Music.class);
		}
	}

	public void init(AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// load music
		assetManager.load("music/keith303_-_brand_new_highscore.mp3",
				Music.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();

		Gdx.app.debug(TAG,
				"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String name : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + name);
		}

		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

		// enable texture filtering for pixel smoothing
		for (Texture textures : atlas.getTextures()) {
			textures.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		// create game resource objects
		fonts = new AssetFonts();
		item = new AssetItem(atlas);
		wall = new AssetWall(atlas);
		music = new AssetMusic(assetManager);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'",
				(Exception) throwable);
	}

}
