package com.mutenlab.sudoit.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.common.Solver;
import com.mutenlab.sudoit.model.Puzzle;

/**
 * @author icerrate
 */
public class SolverActivity extends AppCompatActivity {

    private static final String TAG = SolverActivity.class.getSimpleName();

    public static final String UNSOLVED_SUDOKU_KEY = "unsolved_sudoku_key";

    private Puzzle puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solver);
        initPuzzleOrGetFromExtras();
        updatePuzzle(puzzle);
    }

    private void initPuzzleOrGetFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.get(UNSOLVED_SUDOKU_KEY) != null) {
            puzzle = new Puzzle((Integer[][]) bundle.get(UNSOLVED_SUDOKU_KEY));
        } else {
            puzzle = new Puzzle();
        }
    }

    public void SolvePuzzle(View v) {
        Solver puzzleSolver = new Solver(this.puzzle);
        Puzzle solvedPuzzle = puzzleSolver.solvePuzzle();
        updatePuzzle(solvedPuzzle);
    }

    public void updatePuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
        RecyclerView sudokuRecyclerView = findViewById(R.id.sudoku);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, Puzzle.SIZE);
        sudokuRecyclerView.setLayoutManager(gridLayoutManager);
        SudokuAdapter sudokuAdapter = new SudokuAdapter(puzzle, calculateItemWidth(Puzzle.SIZE));
        sudokuRecyclerView.setAdapter(sudokuAdapter);
    }

    private int calculateItemWidth(int itemsPerRow) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x/itemsPerRow;
    }
}
