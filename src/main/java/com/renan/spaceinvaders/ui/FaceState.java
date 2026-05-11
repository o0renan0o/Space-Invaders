package com.renan.spaceinvaders.ui;

import com.renan.spaceinvaders.assets.FaceSheet;

public enum FaceState {
    FORWARD("face_forward", FaceSheet.COL_IDLE),
    LEFT("face_left", FaceSheet.COL_LOOK_L),
    RIGHT("face_right", FaceSheet.COL_LOOK_R),
    ATTACK("face_attack", FaceSheet.COL_RAMPAGE),
    HURT("face_hurt", FaceSheet.COL_OUCH),
    EVIL_GRIN("face_attack", FaceSheet.COL_EVIL),
    WIN("face_win", FaceSheet.COL_EVIL),
    DEAD("face_dead", FaceSheet.COL_EVIL2),
    GOD("face_forward", FaceSheet.COL_EVIL2);

    public final String spriteKey;
    public final int doomColumn;

    FaceState(String spriteKey, int doomColumn) {
        this.spriteKey = spriteKey;
        this.doomColumn = doomColumn;
    }
}
