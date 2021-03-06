package id.ac.itb.digim.analytics.boundary;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.fill.FloodFill;

public class ChainCodeGenerator {
    private static final int IMAGE_OFFSET = 3;
    private static final int CHAIN_CODE_COARSE_CONST = 3;

    private static FindObjectTopLeftPositionResult getAnObjectTopLeftPosition(
            ImageMatrix<BinaryColor> imageMatrix, BinaryColorType backgroundColor) {

        Log.i("[CHAIN_CODE_GENERATOR][GET_AN_OBJECT_TOP_LEFT_POSITION]", "With background color: " + backgroundColor);

        int width = imageMatrix.getWidth();
        int height = imageMatrix.getHeight();

        boolean found = false;
        int objTopLeftRow = 0;
        int objTopLeftCol = 0;

        int n = 0, i = 0, j = 0;
        while (n < width * height) {
            if (imageMatrix.getPixel(i, j).getBinaryColor() != backgroundColor) {
                found = true;
                objTopLeftRow = i;
                objTopLeftCol = j;
                break;
            } else {
                n++;
                if (j < width - 1) {
                    j++;
                } else {
                    j = 0;
                    i++;
                }
            }
        }

        FindObjectTopLeftPositionResult topLeftPositionResult = new FindObjectTopLeftPositionResult();
        topLeftPositionResult.setFound(found);
        topLeftPositionResult.setTopRow(objTopLeftRow);
        topLeftPositionResult.setLeftCol(objTopLeftCol);

        if (topLeftPositionResult.isFound()) {
            Log.i("[CHAIN_CODE_GENERATOR][GET_AN_OBJECT_TOP_LEFT_POSITION]", "TopLeft Row:" + topLeftPositionResult.getTopRow() + ", Col:" + topLeftPositionResult.getLeftCol());
        } else {
            Log.i("[CHAIN_CODE_GENERATOR][GET_AN_OBJECT_TOP_LEFT_POSITION]", "Object not found");
        }

        return topLeftPositionResult;
    }

    private static FindObjectTopLeftPositionResult getAnObjectTopLeftPosition(boolean image[][], int startRow, int startCol) {

        Log.i("[CHAIN_CODE_GENERATOR][GET_AN_OBJECT_TOP_LEFT_POSITION]", "Get first top left true");

        int width = image[0].length;
        int height = image.length;

        boolean found = false;
        int objTopLeftRow = 0;
        int objTopLeftCol = 0;

        int n = startRow * width + startCol, i = startRow, j = startCol;
        while (n < width * height) {
            if (image[i][j]) {
                found = true;
                objTopLeftRow = i;
                objTopLeftCol = j;
                break;
            } else {
                n++;
                if (j < width - 1) {
                    j++;
                } else {
                    j = 0;
                    i++;
                }
            }
        }

        FindObjectTopLeftPositionResult topLeftPositionResult = new FindObjectTopLeftPositionResult();
        topLeftPositionResult.setFound(found);
        topLeftPositionResult.setTopRow(objTopLeftRow);
        topLeftPositionResult.setLeftCol(objTopLeftCol);

        if (topLeftPositionResult.isFound()) {
            Log.i("[CHAIN_CODE_GENERATOR][GET_AN_OBJECT_TOP_LEFT_POSITION]", "TopLeft Row:" + topLeftPositionResult.getTopRow() + ", Col:" + topLeftPositionResult.getLeftCol());
        } else {
            Log.i("[CHAIN_CODE_GENERATOR][GET_AN_OBJECT_TOP_LEFT_POSITION]", "Object not found");
        }

        return topLeftPositionResult;
    }

