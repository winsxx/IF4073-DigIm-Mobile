package id.ac.itb.digim.common.Fill;

import android.graphics.Point;

import java.util.Stack;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.Color;

public class FloodFill {

    public static ImageMatrix<BinaryColor> BinaryFloodFill(int row, int col, BinaryColor color,
                                                           ImageMatrix<BinaryColor> image)
    {
        //System.out.println("Filling........");
        ImageMatrix<BinaryColor> result = new ImageMatrix<>(image);

        BinaryColor now;
        Stack<Point> stackfill = new Stack<>();
        Point posisi = new Point(row,col);
        stackfill.push(posisi);

        while(!stackfill.empty()){
            posisi = stackfill.pop();
            now = result.getPixel(posisi.x, posisi.y);
            result.setPixel(posisi.x, posisi.y, color);

            if(color.getBinaryColor() != now.getBinaryColor()) {
                if (posisi.x-1 >= 0) {
                    Point po = new Point(posisi.x-1,posisi.y);
                    stackfill.push(po);
                }

                if (posisi.x+1 <= image.getHeight()) {
                    Point po2 = new Point(posisi.x+1,posisi.y);
                    stackfill.push(po2);
                }

                if (posisi.y+1 <= image.getWidth()) {
                    Point po3 = new Point(posisi.x,posisi.y+1);
                    stackfill.push(po3);
                }

                if (posisi.y-1 >= 0) {
                    Point po4 = new Point(posisi.x,posisi.y-1);
                    stackfill.push(po4);
                }
            }
        }

        return result;
    }

}
