package sweepFX.core;

public class Cell {

    private final int x, y;
    private boolean bomb;
    private boolean opened = false;
    private boolean flagged = false;
    private long nearBombs;


    public Cell(int x, int y, boolean bomb) {
        this.x = x;
        this.y = y;
        this.bomb = bomb;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getBomb() {
        return bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public long getNearBombs() {
        return nearBombs;
    }

    public void setNearBombs(long numberOfBombs) {
        nearBombs = numberOfBombs;
    }

    public boolean getOpened() {
        return opened;
    }

    public void setOpened(boolean bool) {
        opened = bool;
    }

    public boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(boolean bool) {
        flagged = bool;
    }
}
