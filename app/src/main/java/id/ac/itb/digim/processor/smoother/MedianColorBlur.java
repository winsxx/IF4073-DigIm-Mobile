package id.ac.itb.digim.processor.smoother;

import android.util.Log;

import java.util.Arrays;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class MedianColorBlur {

    public static final ImageMatrix<GreyscaleColor> medianColorBlur(ImageMatrix<GreyscaleColor> imageMatrix, int radius) {
        Log.d("[MEDIAN_COLOR_BLUR][MEDIAN_COLOR_BLUR]", "Function call with radius: " + radius);

        int width = imageMatrix.getWidth();
        int height = imageMatrix.getHeight();
        int boxSize = (radius * 2 + 1) * (radius * 2 + 1);
        ImageMatrix<GreyscaleColor> bluredImage = new ImageMatrix<GreyscaleColor>(GreyscaleColor.class, height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // List all surrounding value
                int[] value = new int[boxSize];
                int idx = 0;
                for (int iBox = i - radius; iBox < i + radius + 1; iBox++) {
                    for (int jBox = j - radius; jBox < j + radius + 1; jBox++) {
                        int x = Math.min(width - 1, Math.max(0, jBox));
                        int y = Math.min(height - 1, Math.max(0, iBox));
                        value[idx] = imageMatrix.getPixel(y, x).getGrey();
                        idx++;
                    }
                }
                // Get the median
                Arrays.sort(value);
                int median;
                if (boxSize % 2 == 0) {
                    median = (value[boxSize / 2] + value[boxSize / 2 + 1]) / 2;
                } else {
                    median = value[boxSize / 2];
                }
                // Assign value to new matrix
                GreyscaleColor color = new GreyscaleColor();
                color.setGrey(median);
                bluredImage.setPixel(i, j, color);
            }
        }

        return bluredImage;
    }

}
