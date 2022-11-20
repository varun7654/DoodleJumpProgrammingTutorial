package com.dacubeking.doodlejump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dacubeking.doodlejump.physics.PhysicsTickable;
import com.dacubeking.doodlejump.physics.PhysicsWorld;
import com.dacubeking.doodlejump.platforms.MovingPlatform;
import com.dacubeking.doodlejump.platforms.NormalPlatform;
import com.dacubeking.doodlejump.platforms.Platform;
import com.dacubeking.doodlejump.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.dacubeking.doodlejump.util.MathUtil.mod;

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
    private final ArrayList<Platform> platforms = new ArrayList<>();

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

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1); //Clear everything to black
        PhysicsWorld.doPhysicsStep(Gdx.graphics.getDeltaTime()); // Do physics for all objects

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background,
                -backgroundWidth / 2,
                camera.position.y - mod(camera.position.y, backgroundHeight)
                        - backgroundHeight / 2,
                backgroundWidth,
                backgroundHeight);
        batch.draw(background,
                -backgroundWidth / 2,
                camera.position.y - mod(camera.position.y, backgroundHeight)
                        + backgroundHeight / 2,
                backgroundWidth,
                backgroundHeight);
        //batch.draw(background, -backgroundWidth / 2, backgroundHeight, backgroundWidth, backgroundHeight);
        for (Platform platform : platforms) {
            platform.render(batch);
        }
        player.render(batch);

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
        PhysicsWorld.dispose();
        for (Platform platform : platforms) {
            platform.dispose();
        }
    }

    float wantedCameraY = 0;
    float nextPlatformGenerationY = 1;

    private final Random random = new Random();

    @Override
    public void onPhysicsTick() {
        Vector3 cameraOffset = new Vector3(0, Gdx.graphics.getHeight() / 2f, 0);
        camera.unproject(cameraOffset).sub(camera.position); // Camera offset is not the world position of the center of the screen

        wantedCameraY = Math.max(
                player.getPhysicsBody().getPosition().y + cameraOffset.y,
                wantedCameraY
        );

        camera.position.y = (wantedCameraY - camera.position.y) * 0.03f
                + camera.position.y;

        // Is the nextPlatformGenerationY inside the screen?
        if (nextPlatformGenerationY < wantedCameraY + backgroundHeight) {
            // Creates a platform at nextPlatformGenerationY
            Vector2 randomPosition = new Vector2(
                    random.nextFloat() * screenWidth - (screenWidth / 2),
                    nextPlatformGenerationY
            );

            if (random.nextFloat() < 0.5) {
                platforms.add(new NormalPlatform(randomPosition));
            } else {
                platforms.add(new MovingPlatform(randomPosition));
            }

            nextPlatformGenerationY += random.nextFloat() * 3 + 0.5;
        }

        Iterator<Platform> platformIterator = platforms.iterator();
        while (platformIterator.hasNext()) {
            Platform platform = platformIterator.next();
            if (platform.getPosition().y < wantedCameraY - backgroundHeight * 1) {
                platform.dispose();
                platformIterator.remove();
            }
        }
    }

    public void reset() {
        platforms.forEach(Platform::dispose); // Dispose all the platforms
        platforms.clear(); // Clear the list of platforms
        player.dispose();
        player = new Player();
        wantedCameraY = 0;
        nextPlatformGenerationY = 0;
    }

    public float getWantedCameraY() {
        return wantedCameraY;
    }
}
