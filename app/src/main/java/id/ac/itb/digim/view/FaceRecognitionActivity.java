package id.ac.itb.digim.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import id.ac.itb.digim.R;
import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.color.LabColor;
import id.ac.itb.digim.common.color.RgbColor;
import id.ac.itb.digim.common.color.RgbaColor;
import id.ac.itb.digim.common.converter.ImageConverter;
import id.ac.itb.digim.common.converter.ImageScaling;
import id.ac.itb.digim.processor.clustering.KMeansCluster;
import id.ac.itb.digim.processor.edging.EdgeDetection;
import id.ac.itb.digim.processor.edging.convolution.ConvolutionKernel;
import id.ac.itb.digim.processor.edging.convolution.KirschConvolutionKernel;
import id.ac.itb.digim.processor.edging.convolution.RobertConvolutionKernel;
import id.ac.itb.digim.processor.edging.convolution.RobinsonConvolutionKernel;
import id.ac.itb.digim.processor.edging.convolution.SobelConvolutionKernel;
import id.ac.itb.digim.processor.smoother.GaussianBlur;

public class FaceRecognitionActivity extends ActionBarActivity {
    private static final int RESULT_LOAD_IMG = 1;

    ImageView mInputImage;
    ImageView mResultImage;
    ImageView mClusterImage;
    Bitmap mImageBitmap;
    ImageMatrix<GreyscaleColor> mGreyscaleColorImageMatrix;
    ImageMatrix<BinaryColor> mBinaryColorMatrix;
    ImageMatrix<GreyscaleColor> mEdgingImageMatrix;
    ImageMatrix<RgbaColor> mClusterImageMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        mInputImage = (ImageView) findViewById(R.id.imageView);
        mResultImage = (ImageView) findViewById(R.id.resultImageView);
        mClusterImage = (ImageView) findViewById(R.id.clusterImageView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edge_detection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void browseGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK &&
                    data != null) {
                Uri dataResult = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(dataResult, filePath, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                mImageBitmap = BitmapFactory.decodeFile(imgDecodableString);

                mGreyscaleColorImageMatrix = ImageConverter.bitmapToGreyscaleMatrix(mImageBitmap);
                mInputImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mGreyscaleColorImageMatrix));


            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void homogenEdgeDetection(View view) {
        System.out.println("Edging start... homogen...");
        ImageMatrix<GreyscaleColor> edgingResult = EdgeDetection.homogenEdging(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(edgingResult));
    }

    public void differenceEdgeDetection(View view) {
        System.out.println("Edging start... difference...");
        ImageMatrix<GreyscaleColor> edgingResult = EdgeDetection.differenceEdging(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(edgingResult));
    }

    public void sobelEdge(View view) {
        SobelConvolutionKernel kernel = new SobelConvolutionKernel();
        mEdgingImageMatrix = kernel.convolve(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mEdgingImageMatrix));
        System.out.println("Edging done");

        mBinaryColorMatrix = ImageConverter.greyscaleToBinaryMatrix(mEdgingImageMatrix);
        System.out.println("Convert to binary done");

        KMeansCluster kmeans = new KMeansCluster(4, mBinaryColorMatrix);
        mClusterImageMatrix = kmeans.coloringCluster(kmeans.performClustering(),
                mBinaryColorMatrix.getHeight(), mBinaryColorMatrix.getWidth());
        mClusterImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mClusterImageMatrix));
    }

    public void robertEdge (View view) {
        RobertConvolutionKernel kernel = new RobertConvolutionKernel();
        mEdgingImageMatrix = kernel.convolve(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mEdgingImageMatrix));
        System.out.println("Edging done");

        mBinaryColorMatrix = ImageConverter.greyscaleToBinaryMatrix(mEdgingImageMatrix);
        System.out.println("Convert to binary done");

        KMeansCluster kmeans = new KMeansCluster(4, mBinaryColorMatrix);
        mClusterImageMatrix = kmeans.coloringCluster(kmeans.performClustering(),
                mBinaryColorMatrix.getHeight(), mBinaryColorMatrix.getWidth());
        mClusterImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mClusterImageMatrix));
    }

    public void robinsonEdge (View view) {
        RobinsonConvolutionKernel kernel = new RobinsonConvolutionKernel();
        mEdgingImageMatrix = kernel.convolve(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mEdgingImageMatrix));
        System.out.println("Edging done");

        mBinaryColorMatrix = ImageConverter.greyscaleToBinaryMatrix(mEdgingImageMatrix);
        System.out.println("Convert to binary done");

        KMeansCluster kmeans = new KMeansCluster(4, mBinaryColorMatrix);
        mClusterImageMatrix = kmeans.coloringCluster(kmeans.performClustering(),
                mBinaryColorMatrix.getHeight(), mBinaryColorMatrix.getWidth());
        mClusterImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mClusterImageMatrix));
    }

    public void kirschEdge (View view) {
        KirschConvolutionKernel kernel = new KirschConvolutionKernel();
        mEdgingImageMatrix = kernel.convolve(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mEdgingImageMatrix));
        System.out.println("Edging done");

        mBinaryColorMatrix = ImageConverter.greyscaleToBinaryMatrix(mEdgingImageMatrix);
        System.out.println("Convert to binary done");

        KMeansCluster kmeans = new KMeansCluster(4, mBinaryColorMatrix);
        mClusterImageMatrix = kmeans.coloringCluster(kmeans.performClustering(),
                mBinaryColorMatrix.getHeight(), mBinaryColorMatrix.getWidth());
        mClusterImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(mClusterImageMatrix));
    }

    public void faceDetection(View view) {
        // TODO implement
        // Resize image width to 300px
        double resizeRatio = 500.0/mGreyscaleColorImageMatrix.getWidth();
        ImageMatrix<GreyscaleColor> processedMatrix =
                ImageScaling.bilinearResize(mGreyscaleColorImageMatrix,
                        (int) (resizeRatio * mGreyscaleColorImageMatrix.getWidth()),
                        (int) (resizeRatio * mGreyscaleColorImageMatrix.getHeight()));

        // Smoothing with gaussian blur
        processedMatrix = GaussianBlur.gaussBlur(processedMatrix, 2);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(processedMatrix));

        // Edge detection
        //ConvolutionKernel kernel = new SobelConvolutionKernel();
        //mEdgingImageMatrix = kernel.convolve(processedMatrix);

        // Edge to binary
        //mBinaryColorMatrix = ImageConverter.greyscaleToBinaryMatrix(mEdgingImageMatrix);

        ImageMatrix<RgbColor> rgbColorImageMatrix = ImageConverter.bitmapToRgbMatrix(mImageBitmap);
        for(int i=0; i<rgbColorImageMatrix.getHeight(); i++){
            for(int j=0; j<rgbColorImageMatrix.getWidth(); j++){
                RgbColor pixel = rgbColorImageMatrix.getPixel(i, j);
                if(LabColor.isSkinColor(pixel)){
                    rgbColorImageMatrix.setPixel(i, j, new RgbColor(0,255,0));
                }
            }
        }
        mClusterImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(rgbColorImageMatrix));

    }
}
