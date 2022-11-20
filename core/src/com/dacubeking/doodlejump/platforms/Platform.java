package com.dacubeking.doodlejump.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.dacubeking.doodlejump.DoodleJump;
import com.dacubeking.doodlejump.physics.PlayerContactable;
import com.dacubeking.doodlejump.physics.PhysicsTickable;
import com.dacubeking.doodlejump.physics.PhysicsWorld;
import com.dacubeking.doodlejump.player.Player;

public abstract class Platform implements Disposable, PhysicsTickable, PlayerContactable {

    protected final Body physicsBody;

    static final Texture gameTiles = new Texture(Gdx.files.internal("textures/game-tiles.png"), true);

    static {
        gameTiles.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
    }

    protected final TextureRegion platformTexture;
    protected final float scalingFactor;


    public Platform(Vector2 position, TextureRegion platformTextureLocal) {
        this.platformTexture = platformTextureLocal;
        scalingFactor = 2.0f / platformTexture.getRegionWidth();

        position.add(-1, 0); //Add a fixed value to make the position passed represent the center of the platform
        // First we create a body definition
        BodyDef platformBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        platformBodyDef.type = BodyType.KinematicBody;
        // Set our body's starting position in the world
        platformBodyDef.position.set(position);

        // Create our body in the world using our body definition
        physicsBody = PhysicsWorld.world.createBody(platformBodyDef);

        // Create a polygon shape
        PolygonShape platformBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        platformBox.set(new Vector2[]{
                        new Vector2(1.9f, 0.5f),
                        new Vector2(1.9f, 0.45f),
                        new Vector2(0.1f, 0.45f),
                        new Vector2(0.1f, 0.5f)
                }
        );
        // Create a fixture from our polygon shape and add it to our ground body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = platformBox;
        fixtureDef.density = 0.0f;
        fixtureDef.isSensor = true;

        physicsBody.createFixture(fixtureDef);


        physicsBody.createFixture(platformBox, 0.0f);
        // Clean up after ourselves
        platformBox.dispose();

        physicsBody.setUserData(this);
        physicsBody.setSleepingAllowed(false);

        PhysicsWorld.addToPhysicsTick(this);
    }

    public void dispose() {
        PhysicsWorld.removeFromPhysicsTick(this);
        PhysicsWorld.world.destroyBody(physicsBody);
    }

    /**
     * Gets the position of the body from Box2D
     *
     * @return the position of the body
     */
    public Vector2 getPosition() {
        return physicsBody.getPosition();
    }

    @Override
    public void onPlayerContact(Body playerBody) {
        if (playerBody.getLinearVelocity().y < 0) {
            DoodleJump.getPlayer().causeJump(Player.JUMP_VELOCITY);
        }
    }

    public void render(Batch batch) {
        Vector2 position = physicsBody.getPosition();
        batch.draw(platformTexture, position.x, position.y,
                platformTexture.getRegionWidth() * scalingFactor,
                platformTexture.getRegionHeight() * scalingFactor);
    }
}
