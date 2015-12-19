package id.ac.itb.digim.processor.equalizer;

public class LogarithmEqualizer {
    public static double[][] equalize(double[][] inputMatrix){
        int height = inputMatrix.length;
        int width = inputMatrix[0].length;
        double[][] outputMatrix = new double[height][width];

        // log (1+ |input[1,j]|)
        double max = 0;
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                outputMatrix[i][j] = Math.log( 1+Math.abs(inputMatrix[i][j]) );
                if (outputMatrix[i][j] > max) max = outputMatrix[i][j];
            }
        }

        // scaling constant
        double c = 255/max;
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                outputMatrix[i][j] = c * outputMatrix[i][j];
            }
        }

        return outputMatrix;
    }
}
