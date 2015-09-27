package id.ac.itb.digim.analytics.boundary;

/**
 * Created by Winson on 9/27/2015.
 */
public class FindObjectTopLeftPositionResult {
    private boolean found;
    private int topRow;
    private int leftCol;

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public int getTopRow() {
        return topRow;
    }

    public void setTopRow(int topRow) {
        this.topRow = topRow;
    }

    public int getLeftCol() {
        return leftCol;
    }

    public void setLeftCol(int leftCol) {
        this.leftCol = leftCol;
    }
}
