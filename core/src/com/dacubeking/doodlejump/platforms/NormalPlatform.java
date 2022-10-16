package com.dacubeking.doodlejump.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dacubeking.doodlejump.Constants;
import com.dacubeking.doodlejump.DoodleJump;
import com.dacubeking.doodlejump.player.Player;

public class NormalPlatform extends Platform {
    public NormalPlatform(Vector2 position) {
        super(position);
    }


    private static final TextureRegion platformTexture = new TextureRegion(
            gameTiles,
            0, 0, 120, 35);

    @Override
    public void render(Batch batch) {
        Vector2 position = physicsBody.getPosition();
        batch.draw(platformTexture, position.x, position.y,
                2,
                (2f / platformTexture.getRegionWidth())
                        * platformTexture.getRegionHeight());
    }

    @Override
    public void onPhysicsTick() {

    }

    @Override
    public void onPlayerContact(Body playerBody) {
        if (playerBody.getLinearVelocity().y < 0) {
            DoodleJump.getPlayer().causeJump(Player.JUMP_VELOCITY);
        }
    }
}
