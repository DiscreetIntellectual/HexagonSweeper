package sweepFX.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {

    private int xCells = 5, yCells = 5;
    private int bombCount = 8;
    private int flagCount = 0;
    private boolean gameOver = false;
    private Cell[][] grid;

    public Board(int xCells, int yCells, int bombCount) {
        this.xCells = xCells;
        this.yCells = yCells;
        this.bombCount = bombCount;
        grid = new Cell[xCells][yCells];
    }

    public void generate() {

        Random random1 = new Random();
        Random random2 = new Random();

        for (int x = 0; x < xCells; x++) {
            for (int y = 0; y < yCells; y++) {
                Cell cell = new Cell(x, y, false);
                grid[x][y] = cell;
            }
        }

        for (int i = 0; i < bombCount; i++) {
            int x = random1.nextInt(xCells);
            int y = random2.nextInt(yCells);
            while (grid[x][y].getBomb()) {
                x = random1.nextInt(xCells);
                y = random2.nextInt(yCells);
            }
            grid[x][y].setBomb(true);
        }

        for (int x = 0; x < xCells; x++) {
            for (int y = 0; y < yCells; y++) {
                Cell cell = grid[x][y];
                if (cell.getBomb()) {
                    continue;
                }
                long bombsTemp = getNeighbors(cell).stream().filter(e -> e.getBomb()).count();
                cell.setNearBombs(bombsTemp);
            }
        }
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }

    public int getFlagCount() {
        return flagCount;
    }

    public boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver() {
        gameOver = true;
    }

    //neighbors for even row: (x-1, y); (x, y-1); (x+1, y-1); (x+1, y); (x+1;y+1); (x, y+1)
    //neighbors for uneven row: (x-1, y); (x-1, y-1); (x, y-1); (x+1, y); (x, y+1); (x-1, y+1)

    public List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int[] deltas1 = {-1, 0, -1, -1, 0, -1, 1, 0, 0, 1, -1, 1};
        int[] deltas2 = {-1, 0, 0, -1, 1, -1, 1, 0, 1, 1, 0, 1};
        if (cell.getY() % 2 == 1) {
            for (int i = 0; i < 11; i += 2) {
                int x = cell.getX() + deltas2[i];
                int y = cell.getY() + deltas2[i + 1];
                if (x >= 0 && x < xCells && y >= 0 && y < yCells)
                    neighbors.add(grid[x][y]);
            }
        }
        else {
            for (int i = 0; i < 11; i += 2) {
                int x = cell.getX() + deltas1[i];
                int y = cell.getY() + deltas1[i + 1];
                if (x >= 0 && x < xCells && y >= 0 && y < yCells)
                    neighbors.add(grid[x][y]);
            }
        }
        return neighbors;
    }

    public void openCell(Cell cell) {
        //if (cell.getOpened() || gameOver || cell.getFlagged()) return;
        cell.setOpened(true);
        if (cell.getBomb()) {
            gameOver = true;
        }
    }

    public void flag(Cell cell) {
        //if (cell.getOpened() || gameOver) return;
        if (!cell.getFlagged()) {
            cell.setFlagged(true);
            flagCount++;
        }
        else {
            cell.setFlagged(false);
            flagCount--;
        }
    }
}
