package id.ac.itb.digim.analytics.equalizer;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class GreyscaleCumulativeEqualizer implements Equalizer<GreyscaleColor> {
    private ImageMatrix<GreyscaleColor> originalImageMatrix;

    public GreyscaleCumulativeEqualizer(ImageMatrix<GreyscaleColor> originalImageMatrix){

    }

    @Override
    public ImageMatrix<GreyscaleColor> getOriginalImageMatrix() {
        return null;
    }

    @Override
    public ImageMatrix<GreyscaleColor> getEqualizedImageMatrix() {
        return null;
    }

    public ImageMatrix<GreyscaleColor> getScaledEqualizedImageMatrix(float scale){
        // Scale value between 0-1
        return null;
    }
}

