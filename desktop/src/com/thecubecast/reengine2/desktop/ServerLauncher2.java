package com.thecubecast.reengine2.desktop;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.thecubecast.ReEngine.serverclass2;

public class ServerLauncher2 {
	public static void main (String[] args) {
		
		HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
	    new HeadlessApplication(new serverclass2(), config);
	}
}
