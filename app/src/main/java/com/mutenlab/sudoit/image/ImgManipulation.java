package com.mutenlab.sudoit.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mutenlab.sudoit.model.ImgManipUtil;
import com.mutenlab.sudoit.model.Line;
import com.mutenlab.sudoit.model.PuzzleOutLine;
import com.mutenlab.sudoit.model.TessOCR;
import com.mutenlab.sudoit.model.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ImgManipulation {

    private final float CONST_RATIO = (float) 0.03;
    private Bitmap mBitmap;
    private Mat clean;
    private BlobExtract mBlobExtract;
    private TessOCR mOCR;
    private boolean error = false;

    public final String TAG_SUBMAT_DIMENS = "Submat dimensions";
    public final String TAG_WHITE_POINT = "White point coorinates";
    public final String TAG_TILE_STATUS = "tile status";
    public final static String TAG_HOUGHLINES = "HoughLines info";
    public final String TAG_ERROR_FIND_GRID = "findGridArea error";
    public final String TAG_ERROR_FLOODFILL = "Floodfill setPixel error";

    public ImgManipulation(Context context, Bitmap bitmap) {
        mBitmap = bitmap;
        mBlobExtract = new BlobExtract();
        mOCR = new TessOCR(context);
    }

    public boolean getError() {
        return error;
    }

    /**
     * performs all the required image processing to find sudoku grid numbers
     */
    public int[][] getSudokuGridNums(ImageView imageView) {
        clean = ImgManipUtil.bitmapToMat(mBitmap);

        if (error) {
            return null;
        }

        Mat greyMat = generateGreyMat(clean);
        Mat thresholdMat = generateThresholdMat(greyMat);
        Mat largestBlobMat = findLargestBlob(thresholdMat);
        Mat houghLinesMat = generateHoughLinesMat(largestBlobMat);
        Mat outlineMat = generateOutlineMat(greyMat, largestBlobMat, houghLinesMat);

        imageView.setImageBitmap(ImgManipUtil.matToBitmap(outlineMat));
        imageView.setVisibility(View.VISIBLE);

        int[][] test_sudo = {{5,3,0,0,7,0,0,0,0}, {6,0,0,1,9,5,0,0,0}, {0,9,8,0,0,0,0,6,0},
                {8,0,0,0,6,0,0,0,3}, {4,0,0,8,0,3,0,0,1}, {7,0,0,0,2,0,0,0,6}, {0,6,0,0,0,0,2,8,0}, {0,0,0,4,1,9,0,0,5}, {0,0,0,0,8,0,0,7,9}};

        return test_sudo;
    }

    private Mat generateOutlineMat(Mat greyMat, Mat largestBlobMat, Mat houghLinesMat) {
        Mat outLineMat = greyMat.clone();
        PuzzleOutLine location = findOutLine(largestBlobMat, houghLinesMat);

        Imgproc.drawMarker(outLineMat, location.topLeft, new Scalar(64), Imgproc.MARKER_TILTED_CROSS, 30, 10, 8);
        Imgproc.drawMarker(outLineMat, location.topRight, new Scalar(64), Imgproc.MARKER_TILTED_CROSS, 30, 10, 8);
        Imgproc.drawMarker(outLineMat, location.bottomLeft, new Scalar(64), Imgproc.MARKER_TILTED_CROSS, 30, 10, 8);
        Imgproc.drawMarker(outLineMat, location.bottomRight, new Scalar(64), Imgproc.MARKER_TILTED_CROSS, 30, 10, 8);

        Imgproc.line(outLineMat, location.top.origin, location.top.destination, new Scalar(64));
        Imgproc.line(outLineMat, location.bottom.origin, location.bottom.destination, new Scalar(127));
        Imgproc.line(outLineMat, location.left.origin, location.left.destination, new Scalar(64));
        Imgproc.line(outLineMat, location.right.origin, location.right.destination, new Scalar(127));

        return outLineMat;
    }

    public PuzzleOutLine findOutLine(Mat largestBlobMat, Mat houghLinesMat) {

        PuzzleOutLine location = new PuzzleOutLine();

        int height = largestBlobMat.height();
        int width = largestBlobMat.width();

        int countHorizontalLines = 0;
        int countVerticalLines = 0;

        List<Line> houghLines = getHoughLines(houghLinesMat);

        for (Line line : houghLines) {
            if (line.getOrientation() == Line.Orientation.horizontal) {
                countHorizontalLines++;

                if (location.top == null) {
                    location.top = line;
                    location.bottom = line;
                    continue;
                }

                if (line.getAngleFromXAxis() > 6)
                    continue;
                if (line.getAngleFromXAxis() < 1 && (line.getMinY() < 5 || line.getMaxY() > height - 5))
                    continue;

                if (line.getMinY() < location.bottom.getMinY())
                    location.bottom = line;
                if (line.getMaxY() > location.top.getMaxY())
                    location.top = line;
            } else if (line.getOrientation() == Line.Orientation.vertical) {
                countVerticalLines++;

                if (location.left == null) {
                    location.left = line;
                    location.right = line;
                    continue;
                }

                if (line.getAngleFromXAxis() < 84)
                    continue;
                if (line.getAngleFromXAxis() > 89 && (line.getMinX() < 5 || line.getMaxX() > width - 5))
                    continue;

                if (line.getMinX() < location.left.getMinX())
                    location.left = line;
                if (line.getMaxX() > location.right.getMaxX())
                    location.right = line;
            }
        }

        if (houghLines.size() < 4) {
            //throw new PuzzleNotFoundException("not enough possible edges found. Need at least 4 for a rectangle.");
        }
        if (countHorizontalLines < 2) {
            //throw new PuzzleNotFoundException("not enough horizontal edges found. Need at least 2 for a rectangle.");
        }
        if (countVerticalLines < 2) {
            //throw new PuzzleNotFoundException("not enough vertical edges found. Need at least 2 for a rectangle.");
        }

        location.topLeft = location.top.findIntersection(location.left);
        if (location.topLeft == null) {
            //throw new PuzzleNotFoundException("Cannot find top left corner");
        }

        location.topRight = location.top.findIntersection(location.right);
        if (location.topRight == null) {
            //throw new PuzzleNotFoundException("Cannot find top right corner");
        }

        location.bottomLeft = location.bottom.findIntersection(location.left);
        if (location.topLeft == null) {
            //throw new PuzzleNotFoundException("Cannot find bottom left corner");
         }

        location.bottomRight = location.bottom.findIntersection(location.right);
        if (location.topLeft == null) {
            //throw new PuzzleNotFoundException("Cannot find bottom right corner");
        }

        return location;
    }

    private Mat generateGreyMat(Mat cleanMat) {
        Mat greyMat = cleanMat.clone();
        Imgproc.cvtColor(greyMat, greyMat, Imgproc.COLOR_BGR2GRAY);
        return greyMat;
    }


    private Mat generateThresholdMat(Mat greyMat) {

        /*Imgproc.cvtColor(clean, clean, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(clean, clean, new Size(11,11), 0);
        ImgManipUtil.adaptiveThreshold(clean);
        Core.bitwise_not(clean, clean);
        ImgManipUtil.dilateMat(clean, 3);
        ImgManipUtil.binaryThreshold(clean);*/

        Mat thresholdMat = greyMat.clone();
        Imgproc.adaptiveThreshold(thresholdMat, thresholdMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, 5);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2, 2));
        Imgproc.erode(thresholdMat, thresholdMat, kernel);
        Mat kernelDil = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(2, 2));
        Imgproc.dilate(thresholdMat, thresholdMat, kernelDil);
        Core.bitwise_not(thresholdMat, thresholdMat);

        return thresholdMat;
    }

    private Mat generateHoughLinesMat(Mat largestBlobMat) {

        Mat houghLinesMat = largestBlobMat.clone();

        List<Line> houghLines = getHoughLines(largestBlobMat);
        for (Line line : houghLines) {
            Imgproc.line(houghLinesMat, line.origin, line.destination, new Scalar(64));
        }
        return houghLinesMat;
    }
    private List<Line> getHoughLines(Mat largestBlobMat) {
        Mat linesMat = largestBlobMat.clone();
        int width = largestBlobMat.width();
        int height = largestBlobMat.height();

        //Need to think about the threshold as getting this correct is very important!
        Imgproc.HoughLines(largestBlobMat, linesMat, (double) 1, Math.PI / 180, 400);

        //The Hough transform returns a series of lines in Polar format this is returned in the
        //form of a Mat where each row is a vector where row[0] is rho and row[1] is theta
        //See http://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/hough_lines/hough_lines.html
        //and http://stackoverflow.com/questions/7925698/android-opencv-drawing-hough-lines/7975315#7975315
        List<Line> houghLines = new ArrayList<>();
        int lines = linesMat.rows();
        for (int x = 0; x < lines; x++) {
            double[] vec = linesMat.get(x, 0);
            Vector vector = new Vector(vec[0], vec[1]);
            Line line = new Line(vector, height, width);

            houghLines.add(line);
        }
        return houghLines;
    }

    private Mat findLargestBlob(Mat thresholdMat) {
        Mat largestBlobMat = thresholdMat.clone();
        int height = largestBlobMat.height();
        int width = largestBlobMat.width();

        Point maxBlobOrigin = new Point(0, 0);

        int maxBlobSize = 0;
        Mat greyMask = new Mat(height + 2, width + 2, CvType.CV_8U, new Scalar(0, 0, 0));
        Mat blackMask = new Mat(height + 2, width + 2, CvType.CV_8U, new Scalar(0, 0, 0));
        for (int y = 0; y < height; y++) {
            Mat row = largestBlobMat.row(y);
            for (int x = 0; x < width; x++) {
                double[] value = row.get(0, x);
                Point currentPoint = new Point(x, y);

                if (value[0] > 128) {
                    int blobSize = Imgproc.floodFill(largestBlobMat, greyMask, currentPoint, new Scalar(64));
                    if (blobSize > maxBlobSize) {
                        Imgproc.floodFill(largestBlobMat, blackMask, maxBlobOrigin, new Scalar(0));
                        maxBlobOrigin = currentPoint;
                        maxBlobSize = blobSize;
                    } else {
                        Imgproc.floodFill(largestBlobMat, blackMask, currentPoint, new Scalar(0));
                    }
                }
            }
        }
        Mat largeBlobMask = new Mat(height + 2, width + 2, CvType.CV_8U, new Scalar(0));
        Imgproc.floodFill(largestBlobMat, largeBlobMask, maxBlobOrigin, new Scalar(255));


        return largestBlobMat;
    }

    private double colourLargestBlobWhite(Mat largestBlobMat, int height, int width, Point maxBlobOrigin) {
        Mat largeBlobMask = new Mat(height + 2, width + 2, CvType.CV_8U, new Scalar(0));
        return (double) Imgproc.floodFill(largestBlobMat, largeBlobMask, maxBlobOrigin, new Scalar(255));
    }

    private void eraseBlobIfLessThanOnePercentOfArea(Mat largestBlobMat, int height, int width, Point maxBlobOrigin, double largestSize) {
        double area = height * width;
        if(largestSize / area < 0.01) {
            Mat eraseMask = new Mat(height + 2, width + 2, CvType.CV_8U, new Scalar(0));
            Imgproc.floodFill(largestBlobMat, eraseMask, maxBlobOrigin, new Scalar(0));
        }
    }

    public Mat byteArrayToMat(byte[][] byteArray) {
        Mat m = new Mat(byteArray.length, byteArray[0].length, CvType.CV_8UC1);
        for (int i = 0; i < byteArray.length; i++) {
            for (int j = 0; j < byteArray[0].length; j++) {
                byte[] data = { byteArray[i][j] };
                m.put(i, j, data);
            }
        }
        return m;
    }

    /**
     * uses OCR to find the number in tile and stores results in 2D array
     *
     * @param tileContainNum
     *            grid array indicating which tiles contains numbers
     * @param nums
     *            queue of Mats containing each individual number
     * @return grid array representing sudoku puzzle (empty == 0)
     */
    public int[][] storeNumsToGrid(boolean[][] tileContainNum, Queue<Mat> nums) {
        int count = 0;
        int[][] grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Log.d("nums queue count", nums.size() + "");
                if (tileContainNum[i][j]) {
                    grid[i][j] = getOCRNum(nums.remove(), count);
                    count++;
                }
            }

        }
        if (!mOCR.isEnded()) {
            mOCR.endTessOCR();
        }
        return grid;
    }

    /**
     * uses tessOCR to recognize the digit in the Mat
     *
     * @param num
     *            Mat containing image of the digit
     * @param count
     *            used for debugging/logging purposes
     * @return recognized integer
     */
    private int getOCRNum(Mat num, int count) {
        if (!mOCR.isInit()) {
            mOCR.initOCR();
        }
        Bitmap b = ImgManipUtil.matToBitmap(num);
        //FileSaver.storeImage(b, count + "");
        int ans = Integer.parseInt(mOCR.doOCR(b));
        if (ans > 9) {
            ans = trimNum(ans);
        }
        Log.d("num", count + ": " + ans);
        return ans;
    }

    /**
     * performs OpenCV image manipulations to extract and undistort sudoku
     * puzzle from image
     *
     * @param mat source mat
     * @return Mat image of fixed puzzle
     */
    public Mat extractSudokuGrid(Mat mat) {
        // convert source bitmap to mat; use canny operation
        Mat edges = new Mat(mat.size(), mat.type());
        Imgproc.Canny(mat, edges, 50, 200);

        // trim external noise to localize the sudoku puzzle and stores in bmp
        // then m2
        int[] bounds = ImgManipUtil.findGridBounds(edges);
        error = ImgManipUtil.notSquare(bounds);

        edges = subMat(edges, bounds);
        clean = subMat(clean, bounds);

        List<Point> corners = findCorners(edges);
        Point topLeft = corners.get(0);
        Point topRight = corners.get(1);
        Point bottomLeft = corners.get(2);
        Point bottomRight = corners.get(3);

        edges = ImgManipUtil.fixPerspective(topLeft, topRight, bottomLeft,
                bottomRight, edges);
        clean = ImgManipUtil.fixPerspective(topLeft, topRight, bottomLeft,
                bottomRight, clean);
        //FileSaver.storeImage(ImgManipUtil.matToBitmap(edges), "edges");
        //FileSaver.storeImage(ImgManipUtil.matToBitmap(clean), "clean");
        return edges;
    }

    /**
     * returns smaller mat based on bounds
     *
     * @param mat
     *            source mat
     * @param bounds
     *            array: [0]=left, [1]=right, [2]=top, [3]=bottom
     * @return smaller subMat according to bounds
     */
    private Mat subMat(Mat mat, int[] bounds) {
        int left = bounds[0];
        int right = bounds[1];
        int top = bounds[2];
        int bot = bounds[3];

        return mat.submat(top, bot, left, right);
    }

    /**
     * finds corners of the sudoku grid in the Mat image using openCV HoughLines
     * points of intersection
     *
     * @param mat
     *            source image
     * @return List of Points representing coordinates of the four corners
     */
    private List<Point> findCorners(Mat mat) {
        Mat lines = new Mat();
        List<double[]> horizontalLines = new ArrayList<double[]>();
        List<double[]> verticalLines = new ArrayList<double[]>();

        Imgproc.HoughLinesP(mat, lines, 1, Math.PI / 180, 150);

        for (int i = 0; i < lines.cols(); i++) {
            double[] line = lines.get(0, i);
            double x1 = line[0];
            double y1 = line[1];
            double x2 = line[2];
            double y2 = line[3];
            if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {
                horizontalLines.add(line);
            } else if (Math.abs(x2 - x1) < Math.abs(y2 - y1)) {
                verticalLines.add(line);
            }
        }
        String lineInfo = String.format(
                "horizontal: %d, vertical: %d, total: %d",
                horizontalLines.size(), verticalLines.size(), lines.cols());
        Log.d(TAG_HOUGHLINES, lineInfo);

        // find the lines furthest from centre which will be the bounds for the
        // grid
        double[] topLine = horizontalLines.get(0);
        double[] bottomLine = horizontalLines.get(0);
        double[] leftLine = verticalLines.get(0);
        double[] rightLine = verticalLines.get(0);

        double xMin = 1000;
        double xMax = 0;
        double yMin = 1000;
        double yMax = 0;

        for (int i = 0; i < horizontalLines.size(); i++) {
            if (horizontalLines.get(i)[1] < yMin
                    || horizontalLines.get(i)[3] < yMin) {
                topLine = horizontalLines.get(i);
                yMin = horizontalLines.get(i)[1];
            } else if (horizontalLines.get(i)[1] > yMax
                    || horizontalLines.get(i)[3] > yMax) {
                bottomLine = horizontalLines.get(i);
                yMax = horizontalLines.get(i)[1];
            }
        }

        for (int i = 0; i < verticalLines.size(); i++) {
            if (verticalLines.get(i)[0] < xMin
                    || verticalLines.get(i)[2] < xMin) {
                leftLine = verticalLines.get(i);
                xMin = verticalLines.get(i)[0];
            } else if (verticalLines.get(i)[0] > xMax
                    || verticalLines.get(i)[2] > xMax) {
                rightLine = verticalLines.get(i);
                xMax = verticalLines.get(i)[0];
            }
        }

        // obtain four corners of sudoku grid
        Point topLeft = ImgManipUtil.findCorner(topLine, leftLine);
        Point topRight = ImgManipUtil.findCorner(topLine, rightLine);
        Point bottomLeft = ImgManipUtil.findCorner(bottomLine, leftLine);
        Point bottomRight = ImgManipUtil.findCorner(bottomLine, rightLine);

        List<Point> corners = new ArrayList<Point>(4);
        corners.add(topLeft);
        corners.add(topRight);
        corners.add(bottomLeft);
        corners.add(bottomRight);

        return corners;
    }

    /**
     * finds which tile contains a number and which doesn't
     *
     * @param m
     *            source mat image
     * @param rects
     *            List of Rects indicating where the numbers are located
     * @return grid array indicating which tiles are empty; true == contains
     *         number, false == empty
     */
    private boolean[][] findNumTiles(Mat m, List<Rect> rects) {
        byte[][] arrayMat = addRectsToMat(m, rects);
        boolean[][] numTileArray = new boolean[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                numTileArray[i][j] = containsNumberTile(arrayMat, j, i);
            }
        }
        return numTileArray;
    }

    /**
     * determines if array holding mat contains a number
     *
     * @param matarray
     *            array containing pixel info for mat
     * @param xBound
     *            from 0 to 8 the number of the tile
     * @param yBound
     *            from 0 to 8 the number of the tile
     * @return true if empty, false otherwise
     */
    private boolean containsNumberTile(byte[][] matarray, int xBound, int yBound) {
        int area = matarray.length * matarray[0].length;
        int totalWhite = 0;
        int xStart = xBound * matarray[0].length / 9;
        int xEnd = xStart + matarray[0].length / 9 - 5;
        int yStart = yBound * matarray.length / 9;
        int yEnd = yStart + matarray.length / 9 - 5;

        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                if (matarray[y][x] == 1) {
                    totalWhite++;
                }
            }
        }
        if (totalWhite > 0 * area) {
            return true;
        } else {
            return false;
        }
    }

    private byte[][] addRectsToMat(Mat m, List<Rect> nums) {
        byte[][] matArray = new byte[m.rows()][m.cols()];

        for (Rect r : nums) {
            for (int y = r.y; y < r.y + r.height - 1; y++) {
                for (int x = r.x; x < r.x + r.width - 1; x++) {
                    // set to 1 (white)
                    matArray[y][x] = 1;
                }
            }
        }
        return matArray;
    }

    /**
     * safety method that trims integer to single digit
     *
     * @param n
     * @return
     */
    private int trimNum(int n) {
        while (n > 9) {
            n = n / 10;
        }
        return n;
    }

}