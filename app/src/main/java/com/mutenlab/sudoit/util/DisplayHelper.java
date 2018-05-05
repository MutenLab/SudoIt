package com.mutenlab.sudoit.util;

import android.app.Activity;
import android.graphics.Point;

/**
 * @author Ivan Cerrate.
 */
public class DisplayHelper {

    public static Point getDisplayDimens(Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point;
    }
}
