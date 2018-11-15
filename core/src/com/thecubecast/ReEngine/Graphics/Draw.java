package com.thecubecast.ReEngine.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Draw {

	public AssetManager manager = new AssetManager();

	//Animation Variables
	public Animation<TextureRegion> LoadingAnimation; // Must declare frame type (TextureRegion)
	Texture LoadingSheet;

	public static ShaderProgram OutlineShader;
	public static ShaderProgram FillColorShader;
	
	BitmapFont font = new BitmapFont(Gdx.files.internal("Fonts/Pixel.fnt"), new TextureRegion(new Texture(Gdx.files.internal("Fonts/Pixel.png"))));

	public void LoadShaders() {
		String OutlineShadervertexShader = Gdx.files.internal("Shaders/Outline/vertex.glsl").readString();
		String OutlineShaderfragmentShader = Gdx.files.internal("Shaders/Outline/fragment.glsl").readString();
		OutlineShader = new ShaderProgram(OutlineShadervertexShader,OutlineShaderfragmentShader);

		String FillColorShadervertexShader = Gdx.files.internal("Shaders/Fill_Color/vertex.glsl").readString();
		String FillColorShaderfragmentShader = Gdx.files.internal("Shaders/Fill_Color/fragment.glsl").readString();
		FillColorShader = new ShaderProgram(FillColorShadervertexShader,FillColorShaderfragmentShader);

	}

	public void Init() {
		font.getData().markupEnabled = true;
		LoadShaders();

		// Initialize the Animation with the frame interval and array of frames
		LoadingAnimation = new Animation<TextureRegion>(0.1f, loadAnim(LoadingSheet, "cube_loading_sprite.png", 4, 1));
	}
	
	public void Load() {
		//The loops bellow grab the tiles and add them to the variable

	}

	public void LoadVariables() {
		/*
		//The loops bellow grab the tiles and add them to the variable
		for(int i=0; i < Tiles.length; ++i){
			if (i >= 10) {
				try {
					//Common.print("Loaded images /Sprites/"+ Integer.toString(i) +".png");
					Tiles[i] = manager.get("Sprites/oldTiles/megaminer_"+ Integer.toString(i) +".png", Texture.class);
				}
				catch(Exception e) {
					//e.printStackTrace();
				}
			} else {
				try {
					//Common.print("Loaded images /Sprites/0"+ Integer.toString(i) +".png");
					Tiles[i] = manager.get("Sprites/oldTiles/megaminer_0"+ Integer.toString(i) +".png", Texture.class);
				}
				catch(Exception e) {
					//e.printStackTrace();
				}
			}
		}
		for(int i=0; i < Images.length; ++i){
			if (i >= 10) {
				try {
					//Common.print("Loaded images /Images/"+ Integer.toString(i) +".png");
					Images[i] = manager.get("Images/image_"+ Integer.toString(i) +".png", Texture.class);
				}
				catch(Exception e) {
					//e.printStackTrace();
				}
			} else {
				try {
					//Common.print("Loaded images /Images/0"+ Integer.toString(i) +".png");
					Images[i] = manager.get("Images/image_0"+ Integer.toString(i) +".png", Texture.class);
				}
				catch(Exception e) {
					//e.printStackTrace();
				}
			}
		}
		for(int i=0; i < GUI.length; ++i){
			if (i >= 10) {
				try {
					//Common.print("Loaded GUI images Sprites/GUI/GUI_"+ Integer.toString(i) +".png");
					GUI[i] = manager.get("Sprites/GUI/GUI_"+ Integer.toString(i) +".png", Texture.class);
				}
				catch(Exception e) {
					//e.printStackTrace();
				}
			} else {
				try {
					//Common.print("Loaded GUI images Sprites/GUI/GUI_0"+ Integer.toString(i) +".png");
					GUI[i] = manager.get("Sprites/GUI/GUI_0"+ Integer.toString(i) +".png", Texture.class);
				}
				catch(Exception e) {
					//e.printStackTrace();
				}
			}
		}*/
	}

	public static TextureRegion[] loadAnim(Texture TexSheet, String FileLocation, int Cols, int Rows) {
		// Load the sprite sheet as a Texture
		TexSheet = new Texture(Gdx.files.internal(FileLocation));

		// Use the split utility method to create a 2D array of TextureRegions. This is 
		// possible because this sprite sheet contains frames of equal size and they are 
		// all aligned.
		TextureRegion[][] tmp = TextureRegion.split(TexSheet, 
				TexSheet.getWidth() / Cols,
				TexSheet.getHeight() / Rows);

		// Place the regions into a 1D array in the correct order, starting from the top 
		// left, going across first. The Animation constructor requires a 1D array.
		TextureRegion[] walkFrames = new TextureRegion[Cols * Rows];
		int index = 0;
		for (int i = 0; i < Rows; i++) {
			for (int j = 0; j < Cols; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}

		return walkFrames;
	}
	
	public void DrawAnimatedTile(SpriteBatch buffer, Animation<TextureRegion> animation_, int x, int y, float stateTime) {
		// Get current frame of animation for the current stateTime
		TextureRegion currentFrame = animation_.getKeyFrame(stateTime, true);
		
		buffer.draw(currentFrame, x, y);
	}
	
	public void DrawSplash(SpriteBatch buffer, int ID, int x, int y, float x2, float y2, boolean centered) { // The x2 and y2 is Percentage of 100
		/*
			int SizeX = Images[ID].getDiffuse().getWidth();
			int SizeY = Images[ID].getDiffuse().getHeight();

			if (centered) {
				buffer.draw(Images[ID].getDiffuse(), x-((SizeX * x2)/2), y-((SizeY * y2)/2), SizeX * x2, SizeY * y2);
			}
			else {
				buffer.draw(Images[ID].getDiffuse(), x, y, SizeX * x2, SizeY * y2);
			}*/
	}
	
	public void DrawAny(SpriteBatch buffer, int ID, String Type, int x, int y) {
		/*
		if(Type.equals("Tiles")) {
			buffer.draw(Tiles[ID], x, y);
		}
		if(Type.equals("Images")) {
			buffer.draw(Images[ID], x, y);	
		}
		if(Type.equals("Gui")) {
			buffer.draw(GUI[ID], x, y);	
		}
		*/
	}
	
	public void GUIDrawText(SpriteBatch buffer, int PosX, int PosY, String Text) {
		font.draw(buffer, Text, PosX , PosY);
	}

	public void GUIDrawText(SpriteBatch buffer, int PosX, int PosY, String Text, Color color) {
		font.setColor(color);
		font.draw(buffer, Text, PosX , PosY);
		font.setColor(Color.WHITE);
	}


	//The GUI or Menu would go here.
	public void GUIDeco(SpriteBatch buffer, int PosX, int PosY, String Text) {
		/*
		buffer.draw(GUI[00], PosX, PosY);
		buffer.draw(GUI[01], PosX + GUI[00].getWidth(), PosY);
		buffer.draw(GUI[01], PosX + (GUI[00].getWidth()*2), PosY);
		buffer.draw(GUI[01], PosX + (GUI[00].getWidth()*3), PosY);
		buffer.draw(GUI[01], PosX + (GUI[00].getWidth()*4), PosY);
		buffer.draw(GUI[02], PosX + (GUI[00].getWidth()*5), PosY);
		font.draw(buffer, "testing GUI - " + Text, PosX + GUI[00].getWidth(), PosY + (GUI[00].getHeight()/2));
		*/
	}
	
	public void HUDAchievement(SpriteBatch buffer, int PosX, int PosY, String text, int iconID, float Opacity, boolean Anim, float Time) {
		/*
		buffer.draw(GUI[00], PosX, PosY);
		buffer.draw(GUI[01], PosX + Tiles[59].getWidth(), PosY);
		buffer.draw(GUI[02], PosX + (Tiles[59].getWidth()*2), PosY);
		if (Anim) {
			if (iconID == 4 || iconID == 5 || iconID == 6) {
				//DrawAnimatedTile(buffer, StarsAnimation, PosX + (Tiles[59].getWidth()*2) + (Tiles[59].getWidth()/2), PosY + (Tiles[59].getWidth()/2)-(Tiles[59].getWidth()/8), Time);
			} else {
				Common.print("Not Configured To Animate");
			}
		} else {
			buffer.draw(Tiles[iconID], PosX + (Tiles[59].getWidth()*2) + (Tiles[59].getWidth()/2), PosY + (Tiles[59].getWidth()/2)-(Tiles[59].getWidth()/8), Tiles[iconID].getWidth()/4, Tiles[iconID].getHeight()/4);
		}
		//buffer.draw(Tiles[iconID], PosX, PosY);
		font.draw(buffer, text, PosX + 20, PosY + (Tiles[59].getHeight()/2)+5);
		*/
	}

	public static void setOutlineShaderColor(Color outlineColor) {
		OutlineShader.setUniform1fv("outline_Color", new float[] { outlineColor.r, outlineColor.g, outlineColor.b, outlineColor.a }, 0, 4);
	}

	public static void setOutlineShaderColor(Color outlineColor, float Alpha) {
		OutlineShader.setUniform1fv("outline_Color", new float[] { outlineColor.r, outlineColor.g, outlineColor.b, Alpha }, 0, 4);
	}

	public static void setFillColorShaderColor(Color outlineColor) {
		FillColorShader.begin();
		FillColorShader.setUniform4fv("outline_Color", new float[] { outlineColor.r, outlineColor.g, outlineColor.b, outlineColor.a }, 0, 4);
		FillColorShader.end();
	}

	public static void setFillColorShaderColor(Color outlineColor, float Alpha) {
		FillColorShader.begin();
		FillColorShader.setUniform4fv("outline_Color", new float[] { outlineColor.r, outlineColor.g, outlineColor.b, Alpha }, 0, 4);
		FillColorShader.end();
	}

	public ShapeRenderer debugRenderer = new ShapeRenderer();

    public void DrawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public void DrawDebugPoint(Vector2 start, int lineWidth, Color color, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.circle(start.x, start.y, lineWidth);
        debugRenderer.setColor(color);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
	
}