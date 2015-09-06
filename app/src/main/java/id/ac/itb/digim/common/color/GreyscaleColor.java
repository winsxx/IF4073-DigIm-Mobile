package id.ac.itb.digim.common.color;

public class GreyscaleColor extends Color{

    public GreyscaleColor(){
        super(0);
    }

    public int getGrey(){
        return color & 0xFF;
    }

    public void setGrey(int g){
        color = 0;
        color = color & (g << 16);
        color = color & (g << 8);
        color = color & (g << 0);
    }

    public static int getGreyFromRgb(int red, int green, int blue){
        return (red+green+blue)/3;
    }

}
