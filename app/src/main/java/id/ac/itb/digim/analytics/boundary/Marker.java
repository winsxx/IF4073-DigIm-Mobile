package id.ac.itb.digim.analytics.boundary;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.Point;
import id.ac.itb.digim.common.color.RgbColor;

public class Marker {

    /**
     * Box will drawn to image matrix paramter
     * @param imageMatrix
     * @param color
     * @param topLeft
     * @param bottomRight
     */
    public static void boxMark(ImageMatrix<RgbColor> imageMatrix,
                               RgbColor color,
                               Point topLeft,
                               Point bottomRight,
                               int weight){

        int right = bottomRight.getCol();
        int left = topLeft.getCol();
        int top = topLeft.getRow();
        int bottom = bottomRight.getRow();

        for(int j=left; j<=right; j++){
            for(int w=0; w<weight; w++){
                imageMatrix.setPixel(top+w, j, color);
                imageMatrix.setPixel(bottom-w, j, color);
            }
        }

        for(int i=top; i<=bottom; i++){
            for(int w=0; w<weight; w++){
                imageMatrix.setPixel(i, left+w, color);
                imageMatrix.setPixel(i, right-w, color);
            }
        }

    }

}
