package id.ac.itb.digim.processor.smoother;

public class Discrete2dFourierTransform {

    public static void toFrequencyDomain(double[][] inputData, double[][] realOut,
                        double[][] imagOut, double[][] amplitudeOut) {

        int height = inputData.length;
        int width = inputData[0].length;

        // Two outer loops iterate on output data.
        for (int yWave = 0; yWave < height; yWave++) {
            for (int xWave = 0; xWave < width; xWave++) {
                // Two inner loops iterate on input data.
                for (int ySpace = 0; ySpace < height; ySpace++) {
                    for (int xSpace = 0; xSpace < width; xSpace++) {

                        // Compute real, imag, and ampltude.

                        realOut[yWave][xWave] += inputData[ySpace][xSpace] *
                                Math.cos(2 * Math.PI * ((1.0 * xWave * xSpace / width) +
                                        (1.0 * yWave * ySpace / height)));

                        imagOut[yWave][xWave] -= inputData[ySpace][xSpace] * Math
                                .sin(2 * Math.PI * ((1.0 * xWave * xSpace / width) + (1.0 * yWave * ySpace / height)));

                        amplitudeOut[yWave][xWave] = Math.sqrt(realOut[yWave][xWave] * realOut[yWave][xWave]
                                + imagOut[yWave][xWave] * imagOut[yWave][xWave]);
                    }
                }
            }
        }
    }

    public static void toTimeDomain(double[][] inputReal, double[][] inputImg, double[][] amplitudeOut){
        int height = inputReal.length;
        int width = inputReal[0].length;

        double[][] outputReal = new double[height][width];
        double[][] outputImg = new double[height][width];

        for(int ySpace=0; ySpace<height; ySpace++){
            for(int xSpace=0; xSpace<width; xSpace++){

                amplitudeOut[ySpace][xSpace] = 0;
                outputReal[ySpace][xSpace] = 0;
                outputImg[ySpace][xSpace] = 0;

                for(int yWave=0; yWave<height; yWave++){
                    for(int xWave=0; xWave<width; xWave++){
                        double angle = 2.0 * Math.PI * ((double)yWave*ySpace/height + (double)xWave*xSpace/width);
                        double cosAngle = Math.cos(angle);
                        double sinAngle = Math.sin(angle);
                        outputReal[ySpace][xSpace] +=
                                (inputReal[yWave][xWave]*cosAngle - inputImg[yWave][xWave]*sinAngle);
                        outputImg[ySpace][xSpace] +=
                                (inputImg[yWave][xWave]*cosAngle + inputReal[yWave][xWave]*sinAngle);
                    }
                }

                outputReal[ySpace][xSpace] /= (width * height);
                outputImg[ySpace][xSpace] /= (width * height);
                amplitudeOut[ySpace][xSpace] = Math.sqrt(outputReal[ySpace][xSpace]*outputReal[ySpace][xSpace] +
                        outputImg[ySpace][xSpace]*outputImg[ySpace][xSpace]);
            }
        }
    }

    public static double[][] lowPassFilter(double input[][], double cutoffFrac){
        int height = input.length;
        int width = input[0].length;
        double[][] output = new double[height][width];

        double highestFreq = Math.sqrt(height*height + width*width);
        double cutOff = highestFreq * cutoffFrac;

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(Math.sqrt(i*i + j*j) < cutOff) {
                    output[i][j] = input[i][j];
                } else {
                    output[i][j] = 0;
                }
            }
        }

        return output;
    }

    public static double[][] highPassFilter(double input[][], double cutoffFrac){
        int height = input.length;
        int width = input[0].length;
        double[][] output = new double[height][width];

        double highestFreq = Math.sqrt(height*height + width*width);
        double cutOff = highestFreq * cutoffFrac;

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(Math.sqrt(i*i + j*j) < cutOff) {
                    output[i][j] = 0;
                } else {
                    output[i][j] = input[i][j];
                }
            }
        }

        return output;
    }

}