    private static boolean[][] generateObjectOfInterestMatrix(ImageMatrix<BinaryColor> imageMatrix, BinaryColorType backgroundColor) {
        int width = imageMatrix.getWidth();
        int height = imageMatrix.getHeight();

        // Default value for boolean is false
        boolean image[][] = new boolean[height + (2 * IMAGE_OFFSET)][width + (2 * IMAGE_OFFSET)];

        // Set object of interest with value equal true
        // Get the top left object pixel location
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (imageMatrix.getPixel(i, j).getBinaryColor() != backgroundColor) {
                    image[i + IMAGE_OFFSET][j + IMAGE_OFFSET] = true;
                }
            }
        }

        return image;
    }

    /**
     * Generate chain code : untuk mendapatkan chain code normal dari imageMatrix
     */

    public static List<Integer> generateChainCode(ImageMatrix<BinaryColor> imageMatrix, BinaryColorType backgroundColor) {
        FindObjectTopLeftPositionResult topLeftPosition = getAnObjectTopLeftPosition(imageMatrix, backgroundColor);
        if (topLeftPosition.isFound()) {
            return generateChainCode(imageMatrix, backgroundColor,
                    topLeftPosition.getTopRow(), topLeftPosition.getLeftCol());
        } else {
            return null;
        }
    }

    private static List<Integer> generateChainCode(ImageMatrix<BinaryColor> imageMatrix,
                                                   BinaryColorType backgroundColor,
                                                   int objTopLeftRow,
                                                   int objTopLeftCol) {

        boolean image[][] = generateObjectOfInterestMatrix(imageMatrix, backgroundColor);

        // Trace object to generate chain code
        List<Integer> chainCode = new ArrayList<>();

        int width = imageMatrix.getWidth();
        int height = imageMatrix.getHeight();
        boolean footPrint[][] = new boolean[height + (2 * IMAGE_OFFSET)][width + (2 * IMAGE_OFFSET)]; // to know which cell we already passed

        int cursorRow = objTopLeftRow + IMAGE_OFFSET;
        int cursorCol = objTopLeftCol + IMAGE_OFFSET - 1;

        FreemanCodeEightDirection nextDirection = getNextAvailableMove(image, footPrint, cursorRow, cursorCol);
        while (nextDirection != null) {
            chainCode.add(nextDirection.getCode());
            cursorRow += nextDirection.getRowDirection();
            cursorCol += nextDirection.getColDirection();
            footPrint[cursorRow][cursorCol] = true;

            nextDirection = getNextAvailableMove(image, footPrint, cursorRow, cursorCol);
        }
        Log.i("[CHAIN_CODE_GENERATOR][GENERATE_CHAIN_CODE]", "Chain code: " + chainCode);
        return chainCode;
    }

    private static List<Integer> generateChainCode(boolean[][] image, int objTopLeftRow, int objTopLeftCol) {
        // Trace object to generate chain code
        List<Integer> chainCode = new ArrayList<>();
        int height = image.length;
        int width = image[0].length;
        boolean footPrint[][] = new boolean[height + (2 * IMAGE_OFFSET)][width + (2 * IMAGE_OFFSET)];

        int cursorRow = objTopLeftRow;
        int cursorCol = objTopLeftCol - 1;

        FreemanCodeEightDirection nextDirection = getNextAvailableMove(image, footPrint, cursorRow, cursorCol);
        while (nextDirection != null) {
            chainCode.add(nextDirection.getCode());
            cursorRow += nextDirection.getRowDirection();
            cursorCol += nextDirection.getColDirection();
            footPrint[cursorRow][cursorCol] = true;

            nextDirection = getNextAvailableMove(image, footPrint, cursorRow, cursorCol);
        }
        Log.i("[CHAIN_CODE_GENERATOR][GENERATE_CHAIN_CODE]", "Chain code: " + chainCode);
        return chainCode;
    }

    /**
     * getAllChainCode : untuk mendapatkan chain code / kode belok dari gambar dengan banyak angka
     * angka yang sudah didapatkan chain codenya akan dihapus
     * apabila nilai normalized true hasil yang dikembalikan adalah kode belok yang telah dihaluskan
     */
    public static List<List<Integer>> getAllChainCode(ImageMatrix<BinaryColor> imageMatrix,
                                                      BinaryColorType backgroundColor,
                                                      boolean normalized) {

        List<List<Integer>> allChainCode = new ArrayList<>();
        List<Integer> oneChain;

        boolean[][] image = generateObjectOfInterestMatrix(imageMatrix, backgroundColor);
        FindObjectTopLeftPositionResult topLeftPosition = getAnObjectTopLeftPosition(image, 0, 0);

        while (topLeftPosition.isFound()) {

            if (!normalized) {
                oneChain = generateChainCode(image, topLeftPosition.getTopRow(), topLeftPosition.getLeftCol());
            } else {
                //NOTE : buat plat mobil jelek kalo pake yg coarse
                //oneChain = generateCoarseFourDirectionTurnChainCode(image, topLeftPosition.getTopRow(), topLeftPosition.getLeftCol());
                oneChain = generateNormalizedChainCode(imageMatrix, backgroundColor);
            }

            allChainCode.add(oneChain);

            FloodFill.binaryFloodFill(image, topLeftPosition.getTopRow(), topLeftPosition.getLeftCol());

            topLeftPosition = getAnObjectTopLeftPosition(image, topLeftPosition.getTopRow(), topLeftPosition.getLeftCol());
        }

        return allChainCode;
    }


    /**
     * generateNormalizedChainCode : menghasilkan kode belok dari image matrix
     */
    public static List<Integer> generateNormalizedChainCode(ImageMatrix<BinaryColor> imageMatrix, BinaryColorType backgroundColor) {
        return normalizeEightDirectionChainCode(generateChainCode(imageMatrix, backgroundColor));
    }

    private static List<Integer> generateNormalizedChainCode(ImageMatrix<BinaryColor> imageMatrix,
                                                             BinaryColorType backgroundColor,
                                                             int objTopLeftRow,
                                                             int objTopLeftCol) {
        return normalizeEightDirectionChainCode(generateChainCode(imageMatrix, backgroundColor, objTopLeftRow, objTopLeftCol));
    }

    /**
     * normalized*ChainCode : menghasilkan kode belok dari chain code
     * dengan cara menghitung perbedaan dari dua chain code yang berdekatan
     */
    private static List<Integer> normalizeFourDirectionChainCode(List<Integer> chainCode) {
        if (chainCode == null) return null;

        int codeSize = 4;
        List<Integer> normalizedCC = new ArrayList<>();

        for (int i = 1; i < chainCode.size(); i++) {
            int diff = chainCode.get(i) - chainCode.get(i - 1);
            if (diff < 0) diff += codeSize;
            normalizedCC.add(diff);
        }

        Log.i("[CHAIN_CODE_GENERATOR][NORMALIZE_FOUR_DIRECTION_CHAIN_CODE]", "Normalized chain code: " + normalizedCC);
        return normalizedCC;
    }

    private static List<Integer> normalizeEightDirectionChainCode(List<Integer> chainCode) {
        if (chainCode == null) return null;

        int codeSize = FreemanCodeEightDirection.values().length;
        List<Integer> normalizedCC = new ArrayList<>();

        for (int i = 1; i < chainCode.size(); i++) {
            int diff = chainCode.get(i) - chainCode.get(i - 1);
            if (diff < 0) diff += codeSize;
            normalizedCC.add(diff);
        }

        // Last case
        if (chainCode.size() > 1) {
            int diff = chainCode.get(0) - chainCode.get(chainCode.size() - 1);
            if (diff < 0) diff += codeSize;
            normalizedCC.add(diff);
        }
        Log.i("[CHAIN_CODE_GENERATOR][NORMALIZE_EIGHT_DIRECTION_CHAIN_CODE]", "Normalized chain code: " + normalizedCC);
        return normalizedCC;
    }

    /**
     * generateCoarseFourDirectionTurnChainCode : mengubah chain code menjadi kode belok yang telah dihaluskan
     * caranya : chain code -> dinormalisasi -> dihaluskan -> dihapus nol nya
     */
    public static List<Integer> generateCoarseFourDirectionTurnChainCode(ImageMatrix<BinaryColor> imageMatrix,
                                                                         BinaryColorType backgroundColor) {
        FindObjectTopLeftPositionResult topLeftPosition = getAnObjectTopLeftPosition(imageMatrix, backgroundColor);
        if (topLeftPosition.isFound()) {
            return generateCoarseFourDirectionTurnChainCode(imageMatrix, backgroundColor,
                    topLeftPosition.getTopRow(), topLeftPosition.getLeftCol());
        } else {
            return null;
        }
    }

    private static List<Integer> generateCoarseFourDirectionTurnChainCode(ImageMatrix<BinaryColor> imageMatrix,
                                                                          BinaryColorType backgroundColor,
                                                                          int objTopLeftRow,
                                                                          int objTopLeftCol) {

        List<Integer> normalChainCode = generateChainCode(imageMatrix, backgroundColor,
                objTopLeftRow, objTopLeftCol);
        List<Integer> coarseFourDirectionChainCode = getCoarseFourDirectionChainCode(normalChainCode);
        List<Integer> normalizedCoarseFourDirectionChainCode = normalizeFourDirectionChainCode(coarseFourDirectionChainCode);
        return removeCodeZeroFromNormalizedChainCode(normalizedCoarseFourDirectionChainCode);
    }

    private static List<Integer> generateCoarseFourDirectionTurnChainCode(boolean[][] image,
                                                                          int objTopLeftRow,
                                                                          int objTopLeftCol) {
        List<Integer> normalChainCode = generateChainCode(image, objTopLeftRow, objTopLeftCol);
        List<Integer> coarseFourDirectionChainCode = getCoarseFourDirectionChainCode(normalChainCode);
        List<Integer> normalizedCoarseFourDirectionChainCode = normalizeFourDirectionChainCode(coarseFourDirectionChainCode);
        return removeCodeZeroFromNormalizedChainCode(normalizedCoarseFourDirectionChainCode);
    }

    /**
     * getCoarseFourDirectionChainCode : menghaluskan nilai chain code dengan cara menggabungkan
     * nilai dari tiga kode berdekatan menjadi satu kode
     */
    private static List<Integer> getCoarseFourDirectionChainCode(List<Integer> detailChainCode) {
        int newChainCodeLength = detailChainCode.size() / CHAIN_CODE_COARSE_CONST;
        List<Integer> coarseChainCode = new ArrayList<>();
        for (int i = 0; i < newChainCodeLength; i++) {
            int rowDirection = 0;
            int colDirection = 0;
            for (int j = 0; j < CHAIN_CODE_COARSE_CONST; j++) {
                int detailChainCodeIdx = i * CHAIN_CODE_COARSE_CONST + j;
                Integer code = detailChainCode.get(detailChainCodeIdx);
                FreemanCodeEightDirection freemanCode = FreemanCodeEightDirection.getFreemanCodeFromCode(code);
                rowDirection += freemanCode.getRowDirection();
                colDirection += freemanCode.getColDirection();
            }

            if (rowDirection != 0 || colDirection != 0) {
                double radAngle = Math.atan2((double) -rowDirection, (double) colDirection);
                double degreeAngle = radAngle * (180.0 / Math.PI);
                if (degreeAngle < 0) degreeAngle += 360.0;
                if (degreeAngle <= 45.0 || degreeAngle > 315.0) {
                    coarseChainCode.add(0);
                } else if (degreeAngle > 45.0 && degreeAngle <= 135.0) {
                    coarseChainCode.add(1);
                } else if (degreeAngle > 135.0 && degreeAngle <= 225.0) {
                    coarseChainCode.add(2);
                } else if (degreeAngle > 225.0 && degreeAngle <= 315.0) {
                    coarseChainCode.add(3);
                }
            }
        }
        Log.i("[CHAIN_CODE_GENERATOR][GET_COARSE_FOUR_DIRECTION_CHAIN_CODE]", "Chain code: " + coarseChainCode);
        return coarseChainCode;
    }

    /**
     * removeCodeZeroFormNormalizedChainCode : menghapus angka 0 pada kode belok (yang berarti arah lurus)
     */
    private static List<Integer> removeCodeZeroFromNormalizedChainCode(List<Integer> normalizedChainCode) {
        List<Integer> newChainCode = new ArrayList<>();
        for (int i = 0; i < normalizedChainCode.size(); i++) {
            if (normalizedChainCode.get(i) != 0) {
                newChainCode.add(normalizedChainCode.get(i));
            }
        }
        Log.i("[CHAIN_CODE_GENERATOR][REMOVE_CODE_ZERO]", "Removed code zero: " + newChainCode);
        return newChainCode;
    }

    private static boolean isBesideObject(boolean[][] image, int row, int col){
        return (image[row-1][col] || image[row+1][col] || image[row][col-1] || image[row][col+1]);
    }

    private static FreemanCodeEightDirection getNextAvailableMove(boolean[][] image,
                                                                  boolean[][] footPrint,
                                                                  final int row,
                                                                  final int col) {

        for (FreemanCodeEightDirection direction : FreemanCodeEightDirection.values()) {
            int nextRow = row + direction.getRowDirection();
            int nextCol = col + direction.getColDirection();
            if (!footPrint[nextRow][nextCol] && !image[nextRow][nextCol] && isBesideObject(image, nextRow, nextCol)) {
                return direction;
            }
        }

        return null;
    }

    private enum FreemanCodeEightDirection {
        C0(0, 0, 1),
        C1(1, -1, 1),
        C2(2, -1, 0),
        C3(3, -1, -1),
        C4(4, 0, -1),
        C5(5, 1, -1),
        C6(6, 1, 0),
        C7(7, 1, 1);

        private final int mRowDirection;
        private final int mColDirection;
        private final int mCode;

        private FreemanCodeEightDirection(int code, int rowDirection, int colDirection) {
            mRowDirection = rowDirection;
            mColDirection = colDirection;
            mCode = code;
        }

        public static FreemanCodeEightDirection getFreemanCodeFromCode(int code) {
            switch (code) {
                case 0:
                    return C0;
                case 1:
                    return C1;
                case 2:
                    return C2;
                case 3:
                    return C3;
                case 4:
                    return C4;
                case 5:
                    return C5;
                case 6:
                    return C6;
                case 7:
                    return C7;
                default:
                    return C0;
            }
        }

        public int getRowDirection() {
            return mRowDirection;
        }

        public int getColDirection() {
            return mColDirection;
        }

        public int getCode() {
            return mCode;
        }

    }


}

