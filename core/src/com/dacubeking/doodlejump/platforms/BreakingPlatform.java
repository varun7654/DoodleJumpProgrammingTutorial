package com.dacubeking.doodlejump.platforms;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dacubeking.doodlejump.Constants;
import com.dacubeking.doodlejump.DoodleJump;
import com.dacubeking.doodlejump.player.Player;

public class BreakingPlatform extends Platform {
    private static final float PLATFORM_FALL_SPEED = 10f;

    public BreakingPlatform(Vector2 position) {
        super(position, null);
    }

    private static final Animation<TextureRegion>
            platformTextureAnimation = new Animation<>(0.03f,
            new TextureRegion(gameTiles, 0, 143, 120, 35),
            new TextureRegion(gameTiles, 0, 181, 120, 43),
            new TextureRegion(gameTiles, 0, 232, 120, 55),
            new TextureRegion(gameTiles, 0, 300, 120, 63)
    );

    private static final float scalingFactor = 2.0f
            / platformTextureAnimation.getKeyFrame(0).getRegionWidth();

    private float animationTime = -1;

    @Override
    public void render(Batch batch) {
        Vector2 position = physicsBody.getPosition();

        float usableAnimationTime = Math.max(animationTime, 0);

        TextureRegion platformTexture = platformTextureAnimation.getKeyFrame(usableAnimationTime);

        batch.draw(platformTexture, position.x,
                position.y
                        - (scalingFactor * (platformTexture.getRegionHeight() - 35))
                        - usableAnimationTime * PLATFORM_FALL_SPEED,
                platformTexture.getRegionWidth() * scalingFactor,
                platformTexture.getRegionHeight() * scalingFactor);
    }

    @Override
    public void onPlayerContact(Body playerBody) {
        if (playerBody.getLinearVelocity().y < 0 && animationTime == -1) {
            animationTime = 0;
            DoodleJump.getPlayer().causeJump(Player.JUMP_VELOCITY);
        }
    }

    @Override
    public void onPhysicsTick() {
        if (animationTime != -1) {
            animationTime += Constants.TIME_STEP;
        }
    }
}
