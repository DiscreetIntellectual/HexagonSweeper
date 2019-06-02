package sweepFX.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import sweepFX.core.Board;
import sweepFX.core.Cell;

import java.util.ArrayList;

public class App extends Application {

    private int xCells = 9, yCells = 9;
    private int bombCount = 10;
    private String path = "file:\\D:\\Java Projects\\HexagonSweeper\\resources\\";
    private Board mainBoard;
    private Polygon[][] grid;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> center;
    private int radius = 20;

    public void start(Stage stage) throws Exception {
        mainBoard = new Board(xCells, yCells, bombCount);
        mainBoard.generate();
        grid = new Polygon[xCells][yCells];
        center = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
        for (int i = 0; i < xCells; i++) {
            center.add(new ArrayList<Pair<Integer, Integer>>());
            for (int j = 0; j < yCells; j++) {
                center.get(i).add(new Pair<Integer, Integer>(0, 0));
            }
        }
        for (int x = 0; x < xCells; x++) {
            for (int y = 0; y < yCells; y++) {

                Cell cell = mainBoard.getCell(x, y);
                int centerX, centerY;
                if (y % 2 == 0) {
                    centerX = (2 * x + 1) * radius;
                    centerY = (3 * y / 2 + 1) * radius;
                    center.get(x).set(y, new Pair<Integer, Integer>(centerX, centerY));

                }
                else {
                    centerX = (2 * x + 2) * radius;
                    centerY = (3 * y / 2 + 1) * radius + radius / 2;
                    center.get(x).set(y, new Pair<Integer, Integer>(centerX, centerY));
                }
                grid[x][y] = new Polygon(centerX, centerY - radius,
                        centerX + radius, centerY - radius / 2,
                        centerX + radius, centerY + radius / 2,
                        centerX, centerY + radius,
                        centerX - radius, centerY + radius / 2,
                        centerX - radius, centerY - radius / 2);
                grid[x][y].setFill(new ImagePattern(new Image(path + "closed.png")));
                grid[x][y].setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY)
                        openCell(cell);
                    else {
                        flag(cell);
                    }
                });
            }
        }
        Pane root = new Pane();

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem menuItem1 = new MenuItem("Простой (9х9, 10 бомб)");
        menuItem1.setOnAction(e -> {
            xCells = 9;
            yCells = 9;
            bombCount = 10;
            try {
                stage.close();
                start(new Stage());
            }
            catch (Exception except){
                System.out.println("Oops");
            }
        });
        MenuItem menuItem2 = new MenuItem("Средний (16х16, 35 бомб)");
        menuItem2.setOnAction(e -> {
            xCells = 16;
            yCells = 16;
            bombCount = 35;
            try {
                stage.close();
                start(new Stage());
            }
            catch (Exception except){
                System.out.println("Oops");
            }
        });
        MenuItem menuItem3 = new MenuItem("Эксперт (30х16, 80 бомб)");
        menuItem3.setOnAction(e -> {
            xCells = 30;
            yCells = 16;
            bombCount = 80;
            try {
                stage.close();
                start(new Stage());
            }
            catch (Exception except){
                System.out.println("Oops");
            }
        });

        menu.getItems().add(menuItem1);
        menu.getItems().add(menuItem2);
        menu.getItems().add(menuItem3);
        menuBar.getMenus().add(menu);
        root.getChildren().add(menuBar);
        menuBar.relocate((2 * radius + 2) * xCells - 70, 3 * radius / 2 * yCells + 22);

        for (int x = 0; x < xCells; x++) {
            for (int y = 0; y < yCells; y++) {
                root.getChildren().add(grid[x][y]);
            }
        }


        String statusText = String.format("0 flagged out of %d", bombCount);
        Text status = new Text(statusText);
        Task<Void> task = new Task<Void>() {
            String flagText = "0 flagged";

            @Override
            public Void call() throws Exception {
                int flagCount;

                while (true) {
                    flagCount = mainBoard.getFlagCount();
                    flagText = String.format("%d flagged out of %d", flagCount, bombCount);
                    Platform.runLater(() -> status.setText(flagText));
                    Thread.sleep(100);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        status.setFont(new Font("Courier New", 14));
        status.setY(3 * radius / 2 * yCells + 42);
        root.getChildren().add(status);



        stage.setTitle("YourSweeper");
        stage.setScene(new Scene(root, (2 * radius + 2) * xCells, 3 * radius / 2 * yCells + 50));
        stage.getIcons().add(new Image("file:\\D:\\Java Projects\\YoursSweeper\\resources\\bomb.jpg"));
        stage.show();
    }


    private void openCell(Cell cell) {
        Polygon polygon = grid[cell.getX()][cell.getY()];
        if (cell.getOpened() || mainBoard.getGameOver() || cell.getFlagged()) return;
        mainBoard.openCell(cell);
        if (cell.getBomb()) {
            polygon.setFill(new ImagePattern(new Image(path + "bomb.jpg")));
            return;
        }
        polygon.setFill(new ImagePattern(new Image(path + "bomb-" + Long.toString(cell.getNearBombs()) + ".png")));

        if (!cell.getBomb() && cell.getNearBombs() == 0) {
            mainBoard.getNeighbors(cell).forEach(this::openCell);
        }
    }

    private void flag(Cell cell) {
        Polygon polygon = grid[cell.getX()][cell.getY()];
        if (cell.getOpened() || mainBoard.getGameOver()) return;
        if (!cell.getFlagged()) {
            polygon.setFill(new ImagePattern(new Image(path + "flag.png")));
        }
        else {
            polygon.setFill(new ImagePattern(new Image(path + "closed.png")));
        }
        mainBoard.flag(cell);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
