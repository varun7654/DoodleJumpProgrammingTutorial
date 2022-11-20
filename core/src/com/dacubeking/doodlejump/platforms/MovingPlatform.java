package com.dacubeking.doodlejump.platforms;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dacubeking.doodlejump.DoodleJump;
import com.dacubeking.doodlejump.player.Player;

import java.util.Random;

public class MovingPlatform extends Platform {
    private static final TextureRegion platformTexture = new TextureRegion(
            gameTiles,
            0, 35, 120, 35);

    private static float MOVING_PLATFORM_SPEED = 3f;

    private static Random random = new Random();

    public MovingPlatform(Vector2 position) {
        super(position, platformTexture);
        float speed;
        if (random.nextBoolean()) {
            speed = MOVING_PLATFORM_SPEED;
        } else {
            speed = -MOVING_PLATFORM_SPEED;
        }

        physicsBody.setLinearVelocity(speed, 0);
    }

    float platformWidth = platformTexture.getRegionWidth() * scalingFactor;

    @Override
    public void onPhysicsTick() {
        if (physicsBody.getPosition().x < -DoodleJump.getInstance().screenWidth / 2) {
            physicsBody.setLinearVelocity(MOVING_PLATFORM_SPEED, 0);
        } else if (physicsBody.getPosition().x + platformWidth > DoodleJump.getInstance().screenWidth / 2) {
            physicsBody.setLinearVelocity(-MOVING_PLATFORM_SPEED, 0);
        }
    }
}
