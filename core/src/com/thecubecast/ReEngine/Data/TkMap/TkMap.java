package com.thecubecast.ReEngine.Data.TkMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.google.gson.*;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.worldObjects.*;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.OutlineShader;
import static com.thecubecast.ReEngine.Graphics.Draw.setOutlineShaderColor;

public class TkMap {

    JsonParser jsonReaderthing;
    JsonObject MapObject;

    int Width;
    int Height;

    int TileSize;

    private Texture pixel;
    public TkTileset Tileset;

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

    public TkMap(int Width, int Height, int TileSize) {

        this.Width = Width;
        this.Height = Height;
        this.TileSize = TileSize;

        Tileset = new TkTileset("World","Sprites/Map/World.png", TileSize, TileSize, 0);

        Ground = new int[Width][Height];
        Foreground = new int[Width][Height];
        Collision = new Boolean[Width][Height];

        for (int y = this.getHeight()-1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                this.Ground[x][y] = 150;
            }
        }

        //----------------------------------------------------

        for (int y = this.getHeight()-1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                this.Foreground[x][y] = -1;
            }
        }

        //---------------------------------------------------

        for (int y = this.getHeight()-1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                this.Collision[x][y] = false;
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

    public void setGroundCell(int x, int y, int ID) {
        Ground[x][y] = ID;
    }

    public void setForegroundCell(int x, int y, int ID) {
        Foreground[x][y] = ID;
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
        if (getMapObject() == null) {
            return temp;
        }
        JsonArray temparray = getMapObject().get("Objects").getAsJsonArray();
        for (int i = 0; i < temparray.size(); i++) {
            int X,Y,Z,W,H,D,OffsetX,OffsetY,OffsetZ;
            JsonObject tempObject = temparray.get(i).getAsJsonObject();
            X = tempObject.get("x").getAsInt();
            String Name = tempObject.get("Name").getAsString();
            String Description = tempObject.get("Description").getAsString();
            Y = tempObject.get("y").getAsInt();
            Z = tempObject.get("z").getAsInt();
            W = tempObject.get("Width").getAsInt();
            H = tempObject.get("Height").getAsInt();
            D = tempObject.get("Depth").getAsInt();
            OffsetX = tempObject.get("WidthOffset").getAsInt();
            OffsetY = tempObject.get("HeightOffset").getAsInt();
            OffsetZ = tempObject.get("DepthOffset").getAsInt();
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

            if (tempObject.get("Operation").getAsString().equals("Chest")) {
                Storage tempObj = new Storage(X, Y, Z, new Vector3(W,H,D), Type, Collidable) {
                    Texture Image = new Texture(Gdx.files.internal(tempImgLoc));
                    @Override
                    public void init(int Width, int Height) {

                    }

                    @Override
                    public void update(float delta, List<Cube> Colls) {

                    }

                    @Override
                    public void draw(SpriteBatch batch, float Time) {
                        if (Highlight) {
                            batch.flush();
                            batch.setShader(OutlineShader);
                            setOutlineShaderColor(this.HighlightColor, 0.8f);
                            batch.draw(Image, getPosition().x, getPosition().y);
                            batch.setShader(null);
                        } else {
                            batch.draw(Image, getPosition().x, getPosition().y);
                        }
                    }

                    @Override
                    public BoundingBox getImageHitbox() {
                        BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(Image.getWidth(), Image.getHeight(), 0).add(this.getPosition()));
                        return temp;
                    }
                };
                tempObj.setTexLocation(tempImgLoc);
                tempObj.Name = Name;
                tempObj.Description = Description;

                tempObj.setHitboxOffset(new Vector3(OffsetX,OffsetY,OffsetZ));

                temp.add(tempObj);
            } else if (tempObject.get("Operation").getAsString().equals("Chop")) {
                Chop tempObj = new Chop(X, Y, Z, new Vector3(W,H,D), Type, Collidable) {
                    Texture Image = new Texture(Gdx.files.internal(tempImgLoc));
                    @Override
                    public void init(int Width, int Height) {

                    }

                    @Override
                    public void draw(SpriteBatch batch, float Time) {
                        batch.draw(Image, getPosition().x + xoffset, getPosition().y);
                    }

                    @Override
                    public BoundingBox getImageHitbox() {
                        BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(Image.getWidth(), Image.getHeight(), 0).add(this.getPosition()));
                        return temp;
                    }
                };
                tempObj.setTexLocation(tempImgLoc);
                tempObj.Name = Name;
                tempObj.Description = Description;

                tempObj.setHitboxOffset(new Vector3(OffsetX,OffsetY,OffsetZ));

                temp.add(tempObj);
            } else if (tempObject.get("Operation").getAsString().equals("Mine")) {
                Mine tempObj = new Mine(X, Y, Z, new Vector3(W,H,D), Type, Collidable) {
                    Texture Image = new Texture(Gdx.files.internal(tempImgLoc));
                    @Override
                    public void init(int Width, int Height) {
                        /*this.drops[0][0] =  tempObject.get("x").getAsInt();
                        this.drops[0][1] = ;
                        this.drops[1][0] = ;
                        this.drops[1][1] = ;
                        this.drops[2][0] = ;
                        this.drops[2][1] = ;*/
                    }

                    @Override
                    public void draw(SpriteBatch batch, float Time) {
                        batch.draw(Image, getPosition().x + xoffset, getPosition().y);
                    }

                    @Override
                    public BoundingBox getImageHitbox() {
                        BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(Image.getWidth(), Image.getHeight(), 0).add(this.getPosition()));
                        return temp;
                    }
                };
                tempObj.setTexLocation(tempImgLoc);
                tempObj.Name = Name;
                tempObj.Description = Description;

                tempObj.setHitboxOffset(new Vector3(OffsetX,OffsetY,OffsetZ));

                temp.add(tempObj);
            } else {
                Interactable tempObj = new Interactable(X, Y, Z, new Vector3(W,H,D), Type, Collidable) {

                    Texture Image = new Texture(Gdx.files.internal(tempImgLoc));
                    @Override
                    public void init(int Width, int Height) {

                    }

                    @Override
                    public void update(float delta, List<Cube> Colls) {

                    }

                    @Override
                    public void draw(SpriteBatch batch, float Time) {
                        if (Highlight) {
                            batch.flush();
                            batch.setShader(OutlineShader);
                            setOutlineShaderColor(this.HighlightColor, 0.8f);
                            batch.draw(Image, getPosition().x, getPosition().y);
                            batch.setShader(null);
                        } else {
                            batch.draw(Image, getPosition().x, getPosition().y);
                        }
                    }

                    @Override
                    public BoundingBox getImageHitbox() {
                        BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(Image.getWidth(), Image.getHeight(), 0).add(this.getPosition()));
                        return temp;
                    }
                };
                tempObj.setTexLocation(tempImgLoc);
                tempObj.Name = Name;
                tempObj.Description = Description;

                tempObj.setHitboxOffset(new Vector3(OffsetX,OffsetY,OffsetZ));

                temp.add(tempObj);
            }
        }
        return temp;
    }

    //Returns the Areas that were in the map file
    public void getAreas() {

    }

    public String SerializeMap(List<WorldObject> entities) {
        JsonObject Output = new JsonObject();
        Output.addProperty("Width", this.getWidth());
        Output.addProperty("Height", this.getHeight());

        JsonArray Tilesets = new JsonArray();
            JsonObject TilesetObject = new JsonObject();
            TilesetObject.addProperty("Name", Tileset.Name);
            TilesetObject.addProperty("FilePath", Tileset.FilePath);
            JsonObject Size = new JsonObject();
            Size.addProperty("Width", Tileset.TileSizeW);
            Size.addProperty("Height", Tileset.TileSizeH);
            TilesetObject.add("TileSize", Size);
            TilesetObject.addProperty("TileSep", Tileset.TileSep);
            Tilesets.add(TilesetObject);
        Output.add("Tilesets", Tilesets);

        JsonObject GroundLayer = new JsonObject();
        GroundLayer.addProperty("tileset", this.Tileset.Name);
        GroundLayer.addProperty("exportMode", "CSV");
        String GroundTiles = "";
        for (int y = this.getHeight()-1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                if (x == 0) {
                    GroundTiles += "" + this.Ground[x][y];
                } else {
                    GroundTiles += "," + this.Ground[x][y];
                }
            }
            if (y != 0) {
                GroundTiles += "\n" ;
            }
        }
        GroundLayer.addProperty("text", GroundTiles);
        Output.add("Ground", GroundLayer);

        //----------------------------------------------------

        JsonObject ForegroundLayer = new JsonObject();
        ForegroundLayer.addProperty("tileset", this.Tileset.Name);
        ForegroundLayer.addProperty("exportMode", "CSV");
        String ForegroundTiles = "";
        for (int y = this.getHeight()-1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                if (x == 0) {
                    ForegroundTiles += "" + this.Foreground[x][y];
                } else {
                    ForegroundTiles += "," + this.Foreground[x][y];
                }
            }
            if (y != 0) {
                ForegroundTiles += "\n" ;
            }
        }
        ForegroundLayer.addProperty("text", ForegroundTiles);
        Output.add("Foreground", ForegroundLayer);

        //---------------------------------------------------

        JsonObject CollisionLayer = new JsonObject();
        CollisionLayer.addProperty("exportMode", "Bitstring");
        String CollisionTiles = "";
        for (int y = this.getHeight()-1; y >= 0; y--) {
            for (int x = 0; x < this.getWidth(); x++) {
                    if (this.Collision[x][y]) {
                        CollisionTiles += "1";
                    } else {
                        CollisionTiles += "0";
                    }
            }
            if (y != 0) {
                CollisionTiles += "\n" ;
            }
        }
        CollisionLayer.addProperty("text", CollisionTiles);
        Output.add("Collision", CollisionLayer);

        //Objects
        JsonArray ObjectsList = new JsonArray();
        for (int i = 0; i < entities.size(); i++) {
            JsonObject Entity = new JsonObject();
            Entity.addProperty("Name", ((Interactable) entities.get(i)).Name);
            Entity.addProperty("Description", "");
            Entity.addProperty("x",  entities.get(i).getPosition().x);
            Entity.addProperty("y",  entities.get(i).getPosition().y);
            Entity.addProperty("z",  entities.get(i).getPosition().z);
            Entity.addProperty("Width",  entities.get(i).getSize().x);
            Entity.addProperty("WidthOffset",  entities.get(i).getHitboxOffset().x);
            Entity.addProperty("Height",  entities.get(i).getSize().y);
            Entity.addProperty("HeightOffset",  entities.get(i).getHitboxOffset().y);
            Entity.addProperty("Depth",  entities.get(i).getSize().z);
            Entity.addProperty("DepthOffset",  entities.get(i).getHitboxOffset().z);
            if (entities.get(i) instanceof Interactable) {
                Entity.addProperty("TexLocation", ((Interactable) entities.get(i)).getTexLocation());
            } else {
                Entity.addProperty("TexLocation", "");
            }
            Entity.addProperty("Physics",  entities.get(i).getState().name());
            Entity.addProperty("Collidable",  entities.get(i).isCollidable());
            if (entities.get(i) instanceof Interactable)
                Entity.addProperty("Operation", ((Interactable) entities.get(i)).Name);
            else
                Entity.addProperty("Operation", "None");
            if (entities.get(i) instanceof Mine)
                Entity.addProperty("Collectible", "" + ((Mine) entities.get(i)).drops[0] + ":" + ((Mine) entities.get(i)).drops[1]);
            else if (entities.get(i) instanceof Chop)
                Entity.addProperty("Collectible", "" + ((Chop) entities.get(i)).drops[0] + ":" + ((Chop) entities.get(i)).drops[1]);
            else
                Entity.addProperty("Collectible",  "");
            if (entities.get(i) instanceof Trigger) {
                Entity.addProperty("TriggerType",  ((Trigger) entities.get(i)).getActivationType().toString());
                Entity.addProperty("Event",  ((Trigger) entities.get(i)).getRawCommands());
            } else {
                Entity.addProperty("TriggerType",  "");
                Entity.addProperty("Event", "");
            }

            ObjectsList.add(Entity);
        }

        Output.add("Objects", ObjectsList);

        Gson temp = new GsonBuilder().setPrettyPrinting().create();

        return temp.toJson(Output);
    }

}
