package id.ac.itb.digim.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.ac.itb.digim.R;
import id.ac.itb.digim.processor.equalizer.GreyscaleCumulativeEqualizer;
import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.converter.ImageConverter;


public class MainActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MAX_VALUE_SEEKBAR = 100;

    String mCurrentPhotoPath;
    ImageView image;
    SeekBar seekBar;
    int progress;

    ImageMatrix<GreyscaleColor> mGreyscaleImageMatrix;
    GreyscaleCumulativeEqualizer greyscaleCumulativeEqualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.mCapturedImage);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(MAX_VALUE_SEEKBAR);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public void onCaptureImage(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mGreyscaleImageMatrix = ImageConverter.toGreyscaleMatrix(imageBitmap);
            imageBitmap = ImageConverter.toBitmap(mGreyscaleImageMatrix);
            image.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "DigIm_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
        progress = progressValue;
        float progressReal = (float)progress / MAX_VALUE_SEEKBAR;

        // for testing purpose MAX_VALUE_SEEKBAR
        if(greyscaleCumulativeEqualizer == null && mGreyscaleImageMatrix != null){
            greyscaleCumulativeEqualizer = new GreyscaleCumulativeEqualizer(mGreyscaleImageMatrix);
        }
        if(greyscaleCumulativeEqualizer != null){
            mGreyscaleImageMatrix = greyscaleCumulativeEqualizer.getScaledEqualizedImageMatrix(progressReal);
            image.setImageBitmap(ImageConverter.toBitmap(mGreyscaleImageMatrix));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
