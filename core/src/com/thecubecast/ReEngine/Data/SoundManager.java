package com.thecubecast.ReEngine.Data;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
	
	public float MasterVolume = 1.0f;
	public float MusicVolume = 0.4f;
	public float SoundVolume = 0.8f;

	//Define all sound objects
	Sound Click;
	Sound DrilIdle;
	Sound DrilMove;
	Sound CashGet;
	
	//Define all your music objects
	Music eightbitDiggerAudio;
	Boolean eightbitDigger = false;
	Music WindAudio;
	Boolean Wind = false;
	Music RainAudio;
	Boolean Rain = false;
	
	Map<String, Long> SoundIds = new HashMap<String, Long>();
	
	public void init() { //Create the folders that hold everything neatly
		
		Click = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/menu-clik.wav"));
		DrilIdle = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/menu-clik.wav"));
		DrilMove = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/menu-clik.wav"));
		CashGet = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/gain-cash.wav"));
		
	}
	
	public void update() {
		if (eightbitDigger) {
			eightbitDiggerAudio.setVolume(MusicVolume*MasterVolume);
		}
		if (Wind) {
			WindAudio.setVolume(MusicVolume*MasterVolume);
		}
	}
	
	public void play(String soundName) {
		if(soundName.equals("Click")) {
			long temp = Click.play(SoundVolume*MasterVolume);
			SoundIds.put(soundName, temp);
		}
		if(soundName.equals("CashGet")) {
			long temp = CashGet.play(SoundVolume*MasterVolume);
			SoundIds.put(soundName, temp);
		}
	}
	
	public void pause(String soundName) {
		if(soundName.equals("Click")) {
			long temp = SoundIds.get(soundName);
			Click.pause(temp);
		}
	}
	
	public void stop(String soundName) {
		if(soundName.equals("Click")) {
			long temp = SoundIds.get(soundName);
			Click.stop(temp);
		}
	}
	
	public void playMusic (String Music, boolean looping) {
		if(Music.equals("8-bit-Digger")) {
			eightbitDigger = true;
			eightbitDiggerAudio = Gdx.audio.newMusic(Gdx.files.internal("Music/The-8-Bit-Digger.wav"));
			eightbitDiggerAudio.play();
			eightbitDiggerAudio.setVolume(MusicVolume*MasterVolume);
			if(looping) {
				eightbitDiggerAudio.setLooping(looping);
			}
		} else if(Music.equals("Wind")) {
			Wind = true;
			WindAudio = Gdx.audio.newMusic(Gdx.files.internal("Music/wind.wav"));
			WindAudio.play();
			WindAudio.setVolume(MusicVolume*MasterVolume);
			if(looping) {
				WindAudio.setLooping(looping);
			}
		}
		else if(Music.equals("Rain")) {
			Rain = true;
			RainAudio = Gdx.audio.newMusic(Gdx.files.internal("Music/rain.wav"));
			RainAudio.play();
			RainAudio.setVolume(MusicVolume*MasterVolume);
			if(looping) {
				RainAudio.setLooping(looping);
			}
		}
	}
	
	public boolean isPlaying(String Music) {
		if(Music.equals("8-bit-Digger")) {
			if(eightbitDiggerAudio.isPlaying()) {
				return true;
			} else {
				return false;
			}
		} else if(Music.equals("Wind")) {
			if(WindAudio.isPlaying()) {
				return true;
			} else {
				return false;
			}
		} else if(Music.equals("Rain")) {
			if(RainAudio.isPlaying()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void pauseMusic (String Music) {
		if(Music.equals("8-bit-Digger")) {
			eightbitDiggerAudio.pause();
		} else if(Music.equals("Wind")) {
			WindAudio.pause();
		} else if(Music.equals("Rain")) {
			RainAudio.pause();
		}
	}
	
	public void stopMusic (String Music) {
		if(Music.equals("8-bit-Digger")) {
			eightbitDigger = false;
			eightbitDiggerAudio.stop();
			eightbitDiggerAudio.dispose();
		} else if(Music.equals("Wind")) {
			Wind = false;
			WindAudio.stop();
			WindAudio.dispose();
		} else if(Music.equals("Wind")) {
			Rain = false;
			RainAudio.stop();
			RainAudio.dispose();
		}
	}
	
}