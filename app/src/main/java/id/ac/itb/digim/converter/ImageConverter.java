package id.ac.itb.digim.converter;

import android.graphics.Bitmap;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.color.RgbColor;

public class ImageConverter {

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

                greyscaleMatrix.setPixel(i, j, greyscaleColor);
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
                bitmap.setPixel(j, i, matrix.getPixel(i, j).getColor());
            }
        }

        return bitmap;
    }

}