// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.Factory;
import com.thecubecast.ReEngine.Data.Floor;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.ParticleHandler;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkTextButton;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;

import java.util.ArrayList;
import java.util.List;

public class PlayState extends GameState {

    //This is the edge of the livable world
    //When it reaches zero you lose
    float Radius = 576;
    float RadiusDecay = 0.008f;

    //Tutorial Bools
    boolean BuiltFirstFactory = false;
    boolean BuiltFirstFloor = false;

    //The Array of Floors
    private List<Floor> Floors = new ArrayList<>();

    //The Array of Factories
    private List<Factory> Factories = new ArrayList<>();

    private Skin skin;
    private Stage stage;
    private Table FloorGUI;
    private int FloorSelected;
    private Table FactoryGUI;
    private Label FactoryDescription;
    private String FactoryPurchaseType = "";
    private Table ChooseFactoryGUI;
    private int ChoosingFactoryXPOS;
    private Table ScreenGUI;
    private Table AnalysisGUI;
    private Label AnalysisGuiText;
    Label ScreenGuiText;

    //Particles
    ParticleHandler Particles;

    OrthographicCamera Worldcam;
    OrthographicCamera GuiCam;
    ScreenShakeCameraController shaker;

    SpriteBatch guiBatch;

    Texture Ground = new Texture(Gdx.files.internal("Sprites/Grass_Bottom.png"));
    Texture Base = new Texture(Gdx.files.internal("Sprites/Tower_Base.png"));


    Texture[] Resources;
    static int[] ResourceQuantity;

    //private List<Achievement> Achievements = new ArrayList<>();

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        gsm.DiscordManager.setPresenceDetails("Claustrophobic");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;

        Resources = new Texture[Factory.Types.values().length];
        ResourceQuantity = new int[Resources.length];
        for (int i = 0; i < Resources.length; i++) {
            Resources[i] = new Texture(Gdx.files.internal("Sprites/Resource_" + Factory.Types.values()[i] + ".png"));
        }

        ResourceQuantity[0] = 10;

        //Particles
        Particles = new ParticleHandler();

        //Camera setup
        Worldcam = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        Worldcam.setToOrtho(false, gsm.Width, gsm.Height);
        Worldcam.position.x = 0;
        Worldcam.position.y = 86;
        GuiCam.setToOrtho(false, gsm.Width, gsm.Height);
        shaker = new ScreenShakeCameraController(Worldcam);

        //SETUP CAMERA SPRITEBATCH AND MENU
        guiBatch = new SpriteBatch();

        MenuInit();

