package id.ac.itb.digim.processor.skeletonizer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.Point;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;

public class ZhangSuenBinaryThinner {
    private static final int NEXT_ROW[] = {-1,-1, 0, 1, 1, 1, 0,-1};
    private static final int NEXT_COL[] = {0 , 1, 1, 1, 0,-1,-1,-1};
    private static final int NUM_NEIGHBOUR_PIXEL = 8;

    public static ImageMatrix<BinaryColor> imageThinning(ImageMatrix<BinaryColor> imageMatrix, BinaryColorType backgroundColor){
        Log.d("[ZHANG_SUEN_BINARY_THINNER][IMAGE_THINNING]", "Function call with background color: " + backgroundColor);
        ImageMatrix<BinaryColor> thinnedImage = new ImageMatrix<>(imageMatrix);
        int width = thinnedImage.getWidth();
        int height = thinnedImage.getHeight();
        BinaryColorType foregroundColor = BinaryColorType.BLACK;
        if(backgroundColor == BinaryColorType.BLACK){
            foregroundColor = BinaryColorType.WHITE;
        }

        int iterationCount = 0;
        List<Point> pointList = new ArrayList<>();
        boolean change = true;
        while(change){
            iterationCount++;
            change = false;

            for(int i=1; i+1<height; i++){
                for(int j=1; j+1<width; j++){
                    if(thinnedImage.getPixel(i,j).getBinaryColor() == foregroundColor){
                        int nBlackNeighbour = neighbourCount(thinnedImage, i, j, backgroundColor);
                        int nNeighbourTransition = neighbourTransition(thinnedImage, i, j, backgroundColor);
                        if(nBlackNeighbour>=2 && nBlackNeighbour<=6 && nNeighbourTransition==1){
                            boolean cleanCondition;
                            // Step 1
                            cleanCondition = true;
                            if (thinnedImage.getPixel(i-1,j).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i,j+1).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i+1,j).getBinaryColor() == foregroundColor){
                                 cleanCondition = false;
                            }
                            if (thinnedImage.getPixel(i,j+1).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i+1,j).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i,j-1).getBinaryColor() == foregroundColor){
                                cleanCondition = false;
                            }
                            if(cleanCondition){
                                pointList.add(new Point(i,j));
                            }
                        }
                    }
                }
            }

            // Thinning
            if (!pointList.isEmpty()){
                change = true;
                for(Point p : pointList){
                    BinaryColor binaryColor = new BinaryColor();
                    binaryColor.setBinaryColor(backgroundColor);
                    thinnedImage.setPixel(p.getRow(), p.getCol(), binaryColor);
                }
                pointList.clear();
            }

            for(int i=1; i+1<height; i++){
                for(int j=1; j+1<width; j++){
                    if(thinnedImage.getPixel(i,j).getBinaryColor() == foregroundColor){
                        int nBlackNeighbour = neighbourCount(thinnedImage, i, j, backgroundColor);
                        int nNeighbourTransition = neighbourTransition(thinnedImage, i, j, backgroundColor);
                        if(nBlackNeighbour>=2 && nBlackNeighbour<=6 && nNeighbourTransition==1){
                            boolean cleanCondition;
                            // Step 2
                            cleanCondition = true;
                            if (thinnedImage.getPixel(i-1,j).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i,j+1).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i,j-1).getBinaryColor() == foregroundColor){
                                cleanCondition = false;
                            }
                            if (thinnedImage.getPixel(i-1,j).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i+1,j).getBinaryColor() == foregroundColor &&
                                    thinnedImage.getPixel(i,j-1).getBinaryColor() == foregroundColor){
                                cleanCondition = false;
                            }
                            if(cleanCondition){
                                pointList.add(new Point(i,j));
                            }
                        }
                    }
                }
            }

            // Thinning
            if (!pointList.isEmpty()){
                change = true;
                for(Point p : pointList){
                    BinaryColor binaryColor = new BinaryColor();
                    binaryColor.setBinaryColor(backgroundColor);
                    thinnedImage.setPixel(p.getRow(), p.getCol(), binaryColor);
                }
                pointList.clear();
            }
        }
        Log.d("[ZHANG_SUEN_BINARY_THINNER][IMAGE_THINNING]", "Function call done with " + iterationCount +" iteration");
        return thinnedImage;
    }

    // Precondition: row and col is not at boundary
    private static int neighbourCount(ImageMatrix<BinaryColor> imageMatrix, int row, int col, BinaryColorType backgroundColor){
        int count=0;
        for(int i=0; i<NUM_NEIGHBOUR_PIXEL; i++){
            int nRow = row + NEXT_ROW[i];
            int nCol = col + NEXT_COL[i];

            BinaryColorType binaryColor = imageMatrix.getPixel(nRow, nCol).getBinaryColor();
            count += (binaryColor != backgroundColor)? 1 : 0;
        }
        return count;
    }

    // Precondition: row and col is not at boundary
    private static int neighbourTransition(ImageMatrix<BinaryColor> imageMatrix, int row, int col, BinaryColorType backgroundColor){
        int count=0;
        for(int i=0; i<NUM_NEIGHBOUR_PIXEL; i++){
            int thisRow = row + NEXT_ROW[i];
            int thisCol = col + NEXT_COL[i];
            int nextRow = row + NEXT_ROW[(i+1)%NUM_NEIGHBOUR_PIXEL];
            int nextCol = col + NEXT_COL[(i+1)%NUM_NEIGHBOUR_PIXEL];

            BinaryColorType thisColor = imageMatrix.getPixel(thisRow, thisCol).getBinaryColor();
            BinaryColorType nextColor = imageMatrix.getPixel(nextRow, nextCol).getBinaryColor();
            count += (thisColor == backgroundColor && nextColor != backgroundColor)? 1 : 0;
        }
        return count;
    }
}
