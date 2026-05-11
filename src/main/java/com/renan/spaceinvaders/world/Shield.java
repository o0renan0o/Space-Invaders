package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Shield implements Entity {

    private final int x;
    private final int y;
    private final boolean[][] cells;

    public Shield(int x, int y) {
        this.x = x;
        this.y = y;
        this.cells = new boolean[GameConfig.SHIELD_ROWS][GameConfig.SHIELD_COLS];
        for (int r = 0; r < GameConfig.SHIELD_ROWS; r++) {
            for (int c = 0; c < GameConfig.SHIELD_COLS; c++) {
                cells[r][c] = isInsideShape(r, c);
            }
        }
    }

    private static boolean isInsideShape(int r, int c) {
        int cols = GameConfig.SHIELD_COLS;
        int rows = GameConfig.SHIELD_ROWS;
        if (r >= rows - 3 && c >= cols / 2 - 2 && c <= cols / 2 + 1) return false;
        if (r == 0 && (c < 2 || c >= cols - 2)) return false;
        if (r == 1 && (c < 1 || c >= cols - 1)) return false;
        return true;
    }

    public boolean[][] getCells() {
        return cells;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int widthPx() {
        return GameConfig.SHIELD_COLS * GameConfig.SHIELD_CELL;
    }

    public int heightPx() {
        return GameConfig.SHIELD_ROWS * GameConfig.SHIELD_CELL;
    }

    public boolean handleHit(Rectangle bulletBounds, Bullet.Side side) {
        Rectangle shieldBounds = getBounds();
        if (!shieldBounds.intersects(bulletBounds)) return false;
        int cell = GameConfig.SHIELD_CELL;
        int hitCol = -1;
        int hitRow = -1;
        int bestDistance = Integer.MAX_VALUE;
        int bx = bulletBounds.x + bulletBounds.width / 2;
        for (int r = 0; r < GameConfig.SHIELD_ROWS; r++) {
            for (int c = 0; c < GameConfig.SHIELD_COLS; c++) {
                if (!cells[r][c]) continue;
                int cx = x + c * cell;
                int cy = y + r * cell;
                Rectangle cellRect = new Rectangle(cx, cy, cell, cell);
                if (cellRect.intersects(bulletBounds)) {
                    int distance = Math.abs(cx - bx)
                            + (side == Bullet.Side.PLAYER ? (GameConfig.SHIELD_ROWS - r) : r);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        hitCol = c;
                        hitRow = r;
                    }
                }
            }
        }
        if (hitCol < 0) return false;
        eroseAround(hitRow, hitCol);
        return true;
    }

    private void eroseAround(int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = row + dr;
                int c = col + dc;
                if (r < 0 || r >= GameConfig.SHIELD_ROWS) continue;
                if (c < 0 || c >= GameConfig.SHIELD_COLS) continue;
                if (Math.abs(dr) + Math.abs(dc) > 1) continue;
                cells[r][c] = false;
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, widthPx(), heightPx());
    }

    @Override
    public boolean isAlive() {
        for (boolean[] row : cells) {
            for (boolean cell : row) if (cell) return true;
        }
        return false;
    }
}
