package id.ac.itb.digim.processor.clustering;

public class Sample {
    private int x;
    private int y;
    private int mCluster;

    public Sample() {
        x = 0;
        y = 0;
        mCluster = 0;
    }

    public Sample(int x, int y) {
        this.x = x;
        this.y = y;
        mCluster = 0;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getCluster() {
        return this.mCluster;
    }

    public void setX(int _x) {
        x = _x;
    }

    public void setY(int _y) {
        y = _y;
    }

    public void setCluster(int cluster) {
        mCluster = cluster;
    }


}