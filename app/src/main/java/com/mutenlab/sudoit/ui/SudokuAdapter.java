package com.mutenlab.sudoit.ui;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.model.Point;
import com.mutenlab.sudoit.model.Puzzle;

/**
 * @author icerrate
 */
public class SudokuAdapter extends RecyclerView.Adapter<SudokuAdapter.NumberViewHolder> {

    private Puzzle mPuzzle;

    private int mItemWidth;

    public SudokuAdapter(Puzzle puzzle, int itemWidth) {
        mPuzzle = puzzle;
        mItemWidth = itemWidth;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sudoku_number, parent, false);
        return new NumberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        Point point = getPoint(position);
        holder.onBind(point);
    }

    private Point getPoint(int position) {
        int x = (position % Puzzle.SIZE);
        int y = position / Puzzle.SIZE;

        return new Point(x, y);
    }

    @Override
    public int getItemCount() {
        return Puzzle.SIZE*Puzzle.SIZE;
    }

    class NumberViewHolder extends RecyclerView.ViewHolder {

        public TextView number;

        public NumberViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.number);
        }

        public void onBind(Point point) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mItemWidth, mItemWidth);
            itemView.setLayoutParams(layoutParams);

            Integer value = mPuzzle.getNumber(point);

            if (value == null)
                number.setText("");
            else if (value == -1)
                number.setText("?");
            else
                number.setText(String.valueOf(value));
        }
    }
}
