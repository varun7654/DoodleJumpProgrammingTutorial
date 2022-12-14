package com.dacubeking.doodlejump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dacubeking.doodlejump.physics.PhysicsTickable;
import com.dacubeking.doodlejump.physics.PhysicsWorld;
import com.dacubeking.doodlejump.platforms.Platform;
import com.dacubeking.doodlejump.player.Player;

import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.random;

public class DoodleJump extends ApplicationAdapter implements PhysicsTickable {
    SpriteBatch batch;
    Texture img;

    private Box2DDebugRenderer debugRenderer;
    public Camera camera;
    private Viewport viewport;

    static public Player getPlayer() {
        return getInstance().player;
    }

    private Player player;

    Texture background;
    public float backgroundHeight;
    public float backgroundWidth;

    public float screenWidth;
    public float screenHeight;

    public float pixelScale;

    private static final DoodleJump doodleJump = new DoodleJump();

    public static DoodleJump getInstance() {
        return doodleJump;
    }

    private DoodleJump() {
        // Private constructor to ensure that only one instance of DoodleJump exists
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        PhysicsWorld.init();

        debugRenderer = new Box2DDebugRenderer();
        // Fix the height to be 18
        pixelScale = 18f / Gdx.graphics.getHeight();
        screenHeight = 18;
        screenWidth = pixelScale * Gdx.graphics.getWidth();


        camera = new OrthographicCamera(screenWidth, screenHeight);
        viewport = new ScreenViewport(camera);


        player = new Player();

        background = new Texture(Gdx.files.internal("textures/bck.png"));

        backgroundWidth = camera.viewportWidth;
        backgroundHeight = (camera.viewportWidth / background.getWidth()) * background.getHeight();
        PhysicsWorld.addToPhysicsTick(this);

        reset();
    }


    ArrayList<Platform> platforms = new ArrayList<>();
    float cameraY = 0;

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1); //Clear everything to black
        PhysicsWorld.doPhysicsStep(Gdx.graphics.getDeltaTime()); // Do physics for all objects

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.flush();
        debugRenderer.render(PhysicsWorld.world, camera.combined); // The debug renderer is used to render all the collision boxes
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        PhysicsWorld.dispose();
        for (Platform platform : platforms) {
            platform.dispose();
        }
    }

    @Override
    public void onPhysicsTick() {
        Vector3 cameraOffset = new Vector3(0, Gdx.graphics.getHeight() / 2f, 0);
        camera.unproject(cameraOffset).sub(camera.position); // Camera offset is not the world position of the center of the screen
    }

    public void reset() {
        platforms.forEach(Platform::dispose); // Dispose all the platforms
        platforms.clear(); // Clear the list of platforms
        player.dispose();
        player = new Player();
        cameraY = 0;
    }
}
