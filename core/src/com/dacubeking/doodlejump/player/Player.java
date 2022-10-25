package com.dacubeking.doodlejump.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.dacubeking.doodlejump.Constants;
import com.dacubeking.doodlejump.DoodleJump;
import com.dacubeking.doodlejump.physics.PhysicsTickable;
import com.dacubeking.doodlejump.physics.PhysicsWorld;
import net.dermetfan.gdx.physics.box2d.PositionController.P;

public class Player implements PhysicsTickable, Disposable {

    public static final int MOVEMENT_FORCE = 25;
    public static final int JUMP_VELOCITY = 15;

    private double jumpTime = 0;

    public Body getPhysicsBody() {
        return physicsBody;
    }

    private final Body physicsBody;
    private final static Texture playerTextureLeft = new Texture(Gdx.files.internal("textures/lik-left.png"), true);
    private final static Texture playerTextureRight = new Texture(Gdx.files.internal("textures/lik-right.png"), true);
    private final static Texture playerTextureLeftJumping = new Texture(Gdx.files.internal("textures/lik-left-jumping.png"), true);
    private final static Texture playerTextureRightJumping = new Texture(Gdx.files.internal("textures/lik-right-jumping.png"), true);

    static {
        playerTextureRight.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
        playerTextureLeft.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
        playerTextureRightJumping.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
        playerTextureLeftJumping.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
    }

    public static final int PLAYER_DEATH_BUFFER = 1;

    public Player() {
        // First we create a body definition
        BodyDef playerBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        playerBodyDef.type = BodyType.DynamicBody;
        // Set our body's starting position in the world
        playerBodyDef.position.set(0, 5);

        // Create our body in the world using our body definition
        physicsBody = PhysicsWorld.world.createBody(playerBodyDef);

        // Create a circle shape and set its radius to 6
        ChainShape rectangle = new ChainShape();
        rectangle.createLoop(
                new Vector2[]{
                        new Vector2(1.55f, 0.05f),
                        new Vector2(1.55f, 0),
                        new Vector2(0.55f, 0),
                        new Vector2(0.55f, 0.05f)
                }
        );

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rectangle;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.isSensor = true;

        // Create our fixture and attach it to the body
        Fixture fixture = physicsBody.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        rectangle.dispose();

        PhysicsWorld.addToPhysicsTick(this);
    }

    //NOTE: Use physicsBody.getPosition(); to get the position of the player

    @Override
    public void onPhysicsTick() {
        Vector2 position = physicsBody.getPosition();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            physicsBody.applyForceToCenter(MOVEMENT_FORCE, 0, true);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            physicsBody.applyForceToCenter(-MOVEMENT_FORCE, 0, true);
        }
        if (position.y < 0) {
            causeJump(JUMP_VELOCITY);
        }
        physicsBody.setLinearVelocity(physicsBody.getLinearVelocity().x * 0.995f,
                physicsBody.getLinearVelocity().y);
        jumpTime -= Constants.TIME_STEP;

        DoodleJump dj = DoodleJump.getInstance();

        if (position.y < dj.getWantedCameraY()
                - dj.screenHeight / 2 - PLAYER_DEATH_BUFFER) {
            dj.reset();
        }

        if (position.x < -dj.backgroundWidth / 2 - playerWidth) {
            physicsBody.setTransform(dj.backgroundWidth / 2, position.y, 0);
        }
        if (position.x > dj.backgroundWidth / 2) {
            physicsBody.setTransform(-dj.backgroundWidth / 2 - playerWidth, position.y, 0);
        }
    }

    float playerWidth = (2f / playerTextureLeft.getHeight()) * playerTextureLeft.getWidth();

    public void render(Batch batch) {
        Vector2 position = physicsBody.getPosition();
        Vector2 velocity = physicsBody.getLinearVelocity();


        if (jumpTime > 0) {
            if (velocity.x < 0) {
                batch.draw(playerTextureLeftJumping, position.x, position.y,
                        playerWidth, 2);
            } else {
                batch.draw(playerTextureRightJumping, position.x, position.y,
                        playerWidth, 2);
            }
        } else {
            if (velocity.x < 0) {
                batch.draw(playerTextureLeft, position.x, position.y,
                        playerWidth, 2);
            } else {
                batch.draw(playerTextureRight, position.x, position.y,
                        playerWidth, 2);
            }
        }
    }

    @Override
    public void dispose() {
        PhysicsWorld.world.destroyBody(physicsBody); // Must do this to avoid memory leaks
        PhysicsWorld.removeFromPhysicsTick(this); // Make sure the PhysicsWorld doesn't keep a reference to this object
    }

    public void causeJump(float jumpVelocity) {
        physicsBody.setLinearVelocity(physicsBody.getLinearVelocity().x, jumpVelocity);
        jumpTime = 0.1;
    }
}
