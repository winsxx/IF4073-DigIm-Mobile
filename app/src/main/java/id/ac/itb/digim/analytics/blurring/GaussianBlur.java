package id.ac.itb.digim.analytics.blurring;

import android.util.Log;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class GaussianBlur {

    public static ImageMatrix<GreyscaleColor> gaussBlur(ImageMatrix<GreyscaleColor> source, int radius) {
        Log.d("[GAUSSIAN_BLUR][GAUSS_BLUR]", "Function call with radius: " + radius);
        int[] bxs = boxesForGauss(radius, 3);

        ImageMatrix<GreyscaleColor> target;

        target = boxBlur(source, (bxs[0] - 1) / 2);
        source = boxBlur(target, (bxs[1] - 1) / 2);
        target = boxBlur(source, (bxs[2] - 1) / 2);

        return target;
    }

    private static ImageMatrix<GreyscaleColor> boxBlur(ImageMatrix<GreyscaleColor> source, int radius) {
        Log.d("[GAUSSIAN_BLUR][BOX_BLUR]", "Function call with radius: " + radius);
        ImageMatrix<GreyscaleColor> target = new ImageMatrix<>(source);
        source = boxBlurH(target, radius);
        target = boxBlurT(source, radius);
        return target;
    }

    private static ImageMatrix<GreyscaleColor> boxBlurH(ImageMatrix<GreyscaleColor> source, int radius) {
        Log.d("[GAUSSIAN_BLUR][BOX_BLUR_H]", "Function call with radius: " + radius);
        ImageMatrix<GreyscaleColor> target = new ImageMatrix<>(GreyscaleColor.class, source.getHeight(), source.getWidth());
        int width = source.getWidth();
        int height = source.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int val = 0;
                for (int ix = j - radius; ix < j + radius + 1; ix++) {
                    int x = Math.min(width - 1, Math.max(0, ix));
                    val += source.getPixel(i, x).getGrey();
                }
                GreyscaleColor greyscaleColor = new GreyscaleColor();
                greyscaleColor.setGrey(val / (radius * 2 + 1));
                target.setPixel(i, j, greyscaleColor);
            }
        }
        return target;
    }

    private static ImageMatrix<GreyscaleColor> boxBlurT(ImageMatrix<GreyscaleColor> source, int radius) {
        Log.d("[GAUSSIAN_BLUR][BOX_BLUR_T]", "Function call with radius: " + radius);
        ImageMatrix<GreyscaleColor> target = new ImageMatrix<>(GreyscaleColor.class, source.getHeight(), source.getWidth());
        int width = source.getWidth();
        int height = source.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int val = 0;
                for (int iy = i - radius; iy < i + radius + 1; iy++) {
                    int y = Math.min(height - 1, Math.max(0, iy));
                    val += source.getPixel(y, j).getGrey();
                }
                GreyscaleColor greyscaleColor = new GreyscaleColor();
                greyscaleColor.setGrey(val / (radius * 2 + 1));
                target.setPixel(i, j, greyscaleColor);
            }
        }
        return target;
    }

    private static int[] boxesForGauss(double standardDeviation, int numOfBoxes) {
        Log.d("[GAUSSIAN_BLUR][BOXES_FOR_GAUSS]", "Function call with number of boxes: " + numOfBoxes);

        int wIdeal = (int) Math.sqrt((12 * standardDeviation * standardDeviation / numOfBoxes) + 1);  // Ideal averaging filter width
        int wl = (int) Math.floor(wIdeal);
        if (wl % 2 == 0) wl--;
        int wu = wl + 2;

        int mIdeal = (int) (Math.round(12.0 * standardDeviation * standardDeviation - numOfBoxes * wl * wl - 4.0 * numOfBoxes * wl - 3.0 * numOfBoxes) / (-4.0 * wl - 4.0));

        int[] sizes = new int[numOfBoxes];
        for (int i = 0; i < numOfBoxes; i++) {
            sizes[i] = (i < mIdeal) ? wl : wu;
        }
        return sizes;
    }
}
