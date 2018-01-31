package com.thecubecast.reengine2.desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.mainclass;

public class DesktopLauncher {
	public static void main (String[] args) {



		//set variables from the settings file
		boolean Display_Fullscreen = false;
		boolean Display_WindowedFull = false;
		int[] Windowed_Size = {900,620};
		
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration ();

		config.setWindowListener(new Lwjgl3WindowListener() {

			@Override
			public void iconified(boolean isIconified) {
				//Minimized
				Common.print("Minimized Window");
			}

			@Override
			public void maximized(boolean isMaximized) {

			}

			@Override
			public void focusLost() {

			}

			@Override
			public void focusGained() {

			}

			@Override
			public boolean closeRequested() {
				Common.print("clicked X");
				Common.ProperShutdown();
				return true;
			}

			@Override
			public void filesDropped(String[] files) {

			}

			@Override
			public void refreshRequested() {

			}
		});

		if (Display_Fullscreen) { //Fullscreen
			// get the current display mode of the monitor the window is on
			Graphics.DisplayMode primaryDesktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
			// set the window to fullscreen mode
			config.setFullscreenMode(primaryDesktopMode);
			//Gdx.graphics.setFullscreenMode(mode);
		} else {
			if (Display_WindowedFull) { //Windowed Fullscreen
				config.setMaximized(true);
				config.setDecorated(false);
				Common.print("Windowed-fullscreen");
			} else { //Windowed
				Common.print("Windowed");
				config.setMaximized(true);
			}
		}
		config.setWindowIcon("icon.png");
		config.setTitle("ReEngine");
		new Lwjgl3Application(new mainclass(), config);
	}
	
	public static int GetMonitorSizeW() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return gd.getDisplayMode().getWidth();
	}
	public static int GetMonitorSizeH() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return gd.getDisplayMode().getHeight();
	}
}
