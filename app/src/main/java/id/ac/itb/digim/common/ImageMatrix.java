package id.ac.itb.digim.common;

import id.ac.itb.digim.common.color.Color;

public class ImageMatrix<ColorType extends Color> {

    private Color[][] pixels;
    private int height;
    private int width;

    public ImageMatrix(int height, int width) {
        pixels = new Color[height][width];
        this.height = height;
        this.width = width;
    }

    public ImageMatrix(ImageMatrix<ColorType> mat) {
        pixels = new Color[mat.getHeight()][mat.getWidth()];
        this.height = mat.getHeight();
        this.width = mat.getWidth();

        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                pixels[i][j] = mat.getPixel(i,j);
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
        pixels[row][col] = color;
    }

    public ColorType getPixel(int row, int col){
        return (ColorType) pixels[row][col];
    }

}
