package com.mutenlab.sudoit.model;

/**
 * @author Ivan Cerrate.
 */
public class Puzzle {

    public static final int SIZE = 9;

    public static final int MAX_VALUE = 9;

    public static final int MIN_VALUE = 1;

    private Integer[][] initNumbers;

    private Integer[][] numbers;

    public Puzzle() {
        initNumbers();
    }

    public Puzzle(Puzzle toCopy) {
        initNumbers();
        for (int x = 0; x < Puzzle.SIZE; x++) {
            for (int y = 0; y < Puzzle.SIZE; y++) {
                initNumbers[x][y] = toCopy.getNumber(new Point(x, y));
                numbers[x][y] = toCopy.getNumber(new Point(x, y));
            }
        }
    }

    public Puzzle(Integer[][] problem) {
        initNumbers();
        for (int x = 0; x < Puzzle.SIZE; x++) {
            for (int y = 0; y < Puzzle.SIZE; y++) {
                initNumbers[x][y] = problem[x][y];
                numbers[x][y] = problem[x][y];
            }
        }
    }

    private void initNumbers() {
        this.initNumbers = new Integer[SIZE][SIZE];
        this.numbers = new Integer[SIZE][SIZE];
    }

    private void AssertValidIndexes(Point point) throws IllegalArgumentException {
        if (point.x < 0 || point.x > SIZE - 1)
            throw new IllegalArgumentException("X is out of range.");
        if (point.y < 0 || point.y > SIZE - 1)
            throw new IllegalArgumentException("Y is out of range.");
    }

    public void setNumber(Point point, Integer value) throws IllegalArgumentException {
        AssertValidIndexes(point);
        assertValidValue(value);
        numbers[point.x][point.y] = value;
    }

    public void eraseNumber(Point point) throws IllegalArgumentException {
        AssertValidIndexes(point);
        setNumber(point, null);
    }

    private void assertValidValue(Integer value) {
        if (value == null)
            return;
        if ((value < MIN_VALUE || value > MAX_VALUE) && value != -1)
            throw new IllegalArgumentException("Value must be greater than 0 and less than the SIZE of the grid.");
    }

    public Integer getNumber(Point point) throws IllegalArgumentException {
        AssertValidIndexes(point);
        return numbers[point.x][point.y];
    }

    public Integer getInitNumber(Point point) throws IllegalArgumentException {
        AssertValidIndexes(point);
        return initNumbers[point.x][point.y];
    }

    public Point findNextUnassignedLocation() {
        for (int x = 0; x < Puzzle.SIZE; x++) {
            for (int y = 0; y < Puzzle.SIZE; y++) {
                Integer number = this.numbers[x][y];
                if (number == null || number.intValue() == -1) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public Boolean noConflicts(Point point, Integer number) {
        if (isRowConflict(point.y, number)) return false;
        return !isColumnConflict(point.x, number);
    }

    private boolean isRowConflict(int y, Integer number) {
        for (int x = 0; x < Puzzle.SIZE; x++) {
            Integer pointVal = this.numbers[x][y];
            if (pointVal == null) continue;
            if (pointVal.intValue() == number.intValue())
                return true;
        }
        return false;
    }

    private boolean isColumnConflict(int x, Integer number) {
        for (int y = 0; y < Puzzle.SIZE; y++) {
            Integer pointVal = this.numbers[x][y];
            if (pointVal == null) continue;
            if (pointVal.intValue() == number.intValue())
                return true;
        }
        return false;
    }
}