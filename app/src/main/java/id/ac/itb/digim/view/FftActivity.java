package id.ac.itb.digim.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import id.ac.itb.digim.R;
import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.converter.ImageConverter;
import id.ac.itb.digim.processor.equalizer.LogarithmEqualizer;
import id.ac.itb.digim.processor.smoother.Discrete2dFourierTransform;

public class FftActivity extends ActionBarActivity {
    private static final int RESULT_LOAD_IMG = 1;

    private ImageView originalImage;
    private ImageView fftFrequencyImage;
    private ImageView fftFilteredImageLow;
    private ImageView fftFilteredImageHigh;
    private Bitmap originalImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fft);

        originalImage = (ImageView) findViewById(R.id.imageView);
        fftFrequencyImage = (ImageView) findViewById(R.id.fftFrequencyImage);
        fftFilteredImageLow = (ImageView) findViewById(R.id.fftFilteredImageLow);
        fftFilteredImageHigh = (ImageView) findViewById(R.id.fftFilteredImageHigh);
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

                originalImageBitmap = BitmapFactory.decodeFile(imgDecodableString);
                originalImage.setImageBitmap(originalImageBitmap);

                ImageMatrix<GreyscaleColor> greyscaleImageMatrix = ImageConverter.bitmapToGreyscaleMatrix(originalImageBitmap);
                // Do transformation to frequency domain
                double[][] inputMatrix = ImageConverter.greyscaleMatrixToDoubleMatrix(greyscaleImageMatrix);
                double[][] imaginerMatrix = new double[greyscaleImageMatrix.getHeight()][greyscaleImageMatrix.getWidth()];
                double[][] realMatrix  = new double[greyscaleImageMatrix.getHeight()][greyscaleImageMatrix.getWidth()];
                double[][] amplitudeMatrix = new double[greyscaleImageMatrix.getHeight()][greyscaleImageMatrix.getWidth()];

                // To frequency domain
                Discrete2dFourierTransform.toFrequencyDomain(inputMatrix,
                        realMatrix, imaginerMatrix, amplitudeMatrix);
                amplitudeMatrix = LogarithmEqualizer.equalize(amplitudeMatrix);
                ImageMatrix<GreyscaleColor> fftAmplitude = ImageConverter.doubleMatrixToGreyscaleMatrix(amplitudeMatrix);
                Bitmap fftAmplitudeBitmap = ImageConverter.imageMatrixToBitmap(fftAmplitude);
                fftFrequencyImage.setImageBitmap(fftAmplitudeBitmap);

                // Low Pass
                double[][] passRealMatrix = Discrete2dFourierTransform.lowPassFilter(realMatrix, 4.0/5.0);
                double[][] passImgMatrix = Discrete2dFourierTransform.lowPassFilter(imaginerMatrix, 4.0/5.0);
                // To time domain
                Discrete2dFourierTransform.toTimeDomain(passRealMatrix, passImgMatrix, amplitudeMatrix);
                greyscaleImageMatrix = ImageConverter.doubleMatrixToGreyscaleMatrix(amplitudeMatrix);
                Bitmap filteredBitmap = ImageConverter.imageMatrixToBitmap(greyscaleImageMatrix);
                fftFilteredImageLow.setImageBitmap(filteredBitmap);

                // High Pass
                passRealMatrix = Discrete2dFourierTransform.highPassFilter(realMatrix, 4.0/5.0);
                passImgMatrix = Discrete2dFourierTransform.highPassFilter(imaginerMatrix, 4.0/5.0);
                // To time domain
                Discrete2dFourierTransform.toTimeDomain(passRealMatrix, passImgMatrix, amplitudeMatrix);
                greyscaleImageMatrix = ImageConverter.doubleMatrixToGreyscaleMatrix(amplitudeMatrix);
                filteredBitmap = ImageConverter.imageMatrixToBitmap(greyscaleImageMatrix);
                fftFilteredImageHigh.setImageBitmap(filteredBitmap);

            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
