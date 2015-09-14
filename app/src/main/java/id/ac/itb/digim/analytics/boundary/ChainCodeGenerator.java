package id.ac.itb.digim.analytics.boundary;

import java.util.ArrayList;
import java.util.List;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;

public class ChainCodeGenerator {
    public enum FreemanCodeEightDirection{
        C0(0,0,1),
        C1(1,1,1),
        C2(2,1,0),
        C3(3,1,-1),
        C4(4,0,-1),
        C5(5,-1,-1),
        C6(6,-1,0),
        C7(7,-1,1);

        private final int mRowDirection;
        private final int mColDirection;
        private final int mCode;

        private FreemanCodeEightDirection(int code, int rowDirection, int colDirection){
            mRowDirection = rowDirection;
            mColDirection = colDirection;
            mCode = code;
        }

        public int getRowDirection(){
            return mRowDirection;
        }

        public int getColDirection(){
            return mColDirection;
        }

        public int getCode(){
            return mCode;
        }

    }

    public static List<Integer> generateChainCode(ImageMatrix<BinaryColor> imageMatrix, BinaryColorType backgroundColor){
        int width = imageMatrix.getWidth();
        int height = imageMatrix.getHeight();

        // Default value for boolean is false
        boolean image[][] = new boolean[height+4][width+4];

        // Set object of interest with value equal true
        // Get the top left object pixel location
        boolean found = false;
        int objTopLeftRow = -1;
        int objTopLeftCol = -1;
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(imageMatrix.getPixel(i,j).getBinaryColor() != backgroundColor){
                    image[i+2][j+2] = true;
                    if(!found){
                        found = true;
                        objTopLeftRow = i+1;
                        objTopLeftCol = j+1;
                    }
                }
            }
        }

        // Trace object to generate chain code
        List<Integer> chainCode = new ArrayList<>();
        if(found){
            boolean footPrint[][] = new boolean[height+4][width+4]; // to know which cell we already passed

            int cursorRow = objTopLeftRow;
            int cursorCol = objTopLeftCol--;
            //footPrint[cursorRow][cursorCol] = true;

            FreemanCodeEightDirection nextDirection = getNextAvailableMove(image, footPrint, cursorRow, cursorCol);
            while(nextDirection != null){
                chainCode.add(nextDirection.getCode());
                cursorRow += nextDirection.getRowDirection();
                cursorCol += nextDirection.getColDirection();
                footPrint[cursorRow][cursorCol] = true;

                nextDirection = getNextAvailableMove(image, footPrint, cursorRow, cursorCol);
            }
        }

        return chainCode;
    }

    public static List<Integer> generateNormalizedChainCode(ImageMatrix<BinaryColor> imageMatrix, BinaryColorType backgroundColor){
        return normalizeChainCode(generateChainCode(imageMatrix,backgroundColor));
    }

    private static List<Integer> normalizeChainCode(List<Integer> chainCode){
        int codeSize = FreemanCodeEightDirection.values().length;
        List<Integer> normalizedCC = new ArrayList<>();

        for(int i=1; i<chainCode.size(); i++){
            int diff = chainCode.get(i) - chainCode.get(i-1);
            if(diff < 0 ) diff += codeSize;
            normalizedCC.add(diff);
        }
        // Last case
        if(chainCode.size() > 1){
            int diff = chainCode.get(0) - chainCode.get(chainCode.size() - 1);
            if(diff < 0 ) diff += codeSize;
            normalizedCC.add(diff);
        }

        return normalizedCC;
    }

    private static boolean isBesideObject(boolean[][] image, int row, int col){
        return (image[row-1][col] || image[row+1][col] || image[row][col-1] || image[row][col+1]);
    }

    private static FreemanCodeEightDirection getNextAvailableMove(boolean[][] image,
                                                                  boolean[][] footPrint,
                                                                  final int row,
                                                                  final int col){

        for(FreemanCodeEightDirection direction : FreemanCodeEightDirection.values()){
            int nextRow = row+direction.getRowDirection();
            int nextCol = col+direction.getColDirection();
            if( !footPrint[nextRow][nextCol] && !image[nextRow][nextCol] && isBesideObject(image, nextRow, nextCol)){
                return direction;
            }
        }

        return null;
    }

}
