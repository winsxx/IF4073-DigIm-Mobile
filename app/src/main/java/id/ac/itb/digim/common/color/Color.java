package id.ac.itb.digim.common.color;

public abstract class Color {

    protected Integer color;

    public Color() {
        color = 0;
    }

    public Color(int color) {
        this.color = color;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color.toString();
    }

}
