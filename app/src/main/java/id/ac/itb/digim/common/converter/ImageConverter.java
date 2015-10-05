package id.ac.itb.digim.common.converter;

import android.graphics.Bitmap;
import android.util.Log;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.color.RgbColor;

public class ImageConverter {

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

    public static ImageMatrix<BinaryColor>binaryToFiveSquareMatrix (
            ImageMatrix<BinaryColor> imageInput) {
        ImageMatrix<BinaryColor> fiveSquare = new ImageMatrix<>(BinaryColor.class, 5,5);
        BinaryColor black = new BinaryColor(); black.setBinaryColor(BinaryColorType.BLACK);
        BinaryColor white = new BinaryColor(); white.setBinaryColor(BinaryColorType.WHITE);

        int height = imageInput.getHeight()/5;
        int width = imageInput.getWidth()/5;
        int sHeight = imageInput.getHeight()%5;
        int sWidth = imageInput.getHeight()%5;

        int resHeight, resWidth;

        for (int i = 0 ; i< imageInput.getHeight(); i=i+height+(sHeight>0 ? 1 : 0)) {
            for (int j = 0; j< imageInput.getWidth(); j=j+width+(sWidth>0 ? 1 : 0)) {
                int numBlack = 0, numWhite = 0;

                for (int in=0; in<5; in++) {
                    for (int jn=0; jn<5; jn++) {
                        if (imageInput.getPixel(in+i, jn+j)==black) {
                            numBlack=numBlack+1;
                        } else {
                            numWhite=numWhite+1;
                        }
                    }
                }

                resHeight = i%(sHeight>0 ? height+1 : height);
                resWidth = j%(sWidth>0 ? width+1 : width);

                if (numBlack >= numWhite) {
                    fiveSquare.setPixel(resHeight,resWidth,black);
                } else {
                    fiveSquare.setPixel(resHeight,resWidth,white);
                }

                sWidth = sWidth>0 ? sWidth-1 : 0;
            }
            sHeight = sHeight>0 ? sHeight-1 : 0;
        }

        return fiveSquare;
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