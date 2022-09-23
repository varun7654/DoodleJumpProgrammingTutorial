package com.dacubeking.doodlejump.physics;

import com.badlogic.gdx.physics.box2d.Body;

public interface PlayerContactable {
    void onPlayerContact(Body playerBody);
}
