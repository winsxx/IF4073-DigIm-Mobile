package id.ac.itb.digim.processor.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.color.GreyscaleColor;

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
            samples.get(idx).setCluster(curCluster);

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

    public ImageMatrix<GreyscaleColor> coloringCluster(List<Sample> samples, int height, int width) {
        System.out.println("Coloring cluster.....");

        ImageMatrix<GreyscaleColor> imageMatrix = new ImageMatrix<GreyscaleColor>
                                                                (GreyscaleColor.class, height, width);
        GreyscaleColor grey1 = new GreyscaleColor();
        grey1.setGrey(10);
        GreyscaleColor grey2 = new GreyscaleColor();
        grey2.setGrey(70);
        GreyscaleColor grey3 = new GreyscaleColor();
        grey3.setGrey(130);
        GreyscaleColor grey4 = new GreyscaleColor();
        grey4.setGrey(200);


        for(int i = 0; i< samples.size(); i++) {
            Sample curSample = samples.get(i);
            if (curSample.getCluster() == 0) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), grey1);
            } else if (curSample.getCluster() == 1) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), grey2);
            } else if (curSample.getCluster() == 2) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), grey3);
            } else if (curSample.getCluster() == 3) {
                imageMatrix.setPixel(curSample.getX(), curSample.getY(), grey4);
            }
        }
        return imageMatrix;
    }

    public static double distanceSample (Sample centroid, Sample sam) {
        return Math.sqrt(Math.pow((centroid.getY() - sam.getY()), 2) + Math.pow((centroid.getX() - sam.getX()), 2));
    }
}
