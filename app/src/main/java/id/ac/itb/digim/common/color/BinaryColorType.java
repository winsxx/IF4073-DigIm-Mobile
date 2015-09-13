package id.ac.itb.digim.common.color;

public enum BinaryColorType {
    BLACK (0xff000000),
    WHITE (0xffffffff);

    private final int color;

    private BinaryColorType(int color){
        this.color = color;
    }

    public int getColor(){
        return color;
    }
}
