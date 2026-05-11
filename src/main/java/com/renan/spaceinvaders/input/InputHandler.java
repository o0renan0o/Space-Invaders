package com.renan.spaceinvaders.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class InputHandler implements KeyListener {

    private final Set<Integer> down = ConcurrentHashMap.newKeySet();
    private final Set<Integer> pressed = ConcurrentHashMap.newKeySet();

    public boolean isDown(int keyCode) {
        return down.contains(keyCode);
    }

    public boolean consumePressed(int keyCode) {
        return pressed.remove(keyCode);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (down.add(code)) {
            pressed.add(code);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        down.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Unused - we work with key codes from press/release events.
    }
}
