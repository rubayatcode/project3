import java.util.Scanner;

public class Connect4 {
    public static void main(String[] args) {
        Game game = new Game(4, 5);
        game.play();
    }
}

class Game {
    private final Board board;
    private char currentPlayer;
    private final Scanner scanner;

    public Game(int rows, int cols) {
        this.board = new Board(rows, cols);
        this.currentPlayer = 'X';
        this.scanner = new Scanner(System.in);
    }

    public void play() {
        System.out.println("Welcome to Connect 4 - PopOut variation!");
        System.out.println("Player 1: X   Player 2: O");
        board.printBoard();

        while (true) {
            System.out.println("\nPlayer " + getPlayerNumber(currentPlayer) +
                               " (" + currentPlayer + ") - your turn.");
            System.out.println("Choose move: (D)rop or (P)op out from bottom");
            String moveType = scanner.nextLine().trim().toUpperCase();
            if (moveType.isEmpty()) continue;

            if (moveType.charAt(0) == 'D') {
                if (!handleDrop()) continue;
            } else if (moveType.charAt(0) == 'P') {
                if (!handlePopOut()) continue;
            } else {
                System.out.println("Invalid choice - type D or P.");
                continue;
            }

            board.printBoard();

            boolean p1win = board.hasConnect4('X');
            boolean p2win = board.hasConnect4('O');

            if (p1win || p2win) {
                if (p1win && p2win) {
                    System.out.println("Both players have 4-in-a-row!");
                    System.out.println("By PopOut rules, the current player wins.");
                    System.out.println("Player " + getPlayerNumber(currentPlayer) +
                                       " (" + currentPlayer + ") WINS!");
                } else {
                    char winner = p1win ? 'X' : 'O';
                    System.out.println("Player " + getPlayerNumber(winner) +
                                       " (" + winner + ") WINS!");
                }
                break;
            }

            if (board.isFull()) {
                System.out.println("Board is full. It's a draw!");
                break;
            }

            switchPlayer();
        }

        System.out.println("Game over. Thanks for playing!");
    }

    private boolean handleDrop() {
        System.out.println("Enter column to drop into (1 - " + board.getCols() + "):");
        String line = scanner.nextLine().trim();
        int col;
        try {
            col = Integer.parseInt(line) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid column number.");
            return false;
        }
        if (!board.canDrop(col)) {
            System.out.println("Column is full or out of range.");
            return false;
        }
        board.dropDisc(col, currentPlayer);
        return true;
    }

    private boolean handlePopOut() {
        System.out.println("Enter column to pop out from bottom (1 - " + board.getCols() + "):");
        String line = scanner.nextLine().trim();
        int col;
        try {
            col = Integer.parseInt(line) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid column number.");
            return false;
        }
        if (!board.canPopOut(col, currentPlayer)) {
            System.out.println("Cannot pop out from that column — either wrong piece or out of range.");
            return false;
        }
        board.popOut(col);
        return true;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private int getPlayerNumber(char p) {
        return (p == 'X') ? 1 : 2;
    }
}

class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;
    private final char EMPTY = '.';

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = EMPTY;
            }
        }
    }

    public int getCols() { return cols; }

    public void printBoard() {
        System.out.println();
        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                System.out.print(grid[r][c]);
                System.out.print("|");
            }
            System.out.println();
        }

        System.out.print(" ");
        for (int c = 1; c <= cols; c++) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    public boolean canDrop(int col) {
        if (col < 0 || col >= cols) return false;
        return grid[0][col] == EMPTY;
    }

    public void dropDisc(int col, char player) {
        for (int r = rows - 1; r >= 0; r--) {
            if (grid[r][col] == EMPTY) {
                grid[r][col] = player;
                return;
            }
        }
    }

    public boolean canPopOut(int col, char player) {
        if (col < 0 || col >= cols) return false;
        return grid[rows - 1][col] == player;
    }

    public void popOut(int col) {
        for (int r = rows - 1; r > 0; r--) {
            grid[r][col] = grid[r - 1][col];
        }
        grid[0][col] = EMPTY;
    }

    public boolean isFull() {
        for (int c = 0; c < cols; c++) {
            if (grid[0][c] == EMPTY) return false;
        }
        return true;
    }

    public boolean hasConnect4(char player) {

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols - 4; c++) {
                boolean ok = true;
                for (int k = 0; k < 4; k++) {
                    if (grid[r][c + k] != player) { ok = false; break; }
                }
                if (ok) return true;
            }
        }

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r <= rows - 4; r++) {
                boolean ok = true;
                for (int k = 0; k < 4; k++) {
                    if (grid[r + k][c] != player) { ok = false; break; }
                }
                if (ok) return true;
            }
        }

        for (int r = 0; r <= rows - 4; r++) {
            for (int c = 0; c <= cols - 4; c++) {
                boolean ok = true;
                for (int k = 0; k < 4; k++) {
                    if (grid[r + k][c + k] != player) { ok = false; break; }
                }
                if (ok) return true;
            }
        }

        for (int r = 3; r < rows; r++) {
            for (int c = 0; c <= cols - 4; c++) {
                boolean ok = true;
                for (int k = 0; k < 4; k++) {
                    if (grid[r - k][c + k] != player) { ok = false; break; }
                }
                if (ok) return true;
            }
        }

        return false;
    }
}
