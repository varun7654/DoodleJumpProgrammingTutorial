package com.dacubeking.doodlejump.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dacubeking.doodlejump.Constants;
import com.dacubeking.doodlejump.DoodleJump;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PhysicsWorld implements ContactListener {

    public static World world;

    public static void dispose() {
        if (world != null) {
            world.dispose();
        }
    }

    private static float accumulator = 0;

    private static final ArrayList<PhysicsTickable> physicsTickables = new ArrayList<>();

    public static void addToPhysicsTick(PhysicsTickable physicsTickableObject) {
        synchronized (physicsTickables) {
            physicsTickables.add(physicsTickableObject);
        }
    }

    public static void removeFromPhysicsTick(PhysicsTickable physicsTickableObject) {
        synchronized (physicsTickables) {
            physicsTickables.remove(physicsTickableObject);
        }
    }

    public static void doPhysicsStep(float deltaTime) {
        // We use a fixed time step to ensure the game physics is consistent across all hardware.
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.05f); // 0.05 (50ms/20fps) is the max amount of time to simulate each frame
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            PhysicsWorld.world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            accumulator -= Constants.TIME_STEP;
            // Copy the list to avoid Concurrent Modification Exceptions
            for (PhysicsTickable physicsTickable : new ArrayList<>(physicsTickables)) {
                physicsTickable.onPhysicsTick();
            }
        }
    }

    static PhysicsWorld physicsWorld = new PhysicsWorld();

    public static void init() {
        world = new World(new Vector2(0, -18), true);
        world.setContactListener(physicsWorld);
    }

    @Override
    public void beginContact(@NotNull Contact contact) {
        if (contact.getFixtureA().getBody() == DoodleJump.getPlayer().getPhysicsBody()) {
            if (contact.getFixtureB().getBody().getUserData() instanceof PlayerContactable playerContactable) {
                playerContactable.onPlayerContact(contact.getFixtureA().getBody());
            }
        } else if (contact.getFixtureB().getBody() == DoodleJump.getPlayer().getPhysicsBody()) {
            if (contact.getFixtureA().getBody().getUserData() instanceof PlayerContactable playerContactable) {
                playerContactable.onPlayerContact(contact.getFixtureB().getBody());
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
