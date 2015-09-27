package id.ac.itb.digim.common;

import android.util.Log;

import id.ac.itb.digim.common.color.Color;

public class ImageMatrix<ColorType extends Color> {

    private final Class<ColorType> colorType;
    private int[][] pixels;
    private int height;
    private int width;

    public ImageMatrix(Class<ColorType> classColorType, int height, int width) {

        pixels = new int[height][width];
        this.height = height;
        this.width = width;
        colorType = classColorType;
    }

    public ImageMatrix(ImageMatrix<ColorType> mat) {
        colorType = mat.getColorType();
        pixels = new int[mat.getHeight()][mat.getWidth()];
        this.height = mat.getHeight();
        this.width = mat.getWidth();

        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                pixels[i][j] = mat.getPixel(i, j).getColor();
            }
        }
    }
    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public void setPixel(int row, int col, ColorType color){
        pixels[row][col] = color.getColor();
    }

    public ColorType getPixel(int row, int col){
        try {
            ColorType color = colorType.newInstance();
            color.setColor(pixels[row][col]);
            return color;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<ColorType> getColorType() {
        return colorType;
    }
}
