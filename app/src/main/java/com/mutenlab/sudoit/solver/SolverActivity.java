package com.mutenlab.sudoit.solver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.common.Puzzle;
import com.mutenlab.sudoit.common.PuzzleAdaptor;
import com.mutenlab.sudoit.common.Solver;

public class SolverActivity extends AppCompatActivity {

    private static final String TAG = SolverActivity.class.getCanonicalName();

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
        if (bundle != null && bundle.get("Puzzle") != null) {
            puzzle = new Puzzle((Integer[][]) bundle.get("Puzzle"));
        } else {
            puzzle = new Puzzle();
        }
    }

    public void SolvePuzzle(View v) throws Exception {
        Solver puzzleSolver = new Solver(this.puzzle);
        Puzzle solvedPuzzle = puzzleSolver.solvePuzzle();
        updatePuzzle(solvedPuzzle);
    }

    public void updatePuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
        GridView gridView = (GridView) findViewById(R.id.sudokuGrid);
        PuzzleAdaptor puzzleAdapter = new PuzzleAdaptor(this, this.puzzle);
        gridView.setAdapter(puzzleAdapter);
    }
}
