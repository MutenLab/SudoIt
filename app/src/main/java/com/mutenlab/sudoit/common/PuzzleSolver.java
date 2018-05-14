package com.mutenlab.sudoit.common;

import com.mutenlab.sudoit.model.Point;
import com.mutenlab.sudoit.model.Puzzle;

public class PuzzleSolver {

    private Puzzle originalPuzzle;

    public PuzzleSolver(Puzzle puzzle) {
        this.originalPuzzle = puzzle;
    }

    public Puzzle solvePuzzle() {
        Puzzle workingPuzzle = new Puzzle(originalPuzzle);
        solve(workingPuzzle);
        return workingPuzzle;
    }

    private Boolean solve(Puzzle workingPuzzle) {
        Point workingPoint = workingPuzzle.findNextUnassignedLocation();
        if (workingPoint == null)
            return true;
        for (Integer num = Puzzle.MIN_VALUE; num <= Puzzle.MAX_VALUE; num++) {
            if (workingPuzzle.noConflicts(workingPoint, num)) {
                workingPuzzle.setNumber(workingPoint, num);
                if (solve(workingPuzzle))
                    return true;
                else
                    workingPuzzle.eraseNumber(workingPoint);
            }
        }
        return false;
    }
}