package com.thecubecast.reengine2.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thecubecast.ReEngine.serverclass2;

public class ServerLauncher2 {
	public static void main (String[] args) {
		
		//set variables from the settings file
		int[] Windowed_Size = {900,620};
		
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.fullscreen = false;
		config.width = Windowed_Size[0];
		config.height = Windowed_Size[1];
		
		config.addIcon("icon.png", null);
		config.title = "ReEngine Server";
		config.forceExit = false;
		new LwjglApplication(new serverclass2(), config);
	}
}
