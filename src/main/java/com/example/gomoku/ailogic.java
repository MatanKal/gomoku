package com.example.gomoku;

import java.util.HashSet;
import java.util.Set;

public class ailogic {

    private gameLogic gameLogic;
    private int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
    public ailogic(gameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }


    public int[] aiMove() {
        int[] arr = new int[2];
        int row = 0;
        int col = 0;
        boolean found = false;
        String[] LBmove = gameLogic.getLastMove().split(",");
        if(gameLogic.getWhiteMoves().size() <=2  && gameLogic.getBlackMoves().size() <= 2) {
            row = Integer.parseInt(LBmove[0]);
            col = Integer.parseInt(LBmove[1]);
            if(row<=3 || col<=3|| row>=9 || col>=9) {
                return CloseBorderInteraction(row, col);
            }
            for(int i = 0; i <= 2; i++) {
                for (int[] dir : directions) {
                    if (gameLogic.placePiece(row + dir[0], col + dir[1])) {
                        row = row + dir[0];
                        col = col + dir[1];
                        found=true;
                        break;
                    }
                }

            }
            gameLogic.placePiece(row, col);
            arr[0] = row;
            arr[1] = col;
            return arr;
        }
        for (int i = 0; i < gameLogic.GRID_SIZE; i++) {
            for (int j = 0; j < gameLogic.GRID_SIZE; j++) {
                if (gameLogic.placePiece(i, j)) {
                    row = i;
                    col = j;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        gameLogic.placePiece(row, col);
        arr[0] = row;
        arr[1] = col;
        return arr;
    }
    public int[] CloseBorderInteraction(int row, int col){
        if(row<=3&&col<=3){
            row = row + directions[2][0];
            col = col + directions[2][1];
        }
        else if(row<=3&&col>=9){
            row = row + directions[2][0];
            col = col - directions[2][1];
        }
        else if(row>=9&&col<=3){
            row = row - directions[2][0];
            col = col + directions[2][1];
        }
        else if(row>=9&&col>=9){
            row = row - directions[2][0];
            col = col - directions[2][1];
        }
        return new int[]{row, col};
    }
}
