package id.ac.itb.digim.view;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.ac.itb.digim.R;
import id.ac.itb.digim.analytics.boundary.ChainCodeGenerator;
import id.ac.itb.digim.analytics.distance.DiffChainCode;
import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.converter.ImageConverter;

public class CekKodeBelokActivity extends ActionBarActivity {

    private static final int RESULT_LOAD_IMG = 1;
    ImageMatrix<GreyscaleColor> mGreyscaleImageMatrix;
    ImageMatrix<BinaryColor> mBinaryImageMatrix;
    Bitmap imageBitmap;
    ImageView im;
    SharedPreferences mPrefs;
    List<List<Double>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_kode_belok);
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

                mGreyscaleImageMatrix = ImageConverter.bitmapToGreyscaleMatrix(imageBitmap);
                mBinaryImageMatrix = ImageConverter.greyscaleToBinaryMatrix(mGreyscaleImageMatrix);
                im.setImageBitmap(ImageConverter.imageMatrixToBitmap(mBinaryImageMatrix));


                List<List<Integer>> allChainCode = new ArrayList<List<Integer>>();
                allChainCode = ChainCodeGenerator.getAllChainCode(mBinaryImageMatrix, BinaryColorType.BLACK, true);

                String num = "";

                Log.d("[ALL_CHAIN_CODE_SIZE]", String.valueOf(allChainCode.size()));

                for (int i =0; i< allChainCode.size(); i++) {
                    num += allChainCode.get(i).toString() + "\n";
                }

                TextView numberText = (TextView) findViewById(R.id.belokText);
                numberText.setText("Kode belok :  " + num);

            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }



}
