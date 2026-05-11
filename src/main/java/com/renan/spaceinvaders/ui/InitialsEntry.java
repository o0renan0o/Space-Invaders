package com.renan.spaceinvaders.ui;

public final class InitialsEntry {

    private final char[] letters = {'A', 'A', 'A'};
    private int cursor;

    public void prev() {
        letters[cursor] = letters[cursor] == 'A' ? 'Z' : (char) (letters[cursor] - 1);
    }

    public void next() {
        letters[cursor] = letters[cursor] == 'Z' ? 'A' : (char) (letters[cursor] + 1);
    }

    public void left() {
        cursor = Math.max(0, cursor - 1);
    }

    public void right() {
        cursor = Math.min(letters.length - 1, cursor + 1);
    }

    public boolean advance() {
        if (cursor < letters.length - 1) {
            cursor++;
            return false;
        }
        return true;
    }

    public int getCursor() {
        return cursor;
    }

    public char[] getLetters() {
        return letters;
    }

    public String asString() {
        return new String(letters);
    }

    public void reset() {
        letters[0] = letters[1] = letters[2] = 'A';
        cursor = 0;
    }
}
