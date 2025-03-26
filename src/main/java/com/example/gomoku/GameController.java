package com.example.gomoku;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;

import java.io.Console;

public class GameController {
    @FXML
    private GridPane gridPane;

    private static final int GRID_SIZE = 13;
    private static final double CELL_SIZE = 39;
    private gameLogic logic = new gameLogic();
    private ailogic ai = new ailogic(logic);

    @FXML
    public ImageView boardImage;

    @FXML
    public void initialize() {
        Image image = new Image(getClass().getResourceAsStream("board.jpg"));
        boardImage.setImage(image);
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setStyle("-fx-border-color: transparent;");
                final int r = row;
                final int c = col;
                if (logic.isBlackTurn()) {
                    int[] aiMove = ai.aiMove();
                    placeAIPiece( aiMove[0], aiMove[1]);
                    logic.changeTurn();
                }
                else{
                    cell.setOnMouseClicked(event -> placePiece(event, r, c));
                }
                gridPane.add(cell, col, row);

            }
        }
    }

    private void placePiece(MouseEvent event, int row, int col) {
        if (!logic.placePiece(row, col)) {
            return;
        }
        StackPane cell = (StackPane) gridPane.getChildren().get(row * GRID_SIZE + col);
        Circle piece = new Circle(12);
        piece.setFill(Color.WHITE);
        cell.getChildren().add(piece);
        logic.SetLastMove(row, col);
        String StateFound = logic.StatePosition(row, col);
        if(StateFound!=null){
            System.out.println(StateFound);
        }
        int winner = logic.checkWin(row, col);
        if (winner == 1 || winner == 2) {
            showWinnerAlert(winner);
            resetGame();
        }
        logic.changeTurn();

    }

    private void placeAIPiece( int row, int col) {
        StackPane cell = (StackPane) gridPane.getChildren().get(row * GRID_SIZE + col);
        Circle piece = new Circle(12);
        piece.setFill(Color.BLACK);
        cell.getChildren().add(piece);
        String StateFound = logic.StatePosition(row, col);
        if(StateFound!=null){
            System.out.println(StateFound);
        }
        int winner = logic.checkWin(row, col);
        if (winner == 1 || winner == 2) {
            showWinnerAlert(winner);
            resetGame();
        }
    }

    private void showWinnerAlert(int winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText((winner == 1 ? "Black" : "White") + " wins!");
        alert.showAndWait();
    }

    private void resetGame() {
        gridPane.getChildren().clear();
        logic = new gameLogic();
        initialize();
    }
}
