package minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Minesweeper {
    private char[][] solutionBoard;
    private char[][] playBoard;
    private final int boardHeight;
    private final int boardWidth;
    private final int numMines;
    private int numUncovered;
    private boolean gameOver;
    private boolean playerWin;
    private ArrayList<Integer> minePositions;
    private ArrayList<Integer> foundMinePositions;
    private ArrayList<Integer> extraFlags;

    public Minesweeper(int boardWidth, int boardHeight, int numMines) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.numMines = numMines;
        this.numUncovered = boardHeight * boardWidth - numMines;
        extraFlags = new ArrayList<>();
        gameOver = false;
        createBoard();
        printBoard();
    }

    public boolean doMove(int y, int x) {
        y--;
        x--;

        if (numUncovered == boardWidth * boardHeight - numMines) {
            populateBoard(getPosFromCoords(y, x));
        }

        char ch = playBoard[y][x];
        if (ch == '.' || ch == '*') {
            numUncovered--;
            char tileValue = solutionBoard[y][x];
            playBoard[y][x] = tileValue;
            if (tileValue == '/') {
                for (int i = y - 1; i < y + 2; i++) {
                    if (i >= 0 && i < boardHeight) {
                        for (int j = x - 1; j < x + 2; j++) {
                            if (j >= 0 && j < boardWidth) {
                                doMove(i + 1, j + 1);
                            }
                        }
                    }
                }
            } else if (tileValue == 'X') {
                uncoverAllMines();
                gameOver = true;
                playerWin = false;
            }
        } else {
            return false;
        }
        checkGameOver();
        return true;
    }

    private void uncoverAllMines() {
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                if (solutionBoard[i][j] == 'X') {
                    playBoard[i][j] = 'X';
                }
            }
        }
    }

    public boolean placeFlag(int y, int x) {
        x--;
        y--;
        char ch = playBoard[y][x];
        int pos = getPosFromCoords(y, x);
        if (ch == '.') {
            playBoard[y][x] = '*';
            if (minePositions.contains(pos)) {
                foundMinePositions.add(pos);
                minePositions.remove(Integer.valueOf(pos));
            } else {
                extraFlags.add(pos);
            }
        } else if (ch == '*') {
            playBoard[y][x] = '.';
            if (foundMinePositions.contains(pos)) {
                minePositions.add(pos);
                foundMinePositions.remove(Integer.valueOf(pos));
            } else if (extraFlags.contains(pos)) {
                extraFlags.remove(Integer.valueOf(pos));
            } else {
                System.out.println("This space should not have been flagged..." + x + " " + y);
            }
        } else {
            System.out.println("This tile has already been uncovered!");
            return false;
        }
        checkGameOver();
        return true;
    }

    private int getPosFromCoords(int y, int x) {
        return y * boardWidth + x;
    }

    private void checkGameOver() {
        if (numUncovered == 0 || (minePositions.isEmpty() && extraFlags.isEmpty())) {
            gameOver = true;
            playerWin = true;
        }
    }

    private void updateAllNeighbors() {
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                if (solutionBoard[i][j] != 'X') {
                    int numNeighbors = getNumNeighbors(i, j);
                    solutionBoard[i][j] = numNeighbors > 0 ? Character.forDigit(numNeighbors, 10) : solutionBoard[i][j];
                }
            }
        }
    }

    private int getNumNeighbors(int y, int x) {
        int numNeighbors = 0;
        for (int i = y - 1; i < y + 2; i++) {
            if (i >= 0 && i < boardHeight) {
                for (int j = x - 1; j < x + 2; j++) {
                    if (j >= 0 && j < boardWidth) {
                        if (solutionBoard[i][j] == 'X') {
                            numNeighbors++;
                        }
                    }
                }
            }
        }
        return numNeighbors;
    }

    private void createBoard() {
        solutionBoard = new char[boardHeight][boardWidth];
        playBoard = new char[boardHeight][boardWidth];
        minePositions = new ArrayList<>();
        foundMinePositions = new ArrayList<>();
        for (int i = 0; i < boardHeight; i++) {
            Arrays.fill(solutionBoard[i], '/');
            Arrays.fill(playBoard[i], '.');
        }
    }

    private void populateBoard(int safePos) {
        Random random = new Random();
        for (int i = 0; i < numMines; i++) {
            int pos = random.nextInt(boardHeight * boardWidth);
            if (minePositions.contains(pos) || pos == safePos) {
                i--;
            } else {
                minePositions.add(pos);
            }
        }

        for (int i : minePositions) {
            int posX = i % boardWidth;
            int posY = i / boardWidth;
            solutionBoard[posY][posX] = 'X';
        }
        updateAllNeighbors();
    }

    public void printBoard() {
        System.out.println();
        System.out.print(" |");
        for (int i = 1; i <= boardWidth; i++) {
            System.out.print(i);
        }
        System.out.println("|");
        System.out.println("-|" + "-".repeat(boardWidth)  + "|");
        int rowCount = 1;
        for (char[] row : playBoard) {
            System.out.print(rowCount + "|");
            rowCount++;
            for (char ch : row) {
                System.out.print(ch);
            }
            System.out.println("|");
        }
        System.out.println("-|" + "-".repeat(boardWidth)  + "|");
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPlayerWin() {
        return playerWin;
    }
}
