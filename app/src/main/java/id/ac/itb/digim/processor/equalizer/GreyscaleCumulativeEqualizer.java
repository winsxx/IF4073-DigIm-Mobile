package id.ac.itb.digim.processor.equalizer;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;

public class GreyscaleCumulativeEqualizer implements Equalizer<GreyscaleColor> {
    private ImageMatrix<GreyscaleColor> originalImageMatrix;
    private ImageMatrix<GreyscaleColor> equalizedImageMatrix;

    public GreyscaleCumulativeEqualizer(ImageMatrix<GreyscaleColor> imageMatrix){
        originalImageMatrix = imageMatrix;
        equalizedImageMatrix = generateEqualizedImageMatrix(originalImageMatrix);
    }

    @Override
    public ImageMatrix<GreyscaleColor> getOriginalImageMatrix() {
        return null;
    }

    @Override
    public ImageMatrix<GreyscaleColor> getEqualizedImageMatrix() {
        return null;
    }

    private ImageMatrix<GreyscaleColor> generateEqualizedImageMatrix(ImageMatrix<GreyscaleColor> imageMatrix){
        ImageMatrix<GreyscaleColor> result = new ImageMatrix<>(imageMatrix.getHeight(), imageMatrix.getWidth());

        int[] cdf = new int[256];
        for (int i = 0; i < imageMatrix.getHeight(); ++i) {
            for (int j = 0; j < imageMatrix.getWidth(); ++j) {
                cdf[imageMatrix.getPixel(i, j).getGrey()]++;
            }
        }

        int cdfMin = Integer.MAX_VALUE;
        int cf = 0;
        for (int greyScaleVal = 0; greyScaleVal < 256; ++greyScaleVal) {
            cf += cdf[greyScaleVal];
            if (cdf[greyScaleVal] != 0) {
                cdf[greyScaleVal] = cf;
                cdfMin = Math.min(cdfMin, cf);
            }
        }

        result = new ImageMatrix<GreyscaleColor>(imageMatrix.getHeight(), imageMatrix.getWidth());
        int area = imageMatrix.getHeight()*imageMatrix.getWidth();
        for (int i = 0; i < imageMatrix.getHeight(); ++i) {
            for (int j = 0; j < imageMatrix.getWidth(); ++j) {
                GreyscaleColor color = new GreyscaleColor();
                color.setGrey(255*(cdf[imageMatrix.getPixel(i, j).getGrey()]-cdfMin)/(area-cdfMin));
                result.setPixel(i, j, color);
            }
        }

        return result;
    }

    public ImageMatrix<GreyscaleColor> getScaledEqualizedImageMatrix(float scale){
        ImageMatrix<GreyscaleColor> ans = new ImageMatrix<GreyscaleColor>(
                equalizedImageMatrix.getHeight(),
                equalizedImageMatrix.getWidth());

        for (int i = 0; i < equalizedImageMatrix.getHeight(); ++i) {
            for (int j = 0; j < equalizedImageMatrix.getWidth(); ++j) {
                GreyscaleColor color = new GreyscaleColor();
                int grey = (int) (equalizedImageMatrix.getPixel(i,j).getGrey() * scale);
                color.setGrey(grey);
                ans.setPixel(i, j, color);
            }
        }
        return ans;
    }

}

