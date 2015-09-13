package id.ac.itb.digim.common.color;

public class BinaryColor extends Color {

    public BinaryColor() {
        super(0);
    }

    public void setBinaryColor(BinaryColorType colorType) {
        color = colorType.getColor();
    }

    public BinaryColorType getBinaryColor() {
        if (BinaryColorType.BLACK.getColor() == color) {
            return BinaryColorType.BLACK;
        } else {
            return BinaryColorType.WHITE;
        }
    }

}
