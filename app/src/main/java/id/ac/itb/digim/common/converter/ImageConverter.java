package id.ac.itb.digim.common.converter;

import android.graphics.Bitmap;

import id.ac.itb.digim.analytics.color.ImageColorAnalyzer;
import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.color.RgbColor;

public class ImageConverter {

    public  static ImageMatrix<GreyscaleColor> bitmapToGreyscaleMatrix(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        ImageMatrix<GreyscaleColor> greyscaleMatrix = new ImageMatrix<>(height, width);

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                RgbColor rgbColor = new RgbColor(bitmap.getPixel(j,i));
                int grey = GreyscaleColor.getGreyFromRgb(rgbColor.getRed(),
                        rgbColor.getGreen(),
                        rgbColor.getBlue());
                GreyscaleColor greyscaleColor = new GreyscaleColor();
                greyscaleColor.setGrey(grey);

                greyscaleMatrix.setPixel(i, j, greyscaleColor);
            }
        }

        return greyscaleMatrix;
    }

    public static ImageMatrix<BinaryColor> greyscaleToBinaryMatrix(
        ImageMatrix<GreyscaleColor> greyscaleColorImageMatrix){

        int width = greyscaleColorImageMatrix.getWidth();
        int height = greyscaleColorImageMatrix.getHeight();

        ImageMatrix<BinaryColor> result = new ImageMatrix<>(height, width);
        int treshold = otsuTresholder(greyscaleColorImageMatrix);

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                GreyscaleColor grey = greyscaleColorImageMatrix.getPixel(i, j);
                BinaryColor binary = new BinaryColor();

                if (grey.getGrey() >treshold ) {
                    binary.setBinaryColor(BinaryColorType.WHITE);
                } else {
                    binary.setBinaryColor(BinaryColorType.BLACK);
                }
                result.setPixel(i,j,binary);
            }
        }

        return result;
    }

    public static Bitmap imageMatrixToBitmap(ImageMatrix matrix){
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                bitmap.setPixel(j, i, matrix.getPixel(i, j).getColor());
            }
        }

        return bitmap;
    }

    private static int otsuTresholder(ImageMatrix<GreyscaleColor> imageMatrix){
        // Calculate histogram
        int[] histData = new int[256];
        for (int i = 0; i < imageMatrix.getHeight(); ++i) {
            for (int j = 0; j < imageMatrix.getWidth(); ++j) {
                histData[imageMatrix.getPixel(i, j).getGrey()]++;
            }
        }

        int total = imageMatrix.getHeight() * imageMatrix.getWidth();

        float sum = 0;
        for (int t=0 ; t<256 ; t++) sum += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t=0 ; t<256 ; t++) {
            wB += histData[t];               // Weight Background
            if (wB == 0) continue;

            wF = total - wB;                 // Weight Foreground
            if (wF == 0) break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB;            // Mean Background
            float mF = (sum - sumB) / wF;    // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float)wB * (float)wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }

}