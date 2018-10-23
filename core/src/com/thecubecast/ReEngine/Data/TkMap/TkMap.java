package com.thecubecast.ReEngine.Data.TkMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.worldObjects.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class TkMap {

    JsonParser jsonReaderthing;
    JsonObject MapObject;

    int Width;
    int Height;

    int TileSize;

    Texture pixel;
    TkTileset Tileset;

    int[][] Ground;
    int[][] Foreground;
    Boolean[][] Collision;

    public TkMap(String MapLocation) {
        pixel = new Texture(Gdx.files.internal("white-pixel.png"));
        jsonReaderthing = new JsonParser();
        MapObject = jsonReaderthing.parse(Gdx.files.internal(MapLocation).readString()).getAsJsonObject();

        Width = MapObject.get("Width").getAsInt();
        Height = MapObject.get("Height").getAsInt();

        TileSize = MapObject.get("Tilesets").getAsJsonArray().get(0).getAsJsonObject().get("TileSize").getAsJsonObject().get("Width").getAsInt();

        Tileset = new TkTileset(MapObject.get("Tilesets").getAsJsonArray().get(0).getAsJsonObject().get("Name").getAsString(),
                MapObject.get("Tilesets").getAsJsonArray().get(0).getAsJsonObject().get("FilePath").getAsString(),
                MapObject.get("Tilesets").getAsJsonArray().get(0).getAsJsonObject().get("TileSize").getAsJsonObject().get("Width").getAsInt(),
                MapObject.get("Tilesets").getAsJsonArray().get(0).getAsJsonObject().get("TileSize").getAsJsonObject().get("Height").getAsInt(),
                MapObject.get("Tilesets").getAsJsonArray().get(0).getAsJsonObject().get("TileSep").getAsInt()
                );

        Ground = new int[Width][Height];
        Foreground = new int[Width][Height];
        Collision = new Boolean[Width][Height];

        //Prepare BitString
        String PreparedBitString = MapObject.get("Ground").getAsJsonObject().get("text").getAsString().replace("\n", ",");
        PreparedBitString = PreparedBitString.replace(" ", "");

        String[] Bits = PreparedBitString.split(",");

        int index = 0;
        for (int y = Height-1; y >= 0; y--) {
            for (int x = 0; x < Width; x++) {

                Ground[x][y] = Integer.parseInt(Bits[index]);

                index++;
            }
        }

        //Prepare BitString
        PreparedBitString = MapObject.get("Foreground").getAsJsonObject().get("text").getAsString().replace("\n", ",");
        PreparedBitString = PreparedBitString.replace(" ", "");

        Bits = PreparedBitString.split(",");

        index = 0;
        for (int y = Height-1; y >= 0; y--) {
            for (int x = 0; x < Width; x++) {

                Foreground[x][y] = Integer.parseInt(Bits[index]);

                index++;
            }
        }

        //Prepare BitString
        PreparedBitString = MapObject.get("Collision").getAsJsonObject().get("text").getAsString().replace("\n", "");
        PreparedBitString = PreparedBitString.replace(" ", "");

        Bits = PreparedBitString.split("");

        index = 0;
        for (int y = Height-1; y >= 0; y--) {
            for (int x = 0; x < Width; x++) {

                if(Bits[index].equals("1"))
                    Collision[x][y] = true;
                else
                    Collision[x][y] = false;

                index++;
            }
        }

    }

    public JsonObject getMapObject() {
        return MapObject;
    }

    public int getWidth() {
        return Width;
    }

    public int getHeight() {
        return Height;
    }

    public int getTileSize() {
        return TileSize;
    }

    public int[][] getGround() {
        return Ground;
    }

    public int[][] getForeground() {
        return Foreground;
    }

    public Boolean[][] getCollision() {
        return Collision;
    }

    public void Draw(OrthographicCamera cam, SpriteBatch batch) {

        Rectangle drawView;
        if (cam != null) {
            drawView = new Rectangle(cam.position.x - cam.viewportWidth, cam.position.y - cam.viewportHeight, cam.viewportWidth + cam.viewportWidth, cam.viewportHeight + cam.viewportHeight);
        } else {
            drawView = new Rectangle(0, 0, Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4);
        }

        //Draw the Ground
        for (int y = Height-1; y >= 0; y--) {
            for (int x = 0; x < Width; x++) {
                if (Ground[x][y] != -1) {
                    if(drawView.overlaps(new Rectangle(x*16, y*16, 16, 16))) {
                        batch.draw(Tileset.Tiles[Ground[x][y]], x * 16, y * 16);
                    }
                }
            }
        }
        //Draw the Foreground
        for (int y = Height-1; y >= 0; y--) {
            for (int x = 0; x < Width; x++) {
                if (Foreground[x][y] != -1) {
                    if(drawView.overlaps(new Rectangle(x*16, y*16, 16, 16))) {
                        batch.draw(Tileset.Tiles[Foreground[x][y]], x * 16, y * 16);
                    }
                }
            }
        }
    }

    public void DrawCollision(OrthographicCamera cam, SpriteBatch batch) {

        Rectangle drawView;
        if (cam != null) {
            drawView = new Rectangle(cam.position.x - cam.viewportWidth, cam.position.y - cam.viewportHeight, cam.viewportWidth + cam.viewportWidth, cam.viewportHeight + cam.viewportHeight);
        } else {
            drawView = new Rectangle(0, 0, Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4);
        }

        for (int y = Height-1; y >= 0; y--) {
            for (int x = 0; x < Width; x++) {

                if (Collision[x][y]) {
                    if(drawView.overlaps(new Rectangle(x*16, y*16, 16, 16))) {
                        batch.draw(pixel, x * 16, y * 16, 16, 16);
                    }
                } else {
                    if(drawView.overlaps(new Rectangle(x*16, y*16, 16, 16))) {
                        //batch.draw(pixel, x * 16, y * 16, 16, 16);
                    }
                }
            }
        }
    }

    //Returns the objects that were in the map file
    public ArrayList<WorldObject> getObjects() {
        ArrayList<WorldObject> temp = new ArrayList<>();
        JsonArray temparray = getMapObject().get("Objects").getAsJsonArray();
        for (int i = 0; i < temparray.size(); i++) {
            int X,Y,Z,W,H,D;
            JsonObject tempObject = temparray.get(i).getAsJsonObject();
            X = tempObject.get("x").getAsInt();
            Y = tempObject.get("y").getAsInt();
            Z = tempObject.get("z").getAsInt();
            W = tempObject.get("Width").getAsInt();
            H = tempObject.get("Height").getAsInt();
            D = tempObject.get("Depth").getAsInt();
            String tempImgLoc = tempObject.get("TexLocation").getAsString();
            WorldObject.type Type;
            boolean Collidable = false;
            if (tempObject.get("Physics").getAsString().equals("Static")) {
                Type = WorldObject.type.Static;
                if (tempObject.get("Collidable").getAsBoolean())
                    Collidable = true;
            } else if (tempObject.get("Physics").getAsString().equals("Dynamic")){
                Type = WorldObject.type.Dynamic;
            } else { Type = WorldObject.type.Static;}
            WorldObject tempObj = new WorldObject(X, Y, Z, new Vector3(W,H,D), Type, Collidable) {
                Texture Image = new Texture(Gdx.files.internal(tempImgLoc));
                @Override
                public void init(int Width, int Height) {

                }

                @Override
                public void update(float delta, List<Cube> Colls) {

                }

                @Override
                public void draw(SpriteBatch batch, float Time) {
                    batch.draw(Image, getPosition().x, getPosition().y);
                }
            };

            temp.add(tempObj);
        }
        return temp;
    }

    //Returns the Areas that were in the map file
    public void getAreas() {

    }

    public String SerializeMap(ArrayList<WorldObject> entities) {
        return null;
    }

}
