package com.example.gomoku;

import java.util.HashSet;
import java.util.Set;

public class ailogic {

    private gameLogic gameLogic;
    private int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};

    public ailogic(gameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public int[] aiMove() { // logic for the game

        // start of the game
        if (gameLogic.getBlackMoves().size() <= 2 && gameLogic.getWhiteMoves().size() <= 2) {
            return earlyGameMove();
        } // a move that wins the game
        int[] winningMove = findWinningMove();
        if (winningMove != null) {
            return winningMove;
        }
        // blocking player move
        int[] blockingMove = findBlockingMove();
        if (blockingMove != null) {
            return blockingMove;
        }
        // the best possible move
        int[] strategicMove = findStrategicMove();
        if (strategicMove != null) {
            return strategicMove;
        }
        return findRandomMove();
    }

    private boolean isValidMove(int row, int col) { // checker for outside bounderies and if the move is already made
        if (row < 0 || row >= gameLogic.GRID_SIZE || col < 0 || col >= gameLogic.GRID_SIZE) {
            return false;
        }
        String move = row + "," + col;
        return !gameLogic.getBlackMoves().contains(move) && !gameLogic.getWhiteMoves().contains(move);
    }
    private int[] findWinningMove() { // checks all moves to find if a move will win the game
        for (int i = 0; i < gameLogic.GRID_SIZE; i++) {
            for (int j = 0; j < gameLogic.GRID_SIZE; j++) {
                if (isValidMove(i, j)) {
                   // temporary moves
                    Set<String> blackMoves = new HashSet<>(gameLogic.getBlackMoves());
                    blackMoves.add(i + "," + j);

                    // check if makes a fiveinarow
                    if (wouldCreatePattern(i, j, blackMoves, "FiveInARow")) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
    }

    public int[] earlyGameMove() { // 2 first moves
        if (gameLogic.getBlackMoves().isEmpty()&& !gameLogic.getWhiteMoves().contains("6,6")) {
            return new int[]{5,6};
        } else if (gameLogic.getLastMove().isEmpty()) {
            int center = gameLogic.GRID_SIZE / 2;
            return new int[]{center, center};
        }
        String[] lastMove = gameLogic.getLastMove().split(",");
        int lastRow = Integer.parseInt(lastMove[0]);
        int lastCol = Integer.parseInt(lastMove[1]);

        // check if player played near the border
        if (lastRow <= 3 || lastCol <= 3 || lastRow >= 9 || lastCol >= 9) {
            return CloseBorderInteraction(lastRow, lastCol);
        }

        // find an open spot near the last move
        for (int[] dir : directions) {
            int newRow = lastRow + dir[0];
            int newCol = lastCol + dir[1];

            if (isValidMove(newRow, newCol)) {
                return new int[]{newRow, newCol};
            }
        }

        return findRandomMove();
    }

    private int[] findRandomMove() {
        int center = gameLogic.GRID_SIZE / 2;
        if (isValidMove(center, center)) {
            return new int[]{center, center};
        }
        for (String move : gameLogic.getBlackMoves()) { // try to check near moves already made
            String[] coords = move.split(",");
            int r = Integer.parseInt(coords[0]);
            int c = Integer.parseInt(coords[1]);
           for (int[] dir : directions) {
               for(int i = 1; i < 8; i++) {
                     int newRow = r + dir[0] * i;
                     int newCol = c + dir[1] * i;
                     if (isValidMove(newRow, newCol)) {
                          return new int[]{newRow, newCol};
                     }
                }
            }
        }

        // try cells close to the center
        for (int d = 1; d < gameLogic.GRID_SIZE; d++) {
            for (int i = center - d; i <= center + d; i++) {
                for (int j = center - d; j <= center + d; j++) {
                    if (Math.abs(i - center) == d || Math.abs(j - center )== d) {
                        if (isValidMove(i, j)) {
                            return new int[]{i, j};
                        }
                    }
                }
            }
        }

        // try all cells
        for (int i = 0; i < gameLogic.GRID_SIZE; i++) {
            for (int j = 0; j < gameLogic.GRID_SIZE; j++) {
                if (isValidMove(i, j)) {
                    return new int[]{i, j};
                }
            }
        }

        return new int[]{0, 0};
    }

    private int[] findBlockingMove() {
        // check all moves to block player from winning
        for (int i = 0; i < gameLogic.GRID_SIZE; i++) {
            for (int j = 0; j < gameLogic.GRID_SIZE; j++) {
                if (isValidMove(i, j)) {
                    // temporarily add this move
                    Set<String> whiteMoves = new HashSet<>(gameLogic.getWhiteMoves());
                    whiteMoves.add(i + "," + j);
                    // Check if this would create a winning pattern for the player
                    if (wouldCreatePattern(i, j, whiteMoves, "FiveInARow") || wouldCreatePattern(i, j, whiteMoves, "LiveFour")) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
    }
    private int[] findStrategicMove() {
        int bestScore = -1;
        int[] bestMove = null;
        // check all valid moves
        for (int i = 0; i < gameLogic.GRID_SIZE; i++) {
            for (int j = 0; j < gameLogic.GRID_SIZE; j++) {
                if (isValidMove(i, j)) {
                    int score = evaluateMove(i, j);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return bestMove;
    }
    private int evaluateMove(int row, int col) {
        int score = 0;
        Set<String> blackMoves = new HashSet<>(gameLogic.getBlackMoves());
        blackMoves.add(row + "," + col);

        // score pattern creation
        if (wouldCreatePattern(row, col, blackMoves, "LiveFour")) score += 100;
        else if (wouldCreatePattern(row, col, blackMoves, "DeadFour")) score += 50;
        else if (wouldCreatePattern(row, col, blackMoves, "LiveThree")) score += 30;
        else if (wouldCreatePattern(row, col, blackMoves, "DeadThree")) score += 10;
        else if (wouldCreatePattern(row, col, blackMoves, "LiveTwo")) score += 5;

        // extra if closer to middle
        int center = gameLogic.GRID_SIZE / 2;
        int distanceFromCenter = Math.abs(row - center) + Math.abs(col - center);
        score += Math.max(0, 10 - distanceFromCenter);

        return score;
    }

    private boolean wouldCreatePattern(int row, int col, Set<String> playerMoves, String targetPattern) {
        Set<String> tempPlayerMoves = new HashSet<>(playerMoves);
        tempPlayerMoves.add(row + "," + col);
        Set<String> opponentMoves;
        if (tempPlayerMoves.containsAll(gameLogic.getBlackMoves())) {
            // attacking
            opponentMoves = new HashSet<>(gameLogic.getWhiteMoves());
        } else {
            // blocking
            opponentMoves = new HashSet<>(gameLogic.getBlackMoves());
        }
        return simulatePattern(row, col, tempPlayerMoves, opponentMoves, targetPattern);
    }
    private boolean simulatePattern(int row, int col, Set<String> playerMoves, Set<String> opponentMoves, String targetPattern) {
        for (int[] dir : directions) {
            int count = 1;
            boolean openStart = false, openEnd = false;

            // check positive direction
            for (int i = 1; i < 5; i++) {
                int r = row + dir[0] * i, c = col + dir[1] * i;
                if (r < 0 || r >= gameLogic.GRID_SIZE || c < 0 || c >= gameLogic.GRID_SIZE ||
                        !playerMoves.contains(r + "," + c)) {
                    openEnd = r >= 0 && r < gameLogic.GRID_SIZE && c >= 0 && c < gameLogic.GRID_SIZE &&
                            !playerMoves.contains(r + "," + c) && !opponentMoves.contains(r + "," + c);
                    break;
                }
                count++;
            } // check negative direction
            for (int i = 1; i < 5; i++) {
                int r = row - dir[0] * i, c = col - dir[1] * i;
                if (r < 0 || r >= gameLogic.GRID_SIZE || c < 0 || c >= gameLogic.GRID_SIZE ||
                        !playerMoves.contains(r + "," + c)) {
                    openStart = r >= 0 && r < gameLogic.GRID_SIZE && c >= 0 && c < gameLogic.GRID_SIZE &&
                            !playerMoves.contains(r + "," + c) && !opponentMoves.contains(r + "," + c);
                    break;
                }
                count++;
            }

            String pattern = null;
            if (count >= 5) pattern = "FiveInARow";
            else if (count == 4) pattern = (openStart && openEnd) ? "LiveFour" : "DeadFour";
            else if (count == 3) pattern = (openStart && openEnd) ? "LiveThree" : "DeadThree";
            else if (count == 2) pattern = (openStart && openEnd) ? "LiveTwo" : "DeadTwo";

            if (pattern != null && pattern.equals(targetPattern)) {
                return true;
            }
        }
        return false;
    }
    public int[] CloseBorderInteraction(int row, int col) { // check if player is near the border
        int[] result = new int[]{row, col};

        // corner checkers
        if (row <= 3 && col <= 3) {
            result[0] = row + directions[2][0];
            result[1] = col + directions[2][1];
        }
        else if (row <= 3 && col >= 9) {
            result[0] = row + directions[2][0];
            result[1] = col - directions[2][1];
        }
        else if (row >= 9 && col <= 3) {
            result[0] = row - directions[2][0];
            result[1] = col + directions[2][1];
        }
        else if (row >= 9 && col >= 9) {
            result[0] = row - directions[2][0];
            result[1] = col - directions[2][1];
        }
        // close border checkers but not corners
        else if (row <= 3) {
            result[0] = row + 1;
            result[1] = col;
        }
        else if (row >= 9) {
            result[0] = row - 1;
            result[1] = col;
        }
        else if (col <= 3) {
            result[0] = row;
            result[1] = col + 1;
        }
        else if (col >= 9) {
            result[0] = row;
            result[1] = col - 1;
        }
        if (!isValidMove(result[0], result[1])) {
            int center = gameLogic.GRID_SIZE / 2;
            if (isValidMove(center, center)) {
                return new int[]{center, center};
            }
            return findRandomMove();
        }

        return result;
    }
}