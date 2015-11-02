package id.ac.itb.digim.processor.edging;

import android.util.Log;

import java.util.Arrays;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class EdgeDetection {

    public static ImageMatrix<GreyscaleColor> homogenEdging(ImageMatrix<GreyscaleColor> input) {
        int width = input.getWidth();
        int height = input.getHeight();

        ImageMatrix<GreyscaleColor> edgeMatrix = new ImageMatrix<GreyscaleColor>(GreyscaleColor.class,height,width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int curr = input.getPixel(i,j).getGrey();

                //List all surrounding value
                int[] value = new int[8];
                int idx = 0;
                for (int iBox = i-1; iBox < i+1; iBox++) {
                    for (int jBox = j-1; jBox < j+1; jBox++) {
                        int x = Math.min(width - 1, Math.max(0, jBox));
                        int y = Math.min(height - 1, Math.max(0, iBox));
                        value[idx] = input.getPixel(y, x).getGrey();
                        idx++;
                    }
                }

                //Compute new value
                int maxDiff = 0;
                for (int x=0; x<8; x++) {
                    if (Math.abs(curr-value[x]) > maxDiff) {
                        maxDiff = Math.abs(curr-value[x]);
                    }
                }

                // Assign value to new matrix
                GreyscaleColor color = new GreyscaleColor();
                color.setGrey(maxDiff);
                edgeMatrix.setPixel(i, j, color);
            }
        }

        return edgeMatrix;
    }

    public static ImageMatrix<GreyscaleColor> differenceEdging(ImageMatrix<GreyscaleColor> input) {
        int width = input.getWidth();
        int height = input.getHeight();

        ImageMatrix<GreyscaleColor> edgeMatrix = new ImageMatrix<GreyscaleColor>(GreyscaleColor.class,height,width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int curr = input.getPixel(i,j).getGrey();

                //List all surrounding value
                int[] value = new int[8];
                int idx = 0;
                for (int iBox = i-1; iBox < i+1; iBox++) {
                    for (int jBox = j-1; jBox < j+1; jBox++) {
                        int x = Math.min(width - 1, Math.max(0, jBox));
                        int y = Math.min(height - 1, Math.max(0, iBox));
                        value[idx] = input.getPixel(y, x).getGrey();
                        idx++;
                    }
                }

                //Compute new value
                int diff1 = Math.abs(value[0]-value[4]);
                int diff2 = Math.abs(value[1]-value[5]);
                int diff3 = Math.abs(value[2]-value[6]);
                int diff4 = Math.abs(value[3]-value[7]);
                int maxDiff = Math.max(Math.max(diff1,diff2),Math.max(diff3,diff4));

                // Assign value to new matrix
                GreyscaleColor color = new GreyscaleColor();
                color.setGrey(maxDiff);
                edgeMatrix.setPixel(i, j, color);
            }
        }

        return edgeMatrix;
    }
}
