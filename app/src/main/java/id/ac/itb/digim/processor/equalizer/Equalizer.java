package id.ac.itb.digim.processor.equalizer;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.Color;

public interface Equalizer<ColorType extends Color> {
    ImageMatrix<ColorType> getOriginalImageMatrix();
    ImageMatrix<ColorType> getEqualizedImageMatrix();
}
