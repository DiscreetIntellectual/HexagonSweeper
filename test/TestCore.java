import org.junit.jupiter.api.*;
import sweepFX.core.Board;
import sweepFX.core.Cell;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TestCore {
    Board board = new Board(10, 10, 10);

    @Test
    void test1() {
        board.generate();
        Random random1 = new Random();
        Random random2 = new Random();
        for (int i = 0; i < 10; i++) {
            int x = random1.nextInt(10);
            int y = random2.nextInt(10);
            boolean flag = board.getCell(x, y).getFlagged();
            board.flag(board.getCell(x, y));
            assertEquals(board.getCell(x, y).getFlagged(), !flag);
            assertTrue(!board.getGameOver());
        }
    }

    @Test
    void test2() {
        board.generate();
        Random random1 = new Random();
        Random random2 = new Random();
        int x = random1.nextInt(10);
        int y = random2.nextInt(10);
        List<Cell> neighbors = board.getNeighbors(board.getCell(x, y));
        for (Cell cell: neighbors) {
            assertTrue(board.getNeighbors(cell).contains(board.getCell(x, y)));
        }
    }

    @Test
    void test3() {
        board.generate();
        Random random1 = new Random();
        Random random2 = new Random();
        for (int i = 0; i < 10; i++) {
            int x = random1.nextInt(10);
            int y = random2.nextInt(10);
            if (!board.getCell(x, y).getBomb()) {
                board.openCell(board.getCell(x, y));
                assertTrue(board.getCell(x, y).getOpened());
                assertFalse(board.getCell(x, y).getFlagged());
            } else {
                assertEquals(board.getCell(x, y).getNearBombs(), 0);
            }
        }
        assertFalse(board.getGameOver());
    }
}
