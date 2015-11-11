package id.ac.itb.digim.processor.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.color.RgbColor;
import id.ac.itb.digim.common.color.RgbaColor;

public class KMeansCluster {
    public List<Sample> centroids;
    public List<Sample> samples;

    public KMeansCluster(int numCluster, ImageMatrix<BinaryColor> input) {
        System.out.println("Intialize sample... ");
        centroids = new ArrayList<Sample>(numCluster);
        samples = new ArrayList<Sample>();

        //Init sample from imagematrix
        int numSamples = 0;
        for (int i = 0; i<input.getHeight(); i++) {
            for (int j = 0; j<input.getWidth(); j++) {
                if (input.getPixel(i,j).getBinaryColor() == BinaryColorType.WHITE) {
                    Sample sam = new Sample(i,j);
                    samples.add(sam);
                    numSamples++;
                }
            }
        }
        Random rand = new Random();
        int randIdx;
        for (int i = 0; i < numCluster; i++) {
            randIdx = rand.nextInt(numSamples);
            Sample centroid1 = samples.get(randIdx);
            centroids.add(centroid1);
        }

//        Sample seed1 = new Sample(90, 125);
//        centroids.add(seed1);
//        Sample seed2 = new Sample(160, 125);
//        centroids.add(seed2);
//        Sample seed3 = new Sample(125, 165);
//        centroids.add(seed3);
//        Sample seed4 = new Sample(125, 200);
//        centroids.add(seed4);

    }

    public List<Sample> performClustering() {
        System.out.println("Perform clustering");
        double min;
        int curCluster = 0;
        boolean moving = true;

        //assign sample ke centroid
        for(int idx=0; idx<samples.size(); idx++) {
            min = Integer.MAX_VALUE;
            for(int c=0; c<centroids.size(); c++) {
                if(distanceSample(samples.get(idx),centroids.get(c)) < min) {
                    min = distanceSample(samples.get(idx),centroids.get(c));
                    curCluster = c;
                }
            }
//            if (min < 5) {
//                samples.get(idx).setCluster(curCluster);
//            }

            //calculate new centroid
            for(int i = 0; i<centroids.size();i++) {
                int totalX = 0;
                int totalY = 0;
                int totalInCluster = 0;
                for(int j = 0; j < samples.size(); j++)
                {
                    if(samples.get(j).getCluster() == i){
                        totalX += samples.get(j).getX();
                        totalY += samples.get(j).getY();
                        totalInCluster++;
                    }
                }
                if(totalInCluster > 0){
                    centroids.get(i).setX(totalX / totalInCluster);
                    centroids.get(i).setY(totalY / totalInCluster);
                }
            }
        }

        while(moving) {
            //calculate new centroid
            for(int i = 0; i<centroids.size();i++) {
                int totalX = 0;
                int totalY = 0;
                int totalInCluster = 0;
                for(int j = 0; j < samples.size(); j++)
                {
                    if(samples.get(j).getCluster() == i){
                        totalX += samples.get(j).getX();
                        totalY += samples.get(j).getY();
                        totalInCluster++;
                    }
                }
                if(totalInCluster > 0){
                    centroids.get(i).setX(totalX / totalInCluster);
                    centroids.get(i).setY(totalY / totalInCluster);
                }
            }

            //assign data to new centroid
            moving = false;
            for(int i = 0; i<samples.size();i++) {
                Sample tempSample = samples.get(i);
                min = Integer.MAX_VALUE;
                for(int c=0; c<centroids.size(); c++) {
                    if(distanceSample(samples.get(i),centroids.get(c)) < min) {
                        min = distanceSample(samples.get(i),centroids.get(c));
                        curCluster = c;
                    }
                }
                samples.get(i).setCluster(curCluster);
                if(tempSample.getCluster() != curCluster) {
                    tempSample.setCluster(curCluster);
                    moving = true;
                }
            }
        }

        return samples;
    }

    public ImageMatrix<RgbaColor> coloringCluster(List<Sample> samples, int height, int width) {
        System.out.println("Coloring cluster.....");

        ImageMatrix<RgbaColor> imageMatrix = new ImageMatrix<RgbaColor>
                                                (RgbaColor.class, height, width);
        RgbaColor red = new RgbaColor();
        red.setRed(255);
        red.setGreen(0);
        red.setBlue(0);
        red.setAlpha(255);
        RgbaColor green = new RgbaColor();
        green.setRed(0);
        green.setGreen(255);
        green.setBlue(0);
        green.setAlpha(255);
        RgbaColor blue = new RgbaColor();
        blue.setRed(0);
        blue.setGreen(0);
        blue.setBlue(255);
        blue.setAlpha(255);
        RgbaColor yellow = new RgbaColor();
        yellow.setRed(255);
        yellow.setGreen(255);
        yellow.setBlue(0);
        yellow.setAlpha(255);


        for(int i = 0; i< samples.size(); i++) {
            Sample curSample = samples.get(i);
            if (curSample.getCluster() == 0) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), red);
            } else if (curSample.getCluster() == 1) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), green);
            } else if (curSample.getCluster() == 2) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), blue);
            } else if (curSample.getCluster() == 3) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), yellow);
            }
        }
        return imageMatrix;
    }

    public static double distanceSample (Sample centroid, Sample sam) {
        return Math.sqrt(Math.pow((centroid.getY() - sam.getY()), 2) + Math.pow((centroid.getX() - sam.getX()), 2));
    }
}
