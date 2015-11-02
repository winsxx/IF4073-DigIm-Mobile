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
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.converter.ImageConverter;
import id.ac.itb.digim.processor.edging.EdgeDetection;

public class EdgeDetectionActivity extends ActionBarActivity {
    private static final int RESULT_LOAD_IMG = 1;

    ImageView mInputImage;
    ImageView mResultImage;
    Bitmap mImageBitmap;
    ImageMatrix<GreyscaleColor> mGreyscaleColorImageMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edge_detection);
        mInputImage = (ImageView) findViewById(R.id.imageView);
        mResultImage = (ImageView) findViewById(R.id.resultImageView);
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

                mInputImage.setImageBitmap(mImageBitmap);
                mGreyscaleColorImageMatrix = ImageConverter.bitmapToGreyscaleMatrix(mImageBitmap);


            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void homogenEdgeDetection(View view) {
        ImageMatrix<GreyscaleColor> edgingResult = EdgeDetection.homogenEdging(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(edgingResult));
    }

    public void differenceEdgeDetection(View view) {
        ImageMatrix<GreyscaleColor> edgingResult = EdgeDetection.differenceEdging(mGreyscaleColorImageMatrix);
        mResultImage.setImageBitmap(ImageConverter.imageMatrixToBitmap(edgingResult));
    }
}
