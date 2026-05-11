package com.renan.spaceinvaders.ui;

public enum FaceState {
    FORWARD("face_forward"),
    LEFT("face_left"),
    RIGHT("face_right"),
    ATTACK("face_attack"),
    HURT("face_hurt"),
    EVIL_GRIN("face_attack"),
    WIN("face_win"),
    DEAD("face_dead");

    public final String spriteKey;

    FaceState(String spriteKey) {
        this.spriteKey = spriteKey;
    }
}
