package id.ac.itb.digim.analytics.equalizer;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.Color;

public interface Equalizer<ColorType extends Color> {
    ImageMatrix<ColorType> getOriginalImageMatrix();
    ImageMatrix<ColorType> getEqualizedImageMatrix();
}
