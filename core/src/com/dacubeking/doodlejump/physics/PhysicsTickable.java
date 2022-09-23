package com.dacubeking.doodlejump.physics;

import com.dacubeking.doodlejump.Constants;

public interface PhysicsTickable {

    /**
     * This is called every physics tick. The time step is guaranteed to be constant ({@link Constants#TIME_STEP}).
     */
    void onPhysicsTick();
}
