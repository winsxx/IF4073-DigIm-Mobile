package id.ac.itb.digim.processor.edging.convolution;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public interface ConvolutionKernel {
    ImageMatrix<GreyscaleColor> convolve(ImageMatrix<GreyscaleColor> imageMatrix);
}
