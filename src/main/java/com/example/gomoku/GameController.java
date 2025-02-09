package com.example.gomoku;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class GameController {
    @FXML
    private GridPane gridPane;

    private static final int GRID_SIZE = 15;
    private static final int CELL_SIZE = 35;
    private boolean isBlackTurn = true;
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
                cell.setOnMouseClicked(event -> placePiece(event, r, c));
                gridPane.add(cell, col, row);
            }
        }
    }

    private void placePiece(MouseEvent event, int row, int col) {
        StackPane cell = (StackPane) gridPane.getChildren().get(row * GRID_SIZE + col);
        if (!cell.getChildren().isEmpty()) {
            return;
        }
        Circle piece = new Circle(12);
        piece.setFill(isBlackTurn ? Color.BLACK : Color.WHITE);
        cell.getChildren().add(piece);
        isBlackTurn = !isBlackTurn;

    }
}
