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
    bitTileObject bitTileObject;

    //List<short[]> xcoord = new ArrayList<short[]>();

    public class bitTileObject {
        List<short[]> BitTiles = new ArrayList<short[]>(); //Used to be xcoord
        List<short[]> realTile = new ArrayList<short[]>();
    }


    List<TextureRegion[]> bitTiles = new ArrayList<TextureRegion[]>();
    TextureRegion[] bitTilesgrass;
    TextureRegion[] bitTilesdirt;
    TextureRegion[] bitTilesstone;

    public BitwiseTiles(TiledMap map) {
        bitTileObject = new bitTileObject();

        //The Size of the images
        int Rows = 8;
        int Cols = 4;

        //calculate the map bitwise operations
        calculate(map);

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

            int index = 0;
            for (int l = 0; l < Rows; l++) {
                for (int j = 0; j < Cols; j++) {
                    temp[index++] = tmp[l][j];
                }
            }
            bitTiles.add(temp);
        }

    }

    public void calculate(TiledMap map) { // Will update the bitwise table
        tiledMap = map;
        TiledMapTileLayer TileLayer = (TiledMapTileLayer)tiledMap.getLayers().get(0);

        //Stone is 1
        //Sand is 2
        //Dirt is 3
        //Stone will only merge with dirt, not sand, therefore only 1 bitwise tile to check

        for (int y = 0; y < TileLayer.getHeight(); y++) {
            short[] temp2 = new short[TileLayer.getHeight()];
            short[] tempReal = new short[TileLayer.getHeight()];
            for(int x = 0; x < TileLayer.getWidth(); x++) {
                int TileCheck = TileLayer.getCell(x, y).getTile().getId();

                if (true) {
                    short id = 0;
                    if (TileLayer.getCell(x, y+1) == null) {}
                    else if (TileCheck == TileLayer.getCell(x, y+1).getTile().getId()) {
                        id += 1;
                    } else if (TileCheck != TileLayer.getCell(x, y+1).getTile().getId()) {
                        //id += 16;
                    }

                    if (TileLayer.getCell(x+1, y) == null) {}
                    else if (TileCheck == TileLayer.getCell(x+1, y).getTile().getId()) {
                        id += 2;
                    } else if (TileCheck != TileLayer.getCell(x+1, y).getTile().getId()) {
                        //id += 32;
                    }

                    if (TileLayer.getCell(x, y-1) == null) {}
                    else if (TileCheck == TileLayer.getCell(x, y-1).getTile().getId()) {
                        id += 4;
                    } else if (TileCheck != TileLayer.getCell(x, y-1).getTile().getId()) {
                        //id += 64;
                    }

                    if (TileLayer.getCell(x-1, y) == null) {}
                    else if (TileCheck == TileLayer.getCell(x-1, y).getTile().getId()) {
                        id += 8;
                    } else if (TileCheck != TileLayer.getCell(x-1, y).getTile().getId()) {
                        //id += 128;
                    }

                    tempReal[x] += TileLayer.getCell(x, y).getTile().getId();
                    temp2[x] = id;
                    String s2 = String.format("%8s", Integer.toBinaryString(id & 0xFF)).replace(' ', '0');
                    Common.print("id is " + s2 + " or " + id + " at (" + x + "," + y + ") from tile " + TileLayer.getCell(x, y).getTile().getId());
                }
            }
            bitTileObject.BitTiles.add(temp2);
            bitTileObject.realTile.add(tempReal);
        }
    }

    public void draw(SpriteBatch batch, int tileSize) {//Draws the tile starting from 0, so if its tile 2 draw bitTile 1
        for (int y = 0; y < bitTileObject.realTile.size(); y++) {
            for(int x = 0; x < bitTileObject.realTile.get(y).length; x++) {

                //bitTileObject.BitTiles.get(y)[x] that is the bitTile
                //bitTileObject.realTile.get(y)[x] is the tile type

                //Common.print("Real Tile is " + bitTileObject.realTile.get(y)[x]);
                //Common.print("Bit Tile is " + bitTileObject.BitTiles.get(y)[x]);

                int RealTile = bitTileObject.realTile.get(y)[x];
                int BitDirectionright = bitTileObject.BitTiles.get(y)[x] & 0b1111;
                int BitDirectionleft = bitTileObject.BitTiles.get(y)[x] >> 4;

                if (BitDirectionleft > 0)
                    batch.draw(bitTiles.get(RealTile-1)[BitDirectionleft+16], x*tileSize, y*tileSize);
                else
                    batch.draw(bitTiles.get(RealTile-1)[BitDirectionright], x*tileSize, y*tileSize);
                //batch.draw(bitTiles.get(RealTile-1)[bitTileObject.BitTiles.get(y)[x]], x*tileSize,	y*tileSize,	0, 0, bitTiles.get(RealTile-1)[0].getRegionWidth(), bitTiles.get(RealTile-1)[0].getRegionHeight(), 1, 1,0);



            }
        }
    }

}
