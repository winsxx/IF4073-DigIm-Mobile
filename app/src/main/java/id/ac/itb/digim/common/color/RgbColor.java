package id.ac.itb.digim.common.color;

public class RgbColor extends Color {

    public RgbColor(){
        super(0xFF000000);
    }

    public RgbColor(int color) {
        super(color);
    }

    public RgbColor(int r, int g, int b){
        super(0xFF000000);
        setRed(r);
        setGreen(g);
        setBlue(b);
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

    public void setRGB(int r, int g, int b) {
        setRed(r);
        setGreen(g);
        setBlue(b);
    }
}