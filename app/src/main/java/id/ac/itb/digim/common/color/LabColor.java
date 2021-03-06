package id.ac.itb.digim.common.color;

import java.util.ArrayList;
import java.util.List;

public class LabColor {
    private static final double thresholdColor = 10;

    /**
     * Convert RGB to XYZ then XYZ to Lab
     */
    public static int[] rgb2lab(int R, int G, int B) {
        //http://www.brucelindbloom.com -> Math

        float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
        float Ls, as, bs;
        float eps = 216.f / 24389.f;
        float k = 24389.f / 27.f;

        float Xr = 0.964221f;  // reference white D50
        float Yr = 1.0f;
        float Zr = 0.825211f;

        // RGB to XYZ
        r = R / 255.f; //R 0..1
        g = G / 255.f; //G 0..1
        b = B / 255.f; //B 0..1

        // assuming sRGB (D65)
        if (r <= 0.04045)
            r = r / 12;
        else
            r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

        if (g <= 0.04045)
            g = g / 12;
        else
            g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

        if (b <= 0.04045)
            b = b / 12;
        else
            b = (float) Math.pow((b + 0.055) / 1.055, 2.4);


        X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
        Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
        Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

        // XYZ to Lab
        xr = X / Xr;
        yr = Y / Yr;
        zr = Z / Zr;

        if (xr > eps)
            fx = (float) Math.pow(xr, 1 / 3.);
        else
            fx = (float) ((k * xr + 16.) / 116.);

        if (yr > eps)
            fy = (float) Math.pow(yr, 1 / 3.);
        else
            fy = (float) ((k * yr + 16.) / 116.);

        if (zr > eps)
            fz = (float) Math.pow(zr, 1 / 3.);
        else
            fz = (float) ((k * zr + 16.) / 116);

        Ls = (116 * fy) - 16;
        as = 500 * (fx - fy);
        bs = 200 * (fy - fz);

        int[] lab = new int[3];
        lab[0] = (int) (2.55 * Ls + .5);
        lab[1] = (int) (as + .5);
        lab[2] = (int) (bs + .5);
        return lab;
    }

    /**
     * Computes the difference between two RGB colors by converting them to the L*a*b scale and
     * comparing them using the CIE76 algorithm { http://en.wikipedia.org/wiki/Color_difference#CIE76}
     */
    public static double getColorDifference(RgbColor a, RgbColor b) {
        int r1, g1, b1, r2, g2, b2;
        r1 = a.getRed();
        g1 = a.getGreen();
        b1 = a.getBlue();
        r2 = b.getRed();
        g2 = b.getGreen();
        b2 = b.getBlue();
        int[] lab1 = rgb2lab(r1, g1, b1);
        int[] lab2 = rgb2lab(r2, g2, b2);
        return Math.sqrt(Math.pow(lab2[0] - lab1[0], 2) + Math.pow(lab2[1] - lab1[1], 2) + Math.pow(lab2[2] - lab1[2], 2));
    }

    public static double getDifference (RgbColor a, RgbColor b) {
        return Math.sqrt(Math.pow(a.getRed()-b.getRed(), 2) + Math.pow(a.getGreen()-b.getGreen(), 2) + Math.pow(a.getBlue()-b.getBlue(), 2));
    }

    public static boolean isSkinColor (RgbColor color) {
        List<RgbColor> skinDatabase = new ArrayList<RgbColor>();
        //RgbColor skin1 = new RgbColor(124,109,98); skinDatabase.add(skin1); //2 baju yafi kena
        //RgbColor skin2 = new RgbColor(125,113,87); skinDatabase.add(skin2); //2 baju atia kena
        RgbColor skin3 = new RgbColor(84,71,60); skinDatabase.add(skin3); //1 aman tentram, 2 kena dikit
        //RgbColor skin4 = new RgbColor(108,90,68); skinDatabase.add(skin4); //1 banyak di tangan tp aman, 2 banyak tangan baju atia kena dikit
        //RgbColor skin5 = new RgbColor(104,91,85); skinDatabase.add(skin5); //1 baju pak iping kena, 2 baju yafi kena langit2 kena wajah engga
        RgbColor skin6 = new RgbColor(151, 120, 102); skinDatabase.add(skin6); //1 ga ngaruh :v 2 dikit tp di wajah semua


        boolean isSkin = false;
        int i = 0;
        while (!isSkin && i<skinDatabase.size()) {
            double dist = getDifference(color, skinDatabase.get(i));
            //System.out.println("dist : " + dist);
            if (dist < thresholdColor) {
                isSkin = true;
                break;
            } else {
                i++;
            }
        }
        return isSkin;
    }
}
