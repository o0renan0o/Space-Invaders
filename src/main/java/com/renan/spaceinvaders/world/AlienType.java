package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

public enum AlienType {
    SQUID("alien_squid", GameConfig.SCORE_PER_ALIEN_ROW_0, 1),
    CRAB("alien_crab", GameConfig.SCORE_PER_ALIEN_ROW_1, 1),
    OCTOPUS("alien_octopus", GameConfig.SCORE_PER_ALIEN_ROW_DEFAULT, 1),
    ARMORED("alien_armored", 50, 2),
    DIVER("alien_diver", 50, 1);

    public final String spriteKey;
    public final int score;
    public final int hp;

    AlienType(String spriteKey, int score, int hp) {
        this.spriteKey = spriteKey;
        this.score = score;
        this.hp = hp;
    }

    public static AlienType forRow(int row, int totalRows, boolean armoredFrontRows) {
        if (armoredFrontRows && row < 2) return ARMORED;
        if (row == 0) return SQUID;
        if (row == 1) return CRAB;
        return OCTOPUS;
    }
}
