package com.mutenlab.sudoit.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.common.Solver;
import com.mutenlab.sudoit.model.Puzzle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author icerrate
 */
public class SolverActivity extends AppCompatActivity {

    public static final String PUZZLE_KEY = "puzzle_key";

    private SudokuAdapter sudokuAdapter;

    private Puzzle puzzle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.sudoku)
    RecyclerView sudokuRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solver);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.solver_title);
        setSupportActionBar(toolbar);
        initPuzzleOrGetFromExtras();
    }

    private void initPuzzleOrGetFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.get(PUZZLE_KEY) != null) {
            puzzle = new Puzzle((Integer[][]) bundle.get(PUZZLE_KEY));
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, Puzzle.SIZE);
        sudokuRecyclerView.setLayoutManager(gridLayoutManager);
        sudokuAdapter = new SudokuAdapter(puzzle, calculateItemWidth(Puzzle.SIZE));
        sudokuRecyclerView.setAdapter(sudokuAdapter);
    }

    @OnClick(R.id.SolvePuzzleButton)
    public void solvePuzzle(View view) {
        Solver puzzleSolver = new Solver(this.puzzle);
        Puzzle solvedPuzzle = puzzleSolver.solvePuzzle();
        sudokuAdapter.updatePuzzle(solvedPuzzle);
    }

    private int calculateItemWidth(int itemsPerRow) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int margins = 2*getResources().getDimensionPixelSize(R.dimen.margin);
        return (size.x-margins)/itemsPerRow;
    }
}
