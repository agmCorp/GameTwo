package uy.com.agm.gametwo.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import uy.com.agm.gametwo.GameTwoMain;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		// Para ahorrar batería
		config.useAccelerometer = false;
		config.useCompass = false;
		config.hideStatusBar = true;
		initialize(new GameTwoMain(), config);
	}
}
