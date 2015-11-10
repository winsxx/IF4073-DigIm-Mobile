package id.ac.itb.digim.processor.edging.convolution;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class ConvolutionMatrix {
    private int matrix[][];
    private int _width;
    private int _height;

    public ConvolutionMatrix(int height, int width, int[] value){
        _width = width;
        _height = height;

        matrix = new int[height][width];
        for(int i=0; i<value.length; i++){
            matrix[i/width][i%width] = value[i];
        }
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getCell(int row, int col){
        return matrix[row][col];
    }

    public ImageMatrix<GreyscaleColor> convolve(ImageMatrix<GreyscaleColor> imageMatrix){
        int newMatrixWidth = imageMatrix.getWidth() - _width + 1;
        int newMatrixHeight = imageMatrix.getHeight() - _height +1;
        ImageMatrix<GreyscaleColor> newMatrix = new ImageMatrix<GreyscaleColor>(
                GreyscaleColor.class,
                newMatrixHeight,
                newMatrixWidth);

        for(int i=0; i<newMatrixHeight; i++){
            for(int j=0; j<newMatrixWidth; j++){
                int sum = 0;
                // Apply matrix for a pixel
                for(int mi=0; mi<_height; mi++){
                    for(int mj=0; mj<_width; mj++){
                        sum += imageMatrix.getPixel(i+mi,j+mj).getGrey() * matrix[mi][mj];
                    }
                }
                // New matrix pixel value
                sum = Math.abs(sum);
                if (sum>255) sum = 255;
                GreyscaleColor color = new GreyscaleColor();
                color.setGrey( sum );
                newMatrix.setPixel(i, j, color);
            }
        }

        return newMatrix;
    }
}
