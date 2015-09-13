package id.ac.itb.digim.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URI;

import id.ac.itb.digim.R;
import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.converter.ImageConverter;


public class ChainCodeActivity extends ActionBarActivity {

    private static final int RESULT_LOAD_IMG = 1;
    ImageMatrix<GreyscaleColor> mGreyscaleImageMatrix;
    ImageMatrix<BinaryColor> mBinaryImageMatrix;
    Bitmap imageBitmap;
    ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain_code);
        im = (ImageView) findViewById(R.id.imageView);
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
                imageBitmap = BitmapFactory.decodeFile(imgDecodableString);
                Log.d("[Check]","width:"+imageBitmap.getWidth()+", height:"+imageBitmap.getHeight());
                mGreyscaleImageMatrix = ImageConverter.bitmapToGreyscaleMatrix(imageBitmap);
                mBinaryImageMatrix = ImageConverter.greyscaleToBinaryMatrix(mGreyscaleImageMatrix);
                im.setImageBitmap(ImageConverter.imageMatrixToBitmap(mBinaryImageMatrix));
            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex){
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
