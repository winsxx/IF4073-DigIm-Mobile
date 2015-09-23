//package id.ac.itb.digim.analytics.blurring;
//
//import java.util.ArrayList;
//
//import id.ac.itb.digim.common.ImageMatrix;
//import id.ac.itb.digim.common.color.BinaryColor;
//
//public class GaussianBlur {
//
//    public static ImageMatrix<BinaryColor> gaussBlur_4(ImageMatrix<BinaryColor> imageMatrix,int width,int height,int radius) {
//        int[] bxs = boxesForGauss(radius, 3);
//
//        ImageMatrix<BinaryColor> target = new ImageMatrix<BinaryColor>(imageMatrix.getHeight(),imageMatrix.getWidth());
//
//        boxBlur_4 (imageMatrix, target, width, height, (bxs[0]-1)/2);
//        boxBlur_4 (target, imageMatrix, width, height, (bxs[1]-1)/2);
//        boxBlur_4 (imageMatrix, target, width, height, (bxs[2]-1)/2);
//    }
//
//    private static ImageMatrix<BinaryColor> boxBlur_4 (ImageMatrix<BinaryColor> scl, tcl, int w, int h, int r) {
//        for(int i=0; i<scl.length; i++) tcl[i] = scl[i];
//            boxBlurH_4(tcl, scl, w, h, r);
//        boxBlurT_4(scl, tcl, w, h, r);
//    }
//    function boxBlurH_4 (scl, tcl, w, h, r) {
//        var iarr = 1 / (r+r+1);
//        for(var i=0; i<h; i++) {
//            var ti = i*w, li = ti, ri = ti+r;
//            var fv = scl[ti], lv = scl[ti+w-1], val = (r+1)*fv;
//            for(var j=0; j<r; j++) val += scl[ti+j];
//            for(var j=0  ; j<=r ; j++) { val += scl[ri++] - fv       ;   tcl[ti++] = Math.round(val*iarr); }
//            for(var j=r+1; j<w-r; j++) { val += scl[ri++] - scl[li++];   tcl[ti++] = Math.round(val*iarr); }
//            for(var j=w-r; j<w  ; j++) { val += lv        - scl[li++];   tcl[ti++] = Math.round(val*iarr); }
//        }
//    }
//    function boxBlurT_4 (scl, tcl, w, h, r) {
//        var iarr = 1 / (r+r+1);
//        for(var i=0; i<w; i++) {
//            var ti = i, li = ti, ri = ti+r*w;
//            var fv = scl[ti], lv = scl[ti+w*(h-1)], val = (r+1)*fv;
//            for(var j=0; j<r; j++) val += scl[ti+j*w];
//            for(var j=0  ; j<=r ; j++) { val += scl[ri] - fv     ;  tcl[ti] = Math.round(val*iarr);  ri+=w; ti+=w; }
//            for(var j=r+1; j<h-r; j++) { val += scl[ri] - scl[li];  tcl[ti] = Math.round(val*iarr);  li+=w; ri+=w; ti+=w; }
//            for(var j=h-r; j<h  ; j++) { val += lv      - scl[li];  tcl[ti] = Math.round(val*iarr);  li+=w; ti+=w; }
//         }
//     }
//
//    private static int[] boxesForGauss(int sigma, int n)  // standard deviation, number of boxes
//    {
//        int wIdeal = (int) Math.sqrt((12*sigma*sigma/n)+1);  // Ideal averaging filter width
//        int wl = (int) Math.floor(wIdeal);  if(wl%2==0) wl--;
//        int wu = wl+2;
//
//        int mIdeal = (12*sigma*sigma - n*wl*wl - 4*n*wl - 3*n)/(-4*wl - 4);
//
//        int[] sizes = new int[n];
//        for(int i=0; i<n; i++) {
//            sizes[n] = (i<mIdeal?wl:wu);
//        }
//        return sizes;
//    }
//}
