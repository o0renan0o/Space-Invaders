package com.renan.spaceinvaders.world;

import java.awt.Rectangle;

public interface Entity {
    Rectangle getBounds();

    boolean isAlive();
}
