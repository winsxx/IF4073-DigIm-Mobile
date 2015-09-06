package id.ac.itb.digim.converter;

import android.graphics.Bitmap;
import android.media.Image;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.color.RgbColor;

public class ImageConverter {

    private ImageMatrix<GreyscaleColor> result;

    public ImageConverter(ImageMatrix<GreyscaleColor> imageMatrix) {
        int[] cdf = new int[256];
        for (int i = 0; i < imageMatrix.getHeight(); ++i) {
            for (int j = 0; j < imageMatrix.getWidth(); ++j) {
                cdf[imageMatrix.getColor(i, j).getGrey()]++;
            }
        }

        int cdfMin = Integer.MAX_VALUE;
        int cf = 0;
        for (int greyScaleVal = 0; greyScaleVal < 256; ++greyScaleVal) {
            cf += cdf[greyScaleVal];
            if (cdf[greyScaleVal] != 0) {
                cdf[greyScaleVal] = cf;
                cdfMin = Math.min(cdfMin, cf);
            }
        }

        result = new ImageMatrix<GreyscaleColor>(imageMatrix.getHeight(), imageMatrix.getWidth());
        int area = imageMatrix.getHeight()*imageMatrix.getWidth();
        for (int i = 0; i < imageMatrix.getHeight(); ++i) {
            for (int j = 0; j < imageMatrix.getWidth(); ++j) {
                GreyscaleColor color = new GreyscaleColor();
                color.setGrey(255*(cdf[imageMatrix.getColor(i, j).getGrey()]-cdfMin)/(area-cdfMin));
                result.setColor(i, j, color);
            }
        }
    }

    public ImageMatrix<GreyscaleColor> getResult(double scale) {
        ImageMatrix<GreyscaleColor> ans = new ImageMatrix<GreyscaleColor>(result.getHeight(), result.getWidth());
        for (int i = 0; i < result.getHeight(); ++i) {
            for (int j = 0; j < result.getWidth(); ++j) {
                GreyscaleColor color = ans.getColor(i, j);
                color.setColor((int)((double)color.getColor()*scale));
                ans.setColor(i, j, ans.getColor(i, j));
            }
        }
        return ans;
    }

    public  static ImageMatrix<GreyscaleColor> toGreyscaleMatrix(Bitmap bitmap){
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

                greyscaleMatrix.setColor(i,j,greyscaleColor);
            }
        }

        return greyscaleMatrix;
    }

    public static Bitmap toBitmap(ImageMatrix matrix){
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                bitmap.setPixel(j, i, matrix.getColor(i,j).getColor());
            }
        }

        return bitmap;
    }

}