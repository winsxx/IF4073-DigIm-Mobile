package id.ac.itb.digim.analytics.color;

import java.util.Arrays;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.Color;

public class ImageColorAnalyzer {

    public static long distinctColorCount(ImageMatrix image){
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int imageSize = imageWidth*imageHeight;

        int[] matrixTemp = new int[imageSize];
        for(int i=0; i<imageSize; i++) {
            Color color = image.getPixel(i / imageWidth, i % imageWidth);
            matrixTemp[i] = color.getColor();
        }

        Arrays.sort(matrixTemp);

        // Different Pixel Counting
        // if the pixel if first pixel, increase result
        // else if the pixel is different with previous pixel, increase result
        long result = 0;
        for(int i=0; i<imageSize; i++) {
            if(i==0) {
                result += 1;
            }
            else{
                int j = i-1;
                if( matrixTemp[i] != matrixTemp[j] ) {
                    result += 1;
                }
            }
        }

        return result;
    }
}
