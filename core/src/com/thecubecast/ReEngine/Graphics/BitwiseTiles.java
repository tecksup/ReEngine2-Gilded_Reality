package com.thecubecast.ReEngine.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.thecubecast.ReEngine.Data.Common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BitwiseTiles {

    TiledMap tiledMap;
    public List<bitTileObject> bitTileObjectLayers = new ArrayList<>();

    //List<short[]> xcoord = new ArrayList<short[]>();

    public class bitTileObject {
        public int height;
        public int width;
        public List<short[]> BitTiles = new ArrayList<short[]>(); //Used to be xcoord
        public List<short[]> realTile = new ArrayList<short[]>();
    }


    public class tileTexture {
        public List<TextureRegion[]> variants = new ArrayList<TextureRegion[]>();

    }

    List<tileTexture> bitTiles = new ArrayList<tileTexture>();

    public BitwiseTiles(TiledMap map) {

        //calculate the map bitwise operations
        calculate(map);

        //The Size of the images
        int Rows = 4;
        int Cols = 4;

        Path imagePath = Paths.get("Sprites/bitWise");

        long types = 0;
        try {
            types = Files.list(imagePath).count();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 1; i <= types; i++) {
            TextureRegion[] temp = new TextureRegion[Cols * Rows];
            Texture TexSheet = new Texture(Gdx.files.internal("Sprites/bitWise/" + i + ".png"));

            TextureRegion[][] tmp = TextureRegion.split(TexSheet,
                    TexSheet.getWidth() / Cols,
                    TexSheet.getHeight() / Rows);

           /* int index = 0;
            for (int l = 0; l < Rows; l++) {
                for (int j = 0; j < Cols; j++) {
                    temp[index++] = tmp[l][j];
                }
            }
            */

            temp[6] = tmp[0][0];
            temp[14] = tmp[0][1];
            temp[12] = tmp[0][2];
            temp[4] = tmp[0][3];
            temp[7] = tmp[1][0];
            temp[15] = tmp[1][1];
            temp[13] = tmp[1][2];
            temp[5] = tmp[1][3];
            temp[3] = tmp[2][0];
            temp[11] = tmp[2][1];
            temp[9] = tmp[2][2];
            temp[1] = tmp[2][3];
            temp[2] = tmp[3][0];
            temp[10] = tmp[3][1];
            temp[8] = tmp[3][2];
            temp[0] = tmp[3][3];

            tileTexture tiletemp = new tileTexture();
            tiletemp.variants.add(temp);
            bitTiles.add(tiletemp);
        }

    }

    /*
    public void BitwiseTilesVariants(TiledMap map) {

        //calculate the map bitwise operations
        calculate(map);

        Path imagePath = Paths.get("Sprites/bitWise");

        try {
            Files.list(imagePath).forEach(path -> setupTileTexture(path));
        } catch (IOException e) {

        }

    }

    public void setupTileTexture(Path file) {

        int Rows = 4;
        int Cols = 4;

        TextureRegion[] temp = new TextureRegion[Cols * Rows];
        Texture TexSheet = new Texture(Gdx.files.internal(file.toString()));

        TextureRegion[][] tmp = TextureRegion.split(TexSheet,
                TexSheet.getWidth() / Cols,
                TexSheet.getHeight() / Rows);

            int index = 0;
            for (int l = 0; l < Rows; l++) {
                for (int j = 0; j < Cols; j++) {
                    temp[index++] = tmp[l][j];
                }
            }


        temp[6] = tmp[0][0];
        temp[14] = tmp[0][1];
        temp[12] = tmp[0][2];
        temp[4] = tmp[0][3];
        temp[7] = tmp[1][0];
        temp[15] = tmp[1][1];
        temp[13] = tmp[1][2];
        temp[5] = tmp[1][3];
        temp[3] = tmp[2][0];
        temp[11] = tmp[2][1];
        temp[9] = tmp[2][2];
        temp[1] = tmp[2][3];
        temp[2] = tmp[3][0];
        temp[10] = tmp[3][1];
        temp[8] = tmp[3][2];
        temp[0] = tmp[3][3];

        String[] stringArray = file.toString().split("/");
        Common.print("" + stringArray);
        String[] stringArray2 = stringArray[stringArray.length].split("_");
        Common.print("" + stringArray2);
        int Tilenumber = Integer.parseInt(stringArray2[0]);
        Common.print("" + Tilenumber);



        tileTexture tiletemp = new tileTexture();
        tiletemp.variants.add(temp);
        bitTiles.add(tiletemp);
    }
*/

    public void calculate(TiledMap map) { // Will update the bitwise table
        tiledMap = map;

        Common.print("map Layers: " + tiledMap.getLayers().getCount());

        for (int layer = 0; layer < tiledMap.getLayers().getCount(); layer++) {

            TiledMapTileLayer TileLayer = (TiledMapTileLayer)tiledMap.getLayers().get(layer);
            bitTileObject tempbitTile = new bitTileObject();

            //Stone is 1
            //Sand is 2
            //Dirt is 3
            //Stone will only merge with dirt, not sand, therefore only 1 bitwise tile to check

            //Common.print("map Width: " + TileLayer.getWidth());
            //Common.print("map Height: " + TileLayer.getHeight());

            for (int y = 0; y < TileLayer.getHeight(); y++) {
                short[] temp2 = new short[TileLayer.getHeight()];
                short[] tempReal = new short[TileLayer.getHeight()];
                for (int x = 0; x < TileLayer.getWidth(); x++) {
                    if (TileLayer.getCell(x, y) == null) {
                        tempReal[x] += -1;
                        temp2[x] = -1;
                        continue;
                    }

                    int TileCheck = TileLayer.getCell(x, y).getTile().getId();

                    if (true) {
                        short id = 0;
                        if (TileLayer.getCell(x, y + 1) == null) {
                        } else if (TileCheck == TileLayer.getCell(x, y + 1).getTile().getId()) {
                            id += 1;
                        }

                        if (TileLayer.getCell(x + 1, y) == null) {
                        } else if (TileCheck == TileLayer.getCell(x + 1, y).getTile().getId()) {
                            id += 2;
                        }

                        if (TileLayer.getCell(x, y - 1) == null) {
                        } else if (TileCheck == TileLayer.getCell(x, y - 1).getTile().getId()) {
                            id += 4;
                        }

                        if (TileLayer.getCell(x - 1, y) == null) {
                        } else if (TileCheck == TileLayer.getCell(x - 1, y).getTile().getId()) {
                            id += 8;
                        }

                        tempReal[x] += TileLayer.getCell(x, y).getTile().getId();
                        temp2[x] = id;
                        //String s2 = String.format("%8s", Integer.toBinaryString(id & 0xFF)).replace(' ', '0');
                        //Common.print("id is " + s2 + " or " + id + " at (" + x + "," + y + ") from tile " + TileLayer.getCell(x, y).getTile().getId());
                    }
                }
                tempbitTile.height = TileLayer.getHeight();
                tempbitTile.width = TileLayer.getWidth();
                tempbitTile.BitTiles.add(temp2);
                tempbitTile.realTile.add(tempReal);
            }

            bitTileObjectLayers.add(tempbitTile);
        }
    }

    public void draw(SpriteBatch batch, int tileSize, float playerY, float time) {//Draws the tile starting from 0, so if its tile 2 draw bitTile 1
        for (int layer = 0; layer < bitTileObjectLayers.size(); layer++) {
            for (int y = 0; y < bitTileObjectLayers.get(layer).realTile.size(); y++) {
                for(int x = 0; x < bitTileObjectLayers.get(layer).realTile.get(y).length; x++) {

                    //bitTileObject.BitTiles.get(y)[x] that is the bitTile
                    //bitTileObject.realTile.get(y)[x] is the tile type

                    //Common.print("Real Tile is " + bitTileObject.realTile.get(y)[x]);
                    //Common.print("Bit Tile is " + bitTileObject.BitTiles.get(y)[x]);

                    if (bitTileObjectLayers.get(layer).BitTiles.get(y)[x] == -1) {
                        continue;
                    }

                    int RealTile = bitTileObjectLayers.get(layer).realTile.get(y)[x];
                    int BitDirectionright = bitTileObjectLayers.get(layer).BitTiles.get(y)[x] & 0b1111;
                    int BitDirectionleft = bitTileObjectLayers.get(layer).BitTiles.get(y)[x] >> 4;

                    /*
                    if (BitDirectionleft > 0)
                        batch.draw(bitTiles.get(RealTile-1)[BitDirectionleft+16], (int) x*tileSize, (int) y*tileSize);
                    else // THIS RUNS BY DEFAULT
                        if (RealTile == 4) { // So we can animate special tiles

                        } else {

                        }
                    */
                    batch.draw(bitTiles.get(RealTile-1).variants.get(0)[BitDirectionright], x*tileSize, y*tileSize);
                    //batch.draw(bitTiles.get(RealTile-1)[bitTileObject.BitTiles.get(y)[x]], x*tileSize,	y*tileSize,	0, 0, bitTiles.get(RealTile-1)[0].getRegionWidth(), bitTiles.get(RealTile-1)[0].getRegionHeight(), 1, 1,0);



                }
            }
        }
    }

    public void drawLayer(SpriteBatch batch, int tileSize, float time, int layer, float Playery, boolean first) {//Draws the tile starting from 0, so if its tile 2 draw bitTile 1
        for (int y = 0; y < bitTileObjectLayers.get(layer).realTile.size(); y++) {
            for(int x = 0; x < bitTileObjectLayers.get(layer).realTile.get(y).length; x++) {

                //bitTileObject.BitTiles.get(y)[x] that is the bitTile
                //bitTileObject.realTile.get(y)[x] is the tile type

                //Common.print("Real Tile is " + bitTileObject.realTile.get(y)[x]);
                //Common.print("Bit Tile is " + bitTileObject.BitTiles.get(y)[x]);

                if (bitTileObjectLayers.get(layer).BitTiles.get(y)[x] == -1) {
                    continue;
                }

                int RealTile = bitTileObjectLayers.get(layer).realTile.get(y)[x];
                int BitDirectionright = bitTileObjectLayers.get(layer).BitTiles.get(y)[x] & 0b1111;
                int BitDirectionleft = bitTileObjectLayers.get(layer).BitTiles.get(y)[x] >> 4;

                if (BitDirectionleft > 0)
                    batch.draw(bitTiles.get(RealTile-1).variants.get(0)[BitDirectionleft+16], x*tileSize, y*tileSize);
                else { // THIS RUNS BY DEFAULT
                    if (RealTile == 4) { // So we can animate special tiles

                    } else {
                        batch.draw(bitTiles.get(RealTile-1).variants.get(0)[BitDirectionright], x*tileSize, y*tileSize);
                    }
                }

                if (first) {
                    if(Playery > x*tileSize) {
                        //batch.draw(bitTiles.get(RealTile-1)[BitDirectionright], x*tileSize, y*tileSize);
                        //batch.draw(bitTiles.get(RealTile-1)[bitTileObject.BitTiles.get(y)[x]], x*tileSize,	y*tileSize,	0, 0, bitTiles.get(RealTile-1)[0].getRegionWidth(), bitTiles.get(RealTile-1)[0].getRegionHeight(), 1, 1,0);
                    }
                } else {

                }
            }
        }
    }

    public bitTileObject getBitTileObject(int layer) {
        return bitTileObjectLayers.get(layer);
    }
}
