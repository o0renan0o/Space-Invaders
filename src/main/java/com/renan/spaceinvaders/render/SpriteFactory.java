package com.renan.spaceinvaders.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public final class SpriteFactory {

    public static final Color SQUID_COLOR = new Color(0x66FF66);
    public static final Color CRAB_COLOR = new Color(0x66CCFF);
    public static final Color OCTOPUS_COLOR = new Color(0xFF66CC);
    public static final Color ARMORED_COLOR = new Color(0xFFFFFF);
    public static final Color ARMORED_OUTLINE = new Color(0xFF3333);
    public static final Color DIVER_COLOR = new Color(0xFFCC33);
    public static final Color BOSS_COLOR = new Color(0xFF3333);
    public static final Color BOSS_OUTLINE = new Color(0xFFFFFF);

    private static final String[] SQUID_A = pad(new String[]{
            "   ##",
            "  ####",
            " ######",
            "## ## ##",
            "########",
            "  #  #",
            " # ## #",
            "# #  # #"
    }, 8);
    private static final String[] SQUID_B = pad(new String[]{
            "   ##",
            "  ####",
            " ######",
            "## ## ##",
            "########",
            " # ## #",
            "#  ##  #",
            " #    #"
    }, 8);

    private static final String[] CRAB_A = pad(new String[]{
            " #     #",
            "  #   #",
            " #######",
            "## ### ##",
            "#########",
            "# ##### #",
            "# #   # #",
            "  ## ##"
    }, 9);
    private static final String[] CRAB_B = pad(new String[]{
            " #     #",
            "# #   # #",
            "#####  ##",
            "## #####",
            "#########",
            "# ##### #",
            "  #   #",
            " #     #"
    }, 9);

    private static final String[] OCTOPUS_A = pad(new String[]{
            "    ####",
            "  ########",
            " ##########",
            "### ## ## ##",
            "############",
            " ##  ##  ##",
            " #  ####  #",
            "  ##    ##"
    }, 12);
    private static final String[] OCTOPUS_B = pad(new String[]{
            "    ####",
            "  ########",
            " ##########",
            "### ## ## ##",
            "############",
            "  ##    ##",
            " #  ####  #",
            " ##      ##"
    }, 12);

    private static final String[] DIVER_A = pad(new String[]{
            "   ##",
            "  ####",
            "########",
            "#  ##  #",
            "########",
            "# #### #",
            " #    #",
            "  ####"
    }, 8);
    private static final String[] DIVER_B = pad(new String[]{
            "   ##",
            "  ####",
            "########",
            "## ## ##",
            "########",
            "  #  #",
            " # ## #",
            "##    ##"
    }, 8);

    private static final String[] BOSS = pad(new String[]{
            "        ################",
            "      ####################",
            "    ########################",
            "   ##########################",
            " #############################",
            "###############################",
            "##### ### ### ### ### ### #####",
            "###                         ###",
            "##  ###  ###  ###  ###  ###  ##",
            "##                             ##",
            " ###    ###     ###     ###",
            "   ###                    ###",
            "     #####            #####",
            "        ##############"
    }, 32);

    private static final String[] UFO = pad(new String[]{
            "    ########",
            "   ##########",
            "################",
            "## ## ## ## ## #",
            "################",
            "  ###      ###"
    }, 16);

    private static final String[] SHIP = pad(new String[]{
            "       ##",
            "       ##",
            "      ####",
            "      ####",
            "  ############",
            "################",
            "################",
            "################"
    }, 16);

    private static final String[] HEART = pad(new String[]{
            " ## ##",
            "#######",
            "#######",
            " #####",
            "  ###",
            "   #"
    }, 7);

    private static final String[] RAPID = pad(new String[]{
            " #    #",
            " ##   ##",
            " ### ###",
            " ##   ##",
            " #    #"
    }, 8);

    private static final String[] DOUBLE = pad(new String[]{
            " ##  ##",
            " ##  ##",
            " ##  ##",
            " ##  ##",
            "########",
            "########"
    }, 8);

    private static final String[] PIERCE = pad(new String[]{
            "    #",
            "   ###",
            "  #####",
            " #######",
            "    #",
            "    #",
            "    #",
            "    #"
    }, 9);

    private static final String[] CLOCK = pad(new String[]{
            "   ####",
            "  #    #",
            " #      #",
            "#   ##   #",
            "#   ##   #",
            "#   ##   #",
            "#        #",
            " #      #",
            "  #    #",
            "   ####"
    }, 10);

    private static final String[] SHIELD_ICON = pad(new String[]{
            "  ####",
            " ######",
            "########",
            "########",
            "########",
            "########",
            " ######",
            "  ####"
    }, 8);

    private SpriteFactory() {
    }

    private static String[] pad(String[] rows, int width) {
        String[] out = new String[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String r = rows[i];
            if (r.length() < width) {
                StringBuilder sb = new StringBuilder(r);
                while (sb.length() < width) sb.append(' ');
                out[i] = sb.toString();
            } else if (r.length() > width) {
                out[i] = r.substring(0, width);
            } else {
                out[i] = r;
            }
        }
        return out;
    }

    public static Map<String, BufferedImage> buildAll() {
        Map<String, BufferedImage> map = new HashMap<>();
        int alienScale = 3;

        map.put("squid_0", PixelArt.render(SQUID_A, alienScale, SQUID_COLOR));
        map.put("squid_1", PixelArt.render(SQUID_B, alienScale, SQUID_COLOR));

        map.put("crab_0", PixelArt.render(CRAB_A, alienScale, CRAB_COLOR));
        map.put("crab_1", PixelArt.render(CRAB_B, alienScale, CRAB_COLOR));

        map.put("octopus_0", PixelArt.render(OCTOPUS_A, alienScale, OCTOPUS_COLOR));
        map.put("octopus_1", PixelArt.render(OCTOPUS_B, alienScale, OCTOPUS_COLOR));

        map.put("armored_0", PixelArt.outlined(CRAB_A, alienScale, ARMORED_COLOR, ARMORED_OUTLINE));
        map.put("armored_1", PixelArt.outlined(CRAB_B, alienScale, ARMORED_COLOR, ARMORED_OUTLINE));

        map.put("diver_0", PixelArt.render(DIVER_A, alienScale, DIVER_COLOR));
        map.put("diver_1", PixelArt.render(DIVER_B, alienScale, DIVER_COLOR));

        map.put("boss", PixelArt.outlined(BOSS, 4, BOSS_COLOR, BOSS_OUTLINE));
        map.put("ufo_proc", PixelArt.render(UFO, 4, new Color(0xFF66AA)));
        map.put("ship_proc", PixelArt.render(SHIP, 3, new Color(0x33FF66)));

        map.put("pu_extra_life", PixelArt.render(HEART, 3, new Color(0xFF6688)));
        map.put("pu_rapid", PixelArt.render(RAPID, 3, new Color(0xFFCC33)));
        map.put("pu_double", PixelArt.render(DOUBLE, 3, new Color(0x66FFFF)));
        map.put("pu_pierce", PixelArt.render(PIERCE, 3, new Color(0xFFFFFF)));
        map.put("pu_slow", PixelArt.render(CLOCK, 3, new Color(0xCCCCFF)));
        map.put("pu_shield", PixelArt.render(SHIELD_ICON, 3, new Color(0x66FF77)));

        return map;
    }
}
