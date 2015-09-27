package id.ac.itb.digim.common.fill;

import android.graphics.Point;
import android.util.Log;

import java.util.Stack;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;

public class FloodFill {

    public static ImageMatrix<BinaryColor> BinaryFloodFill(int row, int col, BinaryColor backgroundColor,
                                                           ImageMatrix<BinaryColor> image) {
        Log.i("[FLOOD_FILL][BINARY_FLOOD_FILL]", "Flood fill from row:" + row + " , col:" + col + ", background color:" + backgroundColor.getBinaryColor());
        int counter = 0;
        ImageMatrix<BinaryColor> result = new ImageMatrix<>(image);

        BinaryColor now;
        Stack<Point> stackfill = new Stack<>();
        Point posisi = new Point(row, col);
        stackfill.push(posisi);

        while (!stackfill.empty()) {
            posisi = stackfill.pop();
            counter++;
            now = result.getPixel(posisi.x, posisi.y);
            result.setPixel(posisi.x, posisi.y, backgroundColor);

            if (backgroundColor.getBinaryColor() != now.getBinaryColor()) {
                if (posisi.x - 1 >= 0) {
                    Point po = new Point(posisi.x - 1, posisi.y);
                    stackfill.push(po);
                }

                if (posisi.x + 1 < image.getHeight()) {
                    Point po2 = new Point(posisi.x + 1, posisi.y);
                    stackfill.push(po2);
                }

                if (posisi.y + 1 < image.getWidth()) {
                    Point po3 = new Point(posisi.x, posisi.y + 1);
                    stackfill.push(po3);
                }

                if (posisi.y - 1 >= 0) {
                    Point po4 = new Point(posisi.x, posisi.y - 1);
                    stackfill.push(po4);
                }
            }
        }
        Log.i("[FLOOD_FILL][BINARY_FLOOD_FILL]", "Flood fill from row:" + row + " , col:" + col + ", pixel removed:" + counter);
        return result;
    }

}
