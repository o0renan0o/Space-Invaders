package com.renan.spaceinvaders.core;

import java.util.EnumSet;
import java.util.Set;

public record Phase(
        int number,
        String name,
        int rows,
        int cols,
        double speedMultiplier,
        int fireMinTicks,
        int fireMaxTicks,
        int simultaneousAlienBullets,
        int shieldCount,
        Set<Mechanic> mechanics,
        int clearBonus
) {
    public boolean has(Mechanic m) {
        return mechanics.contains(m);
    }

    public static Phase of(int number, String name,
                           int rows, int cols,
                           double speedMul,
                           int fireMin, int fireMax,
                           int simultaneous,
                           int shields,
                           int clearBonus,
                           Mechanic... mechanics) {
        Set<Mechanic> set = mechanics.length == 0
                ? EnumSet.noneOf(Mechanic.class)
                : EnumSet.copyOf(java.util.Arrays.asList(mechanics));
        return new Phase(number, name, rows, cols, speedMul,
                fireMin, fireMax, simultaneous, shields, set, clearBonus);
    }
}
