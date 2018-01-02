package uy.com.agm.gametwo;

import uy.com.agm.gametwo.game.Assets;
import uy.com.agm.gametwo.screens.DirectedGame;
import uy.com.agm.gametwo.screens.GameScreen;
import uy.com.agm.gametwo.screens.transitions.ScreenTransition;
import uy.com.agm.gametwo.screens.transitions.ScreenTransitionSlice;
import uy.com.agm.gametwo.util.AudioManager;
import uy.com.agm.gametwo.util.GamePreferences;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;

public class GameTwoMain extends DirectedGame {
	private static final String TAG = GameTwoMain.class.getName();

	@Override
	public void create() {
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Load assets
		Assets.instance.init(new AssetManager());

		// Load preferences for audio settings and start playing music
		GamePreferences.instance.load();
		AudioManager.instance.play(Assets.instance.music.song01);

		// Start game
		ScreenTransition transition = ScreenTransitionSlice.init(2,
				ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);

		setScreen(new GameScreen(this), transition);
	}
}
