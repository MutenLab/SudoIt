package com.mutenlab.sudoit.model;

public class SudokuSolver {


    /**
     * exposed method called to solve sudoku puzzle
     *
     * @param grid
     * @return
     */
    public static Integer[][] solveSudoku(Integer[][] grid) {
        Integer[][] copy = deepCopy(grid);
        solve(copy);
        return copy;
    }

    /**
     * recursively solves puzzle
     *
     * @param grid
     * @return
     */
    private static boolean solve(Integer[][] grid) {

        int row = -1;
        int col = -1;

        // find empty cell
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == null || grid[i][j] ==0) {
                    row = i;
                    col = j;
                }
            }
        }

        // return true if cannot find empty cell
        if (row < 0 || col < 0) {
            return true;
        }

        // recursively solve
        for (int i = 1; i <= 9; i++) {
            if (legal(i, row, col, grid)) {
                grid[row][col] = i;

                if (solve(grid)) {
                    return true;
                }

                grid[row][col] = 0;
            }
        }
        return false;
    }

    /**
     * checks if move is legal
     *
     * @param num
     * @param row
     * @param col
     * @param grid
     * @return
     */
    private static boolean legal(int num, int row, int col, Integer[][] grid) {
        // check for row violation
        for (int i = 0; i < 9; i++) {
            if (i == col)
                continue;
            if (grid[row][i] !=  null && grid[row][i] == num) {
                return false;
            }
        }

        // check for col violation
        for (int j = 0; j < 9; j++) {
            if (j == row)
                continue;
            if (grid[j][col] !=  null && grid[j][col] == num) {
                return false;
            }
        }

        // check for box violation
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (i == row && j == col)
                    continue;
                if (grid[i][j] !=  null && grid[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Integer[][] deepCopy(Integer[][] grid) {
        Integer[][] copy = new Integer[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                copy[i][j] = grid[i][j];
            }
        }
        return copy;
    }
}
