package id.ac.itb.digim.common.converter;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class ImageScaling {

    public static ImageMatrix<GreyscaleColor> bilinearResize(ImageMatrix<GreyscaleColor> originalImage,
                                                             int newWidth,
                                                             int newHeight){

        ImageMatrix<GreyscaleColor> resizedImage = new ImageMatrix<>(GreyscaleColor.class, newHeight, newWidth);
        int originalHeight = originalImage.getHeight();
        int originalWidth = originalImage.getWidth();
        float x_ratio = ((float) (originalWidth-1))/newWidth; // -1 to prevent array out of bound
        float y_ratio = ((float) (originalHeight-1))/newHeight;

        for(int i=0; i<newHeight; i++){
            for(int j=0; j<newWidth; j++){
                // Scale
                int x = (int)(x_ratio * j);
                int y = (int)(y_ratio * i);
                float xOffset = (x_ratio * j) - x;
                float yOffset = (y_ratio * i) - y;

                int upLeftColor = originalImage.getPixel(y,x).getGrey();
                int upRightColor = originalImage.getPixel(y,x+1).getGrey();
                int downLeftColor = originalImage.getPixel(y+1,x).getGrey();
                int downRightColor = originalImage.getPixel(y+1,x+1).getGrey();

                //Interpolation
                // A B
                // C D
                // grey = A(1-w)(1-h) + B(w)(1-h) + C(h)(1-w) + Dwh
                int interpolatedGrey = (int) (
                        upLeftColor*(1-xOffset)*(1-yOffset) +
                        upRightColor*xOffset*(1-yOffset) +
                        downLeftColor*yOffset*(1-xOffset) +
                        downRightColor*xOffset*yOffset);

                GreyscaleColor greyscaleColor = new GreyscaleColor();
                greyscaleColor.setGrey(interpolatedGrey);
                resizedImage.setPixel(i, j, greyscaleColor);
            }
        }

        return resizedImage;
    }

}
