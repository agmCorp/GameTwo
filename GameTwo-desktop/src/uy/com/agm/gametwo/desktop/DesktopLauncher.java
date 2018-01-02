package uy.com.agm.gametwo.desktop;

import uy.com.agm.gametwo.GameTwoMain;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;

	public static void main(String[] arg) {
		if (rebuildAtlas) {
			Settings settings = new Settings();

			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings,
					"../../GameTwo-desktop/assets-raw/images", "images",
					"gametwo.pack");
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "GameTwo";
		config.useGL30 = false;
		config.width = 320;
		config.height = 480;

		new LwjglApplication(new GameTwoMain(), config);
	}
}
