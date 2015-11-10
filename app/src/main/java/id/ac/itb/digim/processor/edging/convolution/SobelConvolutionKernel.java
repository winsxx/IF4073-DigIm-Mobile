package id.ac.itb.digim.processor.edging.convolution;

import java.util.ArrayList;
import java.util.List;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class SobelConvolutionKernel implements ConvolutionKernel{
    private List<ConvolutionMatrix> _matrixList;

    public SobelConvolutionKernel(){
        _matrixList = new ArrayList<>();
        /**
         * -1 -2 -1
         *  0  0  0
         * +1 +2 +1
         */
        _matrixList.add(new ConvolutionMatrix(3, 3, new int[]{-1, -2, -1, 0, 0, 0, 1, 2, 1}));
        /**
         * -1  0  1
         * -2  0  2
         * -1  0  1
         */
        _matrixList.add(new ConvolutionMatrix(3, 3, new int[]{-1, 0, 1, -2, 0, 2, -1, 0, 1}));
    }

    @Override
    public ImageMatrix<GreyscaleColor> convolve(ImageMatrix<GreyscaleColor> imageMatrix) {
        int newMatrixWidth = imageMatrix.getWidth() - 3 + 1;
        int newMatrixHeight = imageMatrix.getHeight() - 3 + 1;
        ImageMatrix<GreyscaleColor> newMatrix = new ImageMatrix<GreyscaleColor>(
                GreyscaleColor.class,
                newMatrixHeight,
                newMatrixWidth);

        for(ConvolutionMatrix convolutionMatrix : _matrixList){
            ImageMatrix<GreyscaleColor> convolvedMat = convolutionMatrix.convolve(imageMatrix);
            for(int i=0; i<newMatrix.getHeight(); i++){
                for(int j=0; j<newMatrix.getWidth(); j++){
                    int value = Math.max(newMatrix.getPixel(i,j).getGrey(),
                            convolvedMat.getPixel(i,j).getGrey());
                    GreyscaleColor color = new GreyscaleColor();
                    color.setGrey(value);
                    newMatrix.setPixel(i,j, color);
                }
            }
        }

        return newMatrix;
    }
}
