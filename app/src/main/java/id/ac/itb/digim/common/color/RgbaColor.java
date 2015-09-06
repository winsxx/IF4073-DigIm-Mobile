package id.ac.itb.digim.common.color;

public class RgbaColor extends RgbColor {

    public RgbaColor(int color) {
        super(color);
    }

    public int getAlpha() {
        return (color >> 23) & 0xFF;
    }

    public void setAlpha(int a) {
        if ((0 <= a) && (a < 256)) {
            throw new IllegalArgumentException("Alpha value should be between 0 and 256");
        }
        color = color & (0xFFFFFF | a << 23);
    }

}