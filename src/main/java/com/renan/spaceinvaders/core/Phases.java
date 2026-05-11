package com.renan.spaceinvaders.core;

import java.util.List;

public final class Phases {

    public static final List<Phase> LIST = List.of(
            Phase.of(1, "PATRULHA", 4, 8, 0.8, 50, 130, 1, 4, 100),
            Phase.of(2, "PRIMEIRA ONDA", 5, 10, 1.0, 40, 120, 1, 4, 200),
            Phase.of(3, "REDE CERRADA", 5, 10, 1.2, 35, 110, 1, 4, 300,
                    Mechanic.NARROW_FORMATION),
            Phase.of(4, "BOMBARDEIROS", 5, 10, 1.2, 35, 100, 2, 4, 400,
                    Mechanic.MULTIPLE_BULLETS),
            Phase.of(5, "MERGULHADORES", 5, 10, 1.4, 35, 110, 2, 3, 500,
                    Mechanic.DIVERS, Mechanic.BOSS),
            Phase.of(6, "ESTILHACO", 5, 10, 1.4, 35, 105, 2, 3, 600,
                    Mechanic.SPLITTING_BULLETS),
            Phase.of(7, "BLINDADOS", 5, 10, 1.6, 30, 100, 2, 3, 700,
                    Mechanic.ARMORED_FRONT_ROWS),
            Phase.of(8, "TEMPESTADE", 5, 10, 1.8, 25, 90, 3, 2, 800,
                    Mechanic.MULTIPLE_BULLETS, Mechanic.FEWER_SHIELDS),
            Phase.of(9, "ESCOLTA UFO", 5, 10, 1.8, 30, 95, 2, 3, 900,
                    Mechanic.FREQUENT_UFO),
            Phase.of(10, "INVESTIDA FINAL", 5, 10, 2.2, 22, 80, 3, 2, 1000,
                    Mechanic.DIVERS, Mechanic.SPLITTING_BULLETS,
                    Mechanic.ARMORED_FRONT_ROWS, Mechanic.BOSS)
    );

    private Phases() {
    }

    public static Phase forWave(int wave) {
        if (wave <= LIST.size()) return LIST.get(wave - 1);
        Phase last = LIST.get(LIST.size() - 1);
        int loops = (wave - 1) / LIST.size();
        double speed = last.speedMultiplier() + loops * 0.15;
        int fireMin = Math.max(15, last.fireMinTicks() - loops * 2);
        int fireMax = Math.max(40, last.fireMaxTicks() - loops * 4);
        int bonus = last.clearBonus() + loops * 100;
        return new Phase(wave, "ENDLESS +" + loops,
                last.rows(), last.cols(),
                speed, fireMin, fireMax,
                last.simultaneousAlienBullets(),
                last.shieldCount(),
                last.mechanics(),
                bonus);
    }
}
