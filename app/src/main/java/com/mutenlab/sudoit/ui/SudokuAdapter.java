package com.mutenlab.sudoit.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mutenlab.sudoit.R;
import com.mutenlab.sudoit.model.Point;
import com.mutenlab.sudoit.model.Puzzle;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    public void updatePuzzle(Puzzle puzzle) {
        mPuzzle = puzzle;
        notifyDataSetChanged();
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

        @BindView(R.id.top)
        View topLine;

        @BindView(R.id.start)
        View startLine;

        @BindView(R.id.number)
        TextView number;

        @BindView(R.id.end)
        View endLine;

        @BindView(R.id.bottom)
        View bottomLine;

        public NumberViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBind(Point point) {
            Context context = number.getContext();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemWidth);
            itemView.setLayoutParams(layoutParams);

            Integer value = mPuzzle.getNumber(point);
            Integer initValue = mPuzzle.getInitNumber(point);

            number.setTypeface(null, Typeface.NORMAL);
            number.setTextColor(context.getResources().getColor(R.color.grey));
            if (value == null) {
                number.setText("");
            } else if (value == -1) {
                number.setText("");
            } else {
                number.setText(String.valueOf(value));
                if (initValue != null) {
                    number.setTypeface(null, Typeface.BOLD);
                    number.setTextColor(context.getResources().getColor(R.color.black));
                }
            }

            applyLinesStyle(1, 1, 1, 1);
            paintFirstLevel(point);
            paintSecondLevel(point);
            paintThirdLevel(point);
        }

        private void paintFirstLevel(Point point) {
            if (point.x == 0 && point.y == 0) {
                applyLinesStyle(8, 8, 1, 1);
            } else if (point.x == 0 && point.y == 8) {
                applyLinesStyle(1, 8, 1, 8);
            } else if (point.x == 8 && point.y == 0) {
                applyLinesStyle(8, 1, 8, 1);
            } else if (point.x == 8 && point.y == 8) {
                applyLinesStyle(1, 1, 8, 8);
            } else if (point.x == 0 && point.y > 0 && point.y < 8) {
                if (point.y != 2 && point.y != 5) {
                    applyLinesStyle(1, 8, 1, 1);
                } else {
                    applyLinesStyle(1, 8, 1, 8);
                }
            } else if (point.x == 8 && point.y > 0 && point.y < 8) {
                if (point.y != 2 && point.y != 5) {
                    applyLinesStyle(1, 1, 8, 1);
                } else {
                    applyLinesStyle(1, 1, 8, 8);
                }
            } else if (point.y == 0 && point.x > 0 && point.x < 8) {
                if (point.x != 2 && point.x != 5) {
                    applyLinesStyle(8, 1, 1, 1);
                } else {
                    applyLinesStyle(8, 1, 8, 1);
                }
            } else if (point.y == 8 && point.x > 0 && point.x < 8) {
                if (point.x != 2 && point.x != 5) {
                    applyLinesStyle(1, 1, 1, 8);
                } else {
                    applyLinesStyle(1, 1, 8, 8);
                }
            }
        }

        private void paintSecondLevel(Point point) {
            if (point.x == 1 && point.y > 0 && point.y < 8) {
                if (point.y != 2 && point.y != 5) {
                    applyLinesStyle(1, 1, 1, 1);
                } else {
                    applyLinesStyle(1, 1, 1, 8);
                }
            } else if (point.x == 7 && point.y > 0 && point.y < 8) {
                if (point.y != 2 && point.y != 5) {
                    applyLinesStyle(1, 1, 1, 1);
                } else {
                    applyLinesStyle(1, 1, 1, 8);
                }
            } else if (point.y == 1 && point.x > 0 && point.x < 8) {
                if (point.x != 2 && point.x != 5) {
                    applyLinesStyle(1, 1, 1, 1);
                } else {
                    applyLinesStyle(1, 1, 8, 1);
                }
            } else if (point.y == 7 && point.x > 0 && point.x < 8) {
                if (point.x != 2 && point.x != 5) {
                    applyLinesStyle(1, 1, 1, 1);
                } else {
                    applyLinesStyle(1, 1, 8, 1);
                }
            }
        }

        private void paintThirdLevel(Point point) {
            if (point.x == 2 && point.y == 2) {
                applyLinesStyle(1, 1, 8, 8);
            } else if (point.x == 2 && point.y == 5) {
                applyLinesStyle(1, 1, 8, 8);
            } else if (point.x == 5 && point.y == 2) {
                applyLinesStyle(1, 1, 8, 8);
            } else if (point.x == 5 && point.y == 5) {
                applyLinesStyle(1, 1, 8, 8);
            } else if (point.x == 2 && point.y > 2 && point.y < 7) {
                applyLinesStyle(1, 1, 8, 1);
            } else if (point.x == 5 && point.y > 2 && point.y < 7) {
                applyLinesStyle(1, 1, 8, 1);
            } else if (point.y == 2 && point.x > 2 && point.x < 7) {
                applyLinesStyle(1, 1, 1, 8);
            } else if (point.y == 5 && point.x > 2 && point.x < 7) {
                applyLinesStyle(1, 1, 1, 8);
            }
        }

        private void applyLinesStyle(int top, int start, int end, int bottom) {
            topLine.getLayoutParams().height = top;
            topLine.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

            startLine.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            startLine.getLayoutParams().width = start;

            endLine.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            endLine.getLayoutParams().width = end;

            bottomLine.getLayoutParams().height = bottom;
            bottomLine.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }
}
