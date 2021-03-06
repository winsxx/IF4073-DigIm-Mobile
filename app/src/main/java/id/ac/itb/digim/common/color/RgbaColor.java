package id.ac.itb.digim.common.color;

public class RgbaColor extends Color {

    public RgbaColor(){
        super(0);
    }

    public RgbaColor(int color) {
        super(color);
    }

    public int getRed() {
        return (color >> 16) & 0xFF;
    }

    public void setRed(int r) {
        color = (color & 0xFF00FFFF) | (r << 16);
    }

    public int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public void setGreen(int g) {
        color = (color & 0xFFFF00FF) | (g << 8);
    }

    public int getBlue() {
        return (color & 0xFF);
    }

    public void setBlue(int b) {
        color = (color & 0xFFFFFF00) | b;
    }

    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }

    public void setAlpha(int a) {
        color = (color & 0xFFFFFF) | (a << 24);
    }


}