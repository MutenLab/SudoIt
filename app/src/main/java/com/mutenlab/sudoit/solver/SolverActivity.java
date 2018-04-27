package com.mutenlab.sudoit.solver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.common.SudokuView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class SolverActivity extends AppCompatActivity {

    static {
        OpenCVLoader.initDebug();
    }

    private static final String TAG = SolverActivity.class.getCanonicalName();

    private Bundle bundle;

    private int[][] unsolved;

    private int[][] solved;

    private LinearLayout sudokuLinear;

    private SudokuView mSudokuView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    unsolved = to2DArray(bundle.getIntArray("unsolved"));
                    solved = to2DArray(bundle.getIntArray("solved"));

                    mSudokuView = new SudokuView(getApplicationContext(), unsolved, solved);
                    sudokuLinear.addView(mSudokuView);
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solver);
        sudokuLinear = findViewById(R.id.sudokuLinear);
        bundle = getIntent().getExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private int[][] to2DArray(int[] input) {
        int index = 0;
        int[][] output = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                output[i][j] = input[index];
                index++;
            }
        }
        return output;
    }
}
