package id.ac.itb.digim.common;

import id.ac.itb.digim.common.color.Color;

public class ImageMatrix<ColorType extends Color> {

    private Color[][] matrix;
    private int height;
    private int width;

    public ImageMatrix(int height, int width) {
        matrix = new Color[height][width];
        this.height = height;
        this.width = width;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public void setColor(int row, int col, ColorType color){
        matrix[row][col] = color;
    }

    public ColorType getColor(int row, int col){
        return (ColorType) matrix[row][col];
    }

}
