package id.ac.itb.digim.processor.edging.convolution;

import java.util.ArrayList;
import java.util.List;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class RobertConvolutionKernel implements ConvolutionKernel{
    private List<ConvolutionMatrix> _matrixList;

    public RobertConvolutionKernel(){
        _matrixList = new ArrayList<>();
        /**
         * +1  0
         *  0 -1
         */
        _matrixList.add(new ConvolutionMatrix(2, 2, new int[]{1, 0, 0, -1}));
        /**
         *  0 +1
         * -1  0
         */
        _matrixList.add(new ConvolutionMatrix(2, 2, new int[]{0, 1, -1, 0}));
    }

    @Override
    public ImageMatrix<GreyscaleColor> convolve(ImageMatrix<GreyscaleColor> imageMatrix) {
        int newMatrixWidth = imageMatrix.getWidth() - 2 + 1;
        int newMatrixHeight = imageMatrix.getHeight() - 2 + 1;
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