        //SETUP SCENE2D INPUT
        Gdx.input.setInputProcessor(stage);

    }

    //Controls the camera, so it can only go into the sky at the tower, but side to side only on the ground
    public void CameraControler() {

        //Lerp the camera towards the center of the tower, allowing it to move up and SNAP ON
        if (Worldcam.position.x > -48 && Worldcam.position.x < 48) {
            Worldcam.position.x = (float) (Worldcam.position.x + 0.02 * (0 - Worldcam.position.x));
        }

        if (Worldcam.position.x > -2 && Worldcam.position.x < 2) {
            Worldcam.position.x = 0;
        }

        if (Worldcam.position.y == 86) {
            if (Gdx.input.isKeyPressed(Keys.LEFT) && Gdx.input.isKeyPressed(Keys.RIGHT)) {
                //Do nothing they are both pressed
            } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                Worldcam.position.x -= 5;
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                Worldcam.position.x += 5;
            }
        }

        if (Worldcam.position.x == 0) {
            if (Gdx.input.isKeyPressed(Keys.UP) && Gdx.input.isKeyPressed(Keys.DOWN)) {
                //Do nothing they are both pressed
            } else if (Gdx.input.isKeyPressed(Keys.UP)) {
                if (Worldcam.position.y + 5 < 86 + Floors.size() * 32)
                    Worldcam.position.y += 5;
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                if (Worldcam.position.y - 5 > 86)
                    Worldcam.position.y -= 5;
                else
                    Worldcam.position.y = 86;
            }
        }

        Worldcam.update();
    }

    public boolean canBuildFactory(Factory.Types Product) {
        List<Factory.Cost> tempReq = Product.getRequirements();

        for (int i = 0; i < tempReq.size(); i++) {
            if (ResourceQuantity[i] >= tempReq.get(i).getCost()) {

            } else return false;
        }
        return true;
    }

    public boolean AddFactory(Factory.Types Product, int Xpos) {

        List<Factory.Cost> tempReq = Product.getRequirements();

        if(!canBuildFactory(Product)) {
            return false;
        }

        for (int i = 0; i < tempReq.size(); i++) {
            ResourceQuantity[i] -= tempReq.get(i).getCost();
        }

        Factory temp = new Factory(Product, Xpos);
        Factories.add(temp);
        return true;
    }

    public void UpgradeFloor(int FloorNumber) {
        if (FloorNumber == 0) {
            List<Factory.Cost> tempReq = Floors.get(FloorNumber).getBuildingType().Upgrade().getRequirements();
            boolean canUpgrade = true;
            for (int i = 0; i < tempReq.size(); i++) {
                if (ResourceQuantity[i] >= tempReq.get(i).getCost()) {

                }
                else {
                    canUpgrade = false;
                }
            }

            if (canUpgrade) {
                for (int i = 0; i < tempReq.size(); i++) {
                    ResourceQuantity[i] -= tempReq.get(i).getCost();
                    Floors.get(FloorNumber).setBuildingType(Floors.get(FloorNumber).getBuildingType().Upgrade());
                }
            }

        } else if (Floors.get(FloorNumber).getBuildingType().Upgrade().getValue() <= Floors.get(FloorNumber-1).getBuildingType().getValue()) {
            List<Factory.Cost> tempReq = Floors.get(FloorNumber).getBuildingType().Upgrade().getRequirements();
            boolean canUpgrade = true;
            for (int i = 0; i < tempReq.size(); i++) {
                if (ResourceQuantity[i] >= tempReq.get(i).getCost()) {

                }
                else {
                    canUpgrade = false;
                }
            }

            if (canUpgrade) {
                for (int i = 0; i < tempReq.size(); i++) {
                    ResourceQuantity[i] -= tempReq.get(i).getCost();
                    Floors.get(FloorNumber).setBuildingType(Floors.get(FloorNumber).getBuildingType().Upgrade());
                }
            }

        }
    }

    public boolean canBuildFloor(Floor.Types Product) {
        List<Factory.Cost> tempReq = Product.getRequirements();

        for (int i = 0; i < tempReq.size(); i++) {
            if (ResourceQuantity[i] < tempReq.get(i).getCost()) {
                return false;
            } else {
                for (int j = 0; j < Floors.size(); j++) {
                    Floors.get(j).getCapacity();

                    int FloorsAbove = Floors.size() - j;

                    Floors.get(j).setFloorsAbove(FloorsAbove);

                    if (j+1 < Floors.size()) {
                        //If the building types match then the top most type counts as the base
                        if(Floors.get(j+1).getBuildingType().equals(Floors.get(j).getBuildingType())) {
                            if (Floors.get(j+1).getCapacity() < Floors.get(j).getFloorsAbove()) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean AddFloor(Floor.Types Material) {

        List<Factory.Cost> tempReq = Material.getRequirements();

        if(!canBuildFloor(Material)) {
            return false;
        }

        for (int i = 0; i < tempReq.size(); i++) {
            ResourceQuantity[i] -= tempReq.get(i).getCost();
        }

        Floor temp = new Floor(Material);
        Floors.add(temp);

        return true;


    }

    public void update() {

        if(Factories.size() == 0) {
            if (System.nanoTime() % 5 == 0)
                Particles.AddParticleEffect("Health", 46, 0);
        } else {
            if (!BuiltFirstFactory) {
                ScreenGUI.setVisible(true);
                ScreenGuiText.setText("Congrats on your first Factory!! \n Collect 50 Wood to build your first floor.\n Wait, or build more Factories to speed things up!");
                BuiltFirstFactory = true;
            }
            if(Floors.size() == 0) {
                if (System.nanoTime() % 5 == 0 && BuiltFirstFloor) {
                    Particles.AddParticleEffect("Health", -15, 0);
                    Particles.AddParticleEffect("Health", -15, 0);
                }
                if (!BuiltFirstFloor && ResourceQuantity[0] == 50) {
                    ScreenGUI.setVisible(true);
                    ScreenGuiText.setText("Click on the red particles to build your first floor. \n Upgrade the floor with the green arrow.\n " +
                            "Each floor strength requires a certain quantity of materials.\n be sure to check everything out in the manual!!\n " +
                            "Viewed from the book in the top right of the screen! \n Sorry didn't make it in time...");
                    BuiltFirstFloor = true;
                }
            }
        }

        if(Radius <= 0) {
            ScreenGUI.setVisible(true);
            ScreenGuiText.setText("GAME OVER");
        }

        for (int i = 0; i < Factories.size(); i++) {
            Factories.get(i).Update(ResourceQuantity);
        }

        Radius -= RadiusDecay;

        Particles.Update();

        CameraControler();
        handleInput();

        //ScreenGUI.setPosition(gsm.Width, gsm.Height);



    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        Gdx.gl.glClearColor(0, 0.02f, 0.09f, 0);
        shaker.update(gsm.DeltaTime);
        g.setProjectionMatrix(shaker.getCombinedMatrix());
        g.begin();

        for (int i = -400; i < 400; i++) {
            g.draw(Ground, i*8, -8);
        }
        g.draw(Base, -32, -4);

        //Floors
        for (int i = 0; i < Floors.size(); i++) {
            Floors.get(i).Draw(g, -30, i * 32);
        }

        //Factories
        for (int i = 0; i < Factories.size(); i++) {
            //Renders The Factories
            if (Factories.get(i).getxPosition() < 0) // Left Side
                Factories.get(i).Draw(g, 32 * Factories.get(i).getxPosition() - 32, 0);
            else
                Factories.get(i).Draw(g, 32 * Factories.get(i).getxPosition(), 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            gsm.Cursor = GameStateManager.CursorType.Question;

            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Worldcam.unproject(pos);
            //This will let you see the type of building faster
            //gsm.Render.GUIDrawText(g, Common.roundDown(pos.x) - 5, Common.roundDown(pos.y) - 5, "X: " + ((int) pos.x / 16) + " Y: " + ((int) pos.y / 16));
        } else {
            gsm.Cursor = GameStateManager.CursorType.Normal;
        }

        //Particles
        Particles.Draw(g);

        MenuDraw(g);

        g.setProjectionMatrix(GuiCam.combined);
        for (int i = 0; i < Resources.length; i++) {
            g.draw(Resources[i], 16 + (i*30), height-16);
            gsm.Render.GUIDrawText(g, 26 + (i*30), height-6, "" + ResourceQuantity[i]);
        }

        g.end();

        gsm.Render.debugRenderer.setProjectionMatrix(Worldcam.combined);
        gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (!ChooseFactoryGUI.isVisible()) {

            //This function renders buttons on the floor your looking at
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Worldcam.unproject(pos);
            FloorGUI.setVisible(false);
            for (int i = 0; i < Floors.size(); i++) {
                //-30,i*32,60,32
                if (pos.x > -30 && pos.x < 30) {
                    if (pos.y > i * 32 && pos.y < i * 32 + 32) {

                        FloorSelected = i;

                        FloorGUI.setVisible(true);
                        FloorGUI.setPosition(105-Worldcam.position.x, i * 32 +80 - Worldcam.position.y);

                    }
                }
            }
            if (pos.x > -30 && pos.x < 30) { // The build a new floor button
                if (pos.y > Floors.size() * 32 && pos.y < Floors.size() * 32 + 32) {
                    gsm.Render.debugRenderer.setColor(Color.RED);
                    gsm.Render.debugRenderer.rect(-30, Floors.size() * 32, 60, 2);
                    //TODO change this to a one click deal
                    if (Gdx.input.isTouched()) {
                        AddFloor(Floor.Types.Wood);
                    }
                }
            }

            FactoryGUI.setVisible(false);
            for (int i = 0; i < Factories.size(); i++) {
                if (Factories.get(i).getxPosition() < 0) {// Left Side
                    if (pos.x > 32 * Factories.get(i).getxPosition() - 32 && pos.x < 32 * Factories.get(i).getxPosition()) {
                        if (pos.y > 0 && pos.y < 28) {
                            ChoosingFactoryXPOS = Factories.get(i).getxPosition();

                            FactoryGUI.setVisible(true);
                            FactoryGUI.setPosition(Factories.get(i).getxPosition()*32 + 117-Worldcam.position.x, 32 +49 - Worldcam.position.y);
                        }
                    }
                } else {
                    if (pos.x > 32 * Factories.get(i).getxPosition() && pos.x < 32 * Factories.get(i).getxPosition() + 32) {
                        if (pos.y > 0 && pos.y < 28) {
                            ChoosingFactoryXPOS = Factories.get(i).getxPosition();

                            FactoryGUI.setVisible(true);
                            FactoryGUI.setPosition(Factories.get(i).getxPosition()*32 + 149-Worldcam.position.x, 32 +49 - Worldcam.position.y);
                        }
                    }
                }
            }

            //Handles the new Factory button
            if (pos.x < 0) {// Left Side
                if (pos.y > 0 && pos.y < 28) {
                    gsm.Render.debugRenderer.setColor(Color.RED);
                    int Xpos = ((int) pos.x / 32);
                    boolean Occupied = false;
                    if (pos.x > -32 && pos.x < 32) {
                    } else {
                        for (int i = 0; i < Factories.size(); i++) {
                            if (Factories.get(i).getxPosition() == Xpos) {
                                Occupied = true;
                                break;
                            }
                        }
                        if (!Occupied)
                            gsm.Render.debugRenderer.rect((Xpos - 1) * 32, 0, 32, 2);

                        if (Gdx.input.isTouched()) {
                            if (!Occupied) {
                                ChooseFactoryGUI.setVisible(true);
                                ChoosingFactoryXPOS = Xpos;
                            }
                        }
                    }
                }
            } else {
                if (pos.y > 0 && pos.y < 28) {
                    gsm.Render.debugRenderer.setColor(Color.RED);
                    int Xpos = ((int) pos.x / 32);
                    boolean Occupied = false;
                    if (pos.x > -32 && pos.x < 32) {
                    } else {
                        for (int i = 0; i < Factories.size(); i++) {
                            if (Factories.get(i).getxPosition() == Xpos) {
                                Occupied = true;
                                break;
                            }
                        }
                        if (!Occupied)
                            gsm.Render.debugRenderer.rect(Xpos * 32, 0, 32, 2);

                        if (Gdx.input.isTouched()) {
                            if (!Occupied) {
                                ChooseFactoryGUI.setVisible(true);
                                ChoosingFactoryXPOS = Xpos;
                            }
                        }
                    }
                }
            }
        }

        gsm.Render.debugRenderer.setColor(Color.PINK);
        gsm.Render.debugRenderer.line(-Radius,-10, -Radius, 10000);
        gsm.Render.debugRenderer.line(Radius,-10, Radius, 10000);

        gsm.Render.debugRenderer.end();

    }

    public void RenderCam() {
        Worldcam.update();
    }

    private void handleInput() {
        Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
        GuiCam.unproject(pos);

        gsm.MouseX = (int) pos.x;
        gsm.MouseY = (int) pos.y;
    }

    public void reSize(SpriteBatch g, int H, int W) {
        float posX = Worldcam.position.x;
        float posY = Worldcam.position.y;
        float posZ = Worldcam.position.z;
        Worldcam.setToOrtho(false, W, H);
        Worldcam.position.set(posX, posY, posZ);

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, W, H);
        guiBatch.setProjectionMatrix(matrix);
        shaker.reSize(Worldcam);
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
    }

    public void MenuInit() {

        setupSkin();
        stage = new Stage(new StretchViewport(gsm.Width, gsm.Height));
        Gdx.input.setInputProcessor(stage);

        ChooseFactoryGUI = new Table();
        ChooseFactoryGUI.setFillParent(true);
        ChooseFactoryGUI.setVisible(false);

        Table ScrollPaneContentL = new Table();
        Table ScrollPaneContentR = new Table(skin);
        ScrollPaneContentR.setBackground("Table_dialog_icons");
        ScrollPane tempScrollPane = new ScrollPane(ScrollPaneContentL, skin);
        ChooseFactoryGUI.add(tempScrollPane);
        ChooseFactoryGUI.add(ScrollPaneContentR);

        for (int i = 0; i < Factory.Types.values().length; i++) {
            TkTextButton temp = new TkTextButton(Factory.Types.values()[i].name(), skin);
            Factory.Types tempTemptemp = Factory.Types.values()[i];
            temp.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    String CostText = "";
                    for (int j = 0; j < tempTemptemp.getRequirements().size(); j++) {
                        CostText += "\n\t" + tempTemptemp.getRequirements().get(j).getResource() + ": ";
                        CostText += tempTemptemp.getRequirements().get(j).getCost();
                    }
                    FactoryDescription.setText("" + temp.getText() + " \n\nCost" + CostText + "\n\n Produces 5 every 5 seconds \n Can be upgraded 2 Times");
                    FactoryPurchaseType = temp.getText().toString();
                }
            });
            ScrollPaneContentL.add(temp).pad(2).row();

        }

        TkTextButton temp = new TkTextButton("Exit", skin);
        temp.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                ChoosingFactoryXPOS = 0;
                ChooseFactoryGUI.setVisible(false);
            }
        });
        ScrollPaneContentL.add(temp).pad(2).row();

        FactoryDescription = new Label("Click Wood on the left\n then buy a factory! ", skin);
        TkTextButton BuyFactory = new TkTextButton("Buy", skin);
        BuyFactory.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                for (int j = 0; j < Factory.Types.values().length; j++) {
                    if (FactoryPurchaseType.equals(Factory.Types.values()[j].name())) {
                        if (AddFactory(Factory.Types.values()[j], ChoosingFactoryXPOS)) {
                            FactoryPurchaseType = "";
                            ChoosingFactoryXPOS = 0;
                            ChooseFactoryGUI.setVisible(false);
                            FactoryDescription.setText("Choose a Product!!");
                        } else {
                            FactoryDescription.setText("NOT ENOUGH RESOURCES");
                        }
                    }
                }
            }
        });
        ScrollPaneContentR.add(FactoryDescription).pad(2).row();
        ScrollPaneContentR.add(BuyFactory).pad(2).bottom();

        stage.addActor(ChooseFactoryGUI);

        FloorGUI = new Table();
        FloorGUI.setSize(60, 32);

        ImageButton UpgradeIcon = new ImageButton(skin, "Upgrade");

        UpgradeIcon.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                UpgradeFloor(FloorSelected);
                FloorSelected = -1;
            }
        });
        FloorGUI.add(UpgradeIcon);
        FloorGUI.setVisible(false);

        stage.addActor(FloorGUI);

        FactoryGUI = new Table(skin);
        FactoryGUI.setSize(32, 28);

        ImageButton FactUpgradeIcon = new ImageButton(skin, "Upgrade");

        FactUpgradeIcon.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                ChoosingFactoryXPOS = 0;
            }
        });
        FactoryGUI.add(FactUpgradeIcon);
        FactoryGUI.setVisible(false);

        stage.addActor(FactoryGUI);

        ScreenGUI = new Table();
        ScreenGUI.setFillParent(true);

        Table Window = new Table(skin);
        Window.setBackground("Table_dialog_icons");
        ScreenGUI.add(Window);

        ScreenGuiText = new Label("Welcome!\n Build a factory by clicking on the red particles.\n Once you get a factory making more wood,\n you will be able to" +
                " build the first floor of your tower!\n Hurry! the world is shrinking.\n Use the arrow keys to move around your city.\n The pink line is the edge of the world!!!", skin);

        Window.add(ScreenGuiText).fill().pad(2).row();
        TkTextButton tempButton2 = new TkTextButton("OK", skin);
        tempButton2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                ScreenGuiText.setText("");
                ScreenGUI.setVisible(false);
            }
        });
        Window.add(tempButton2).pad(2).row();

        stage.addActor(ScreenGUI);

        AnalysisGUI = new Table(skin);
        AnalysisGUI.setBackground("Table_dialog_icons");

    }

    public void MenuDraw(SpriteBatch bbg) {

        bbg.setProjectionMatrix(GuiCam.combined);
        stage.act(gsm.DeltaTime);
        stage.getRoot().draw(bbg, 1);
        bbg.setProjectionMatrix(Worldcam.combined);
    }

    //Ends the Gui Shit

    @Override
    public void Shutdown() {

    }

}