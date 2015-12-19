package id.ac.itb.digim.common.converter;

import android.graphics.Bitmap;
import android.util.Log;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.color.RgbColor;

public class ImageConverter {
    private static final int squareSize = 5;

    public  static ImageMatrix<RgbColor> bitmapToRgbMatrix(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        ImageMatrix<RgbColor> rgbMatrix = new ImageMatrix<>(RgbColor.class, height, width);

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                RgbColor rgbColor = new RgbColor(bitmap.getPixel(j,i));
                rgbMatrix.setPixel(i, j, rgbColor);
            }
        }
        Log.i("[IMAGE_CONVERTER][BITMAP_TO_RGB]", "Bitmap to rgb done");
        return rgbMatrix;
    }

    public  static ImageMatrix<GreyscaleColor> bitmapToGreyscaleMatrix(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        ImageMatrix<GreyscaleColor> greyscaleMatrix = new ImageMatrix<>(GreyscaleColor.class, height, width);

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
        Log.i("[IMAGE_CONVERTER][BITMAP_TO_GREYSCALE]", "Bitmap to greyscale done");
        return greyscaleMatrix;
    }

    public static ImageMatrix<BinaryColor> greyscaleToBinaryMatrix(
        ImageMatrix<GreyscaleColor> greyscaleColorImageMatrix){

        int width = greyscaleColorImageMatrix.getWidth();
        int height = greyscaleColorImageMatrix.getHeight();

        ImageMatrix<BinaryColor> result = new ImageMatrix<>(BinaryColor.class, height, width);
        int treshold = otsuThresholder(greyscaleColorImageMatrix);

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
        Log.i("[IMAGE_CONVERTER][GREYSCALE_TO_BINARY]", "With treshold " + treshold);
        return result;
    }

    public static ImageMatrix<BinaryColor>binaryToSquareMatrix (
            ImageMatrix<BinaryColor> imageInput) {

        ImageMatrix<BinaryColor> square = new ImageMatrix<>(BinaryColor.class, squareSize,squareSize);
        BinaryColor black = new BinaryColor(); black.setBinaryColor(BinaryColorType.BLACK);
        BinaryColor white = new BinaryColor(); white.setBinaryColor(BinaryColorType.WHITE);

        int height = imageInput.getHeight()/squareSize;
        int width = imageInput.getWidth()/squareSize;
        int sHeight = imageInput.getHeight()%squareSize;
        int sWidth = imageInput.getHeight()%squareSize;

        for (int i = 0 ; i< squareSize; i++) {
            for (int j = 0; j< squareSize; j++) {
                int numBlack = 0, numWhite = 0;

                for (int in=0; in<height+(i<sHeight ? 1 : 0); in++) {
                    for (int jn=0; jn<width+(j<sWidth ? 1 : 0); jn++) {

                        int rowB = (i<sHeight) ? i*(height+1)+in : sHeight*(height+1)+(i-sHeight)*height+in;
                        int colB = (j<sWidth) ? j*(width+1)+jn : sWidth*(width+1)+(j-sWidth)*width+jn;

                        if (imageInput.getPixel(rowB, colB).getBinaryColor()==BinaryColorType.BLACK) {
                            numBlack=numBlack+1;
                        } else {
                            numWhite=numWhite+1;
                        }
                    }
                }

                if (numBlack >= 0.3*(numBlack+numWhite)) {
                    square.setPixel(i, j, black);
                } else {
                    square.setPixel(i, j, white);
                }
            }
        }

        return square;
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

    public static double[][] greyscaleMatrixToDoubleMatrix(ImageMatrix<GreyscaleColor> matrix){
        double output[][] = new double[matrix.getHeight()][matrix.getWidth()];
        for(int i=0; i<matrix.getHeight(); i++){
            for(int j=0; j<matrix.getWidth(); j++){
                output[i][j] = (double) matrix.getPixel(i, j).getGrey();
            }
        }
        return output;
    }

    public static ImageMatrix<GreyscaleColor> doubleMatrixToGreyscaleMatrix(double[][] matrix){
        ImageMatrix<GreyscaleColor> greyscaleMatrix =
                new ImageMatrix<GreyscaleColor>(GreyscaleColor.class, matrix.length, matrix[0].length);

        for(int i=0; i<greyscaleMatrix.getHeight(); i++){
            for(int j=0; j<greyscaleMatrix.getWidth(); j++){
                GreyscaleColor color = new GreyscaleColor();
                int temp = (int) Math.round(matrix[i][j]);
                if (temp < 0) temp = 0;
                if (temp > 255) temp = 255;
                color.setGrey(temp);
                greyscaleMatrix.setPixel(i, j, color);
            }
        }

        return greyscaleMatrix;
    }

    private static int otsuThresholder(ImageMatrix<GreyscaleColor> imageMatrix) {
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

        Log.i("[IMAGE_CONVERTER][OTSU_THRESHOLDER]", "Otsu treshold " + threshold);
        return threshold;
    }

}