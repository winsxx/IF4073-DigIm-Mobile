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

import com.google.gson.Gson;

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

public class DeteksiAngkaActivity extends ActionBarActivity {
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
        setContentView(R.layout.activity_deteksi_angka);
        im = (ImageView) findViewById(R.id.imageView);
        mPrefs = getSharedPreferences("BASE_PICTURE", MODE_PRIVATE);
        list = new ArrayList<List<Double>>(10);
    }

    public void loadData() {
        String json;
        Gson gson = new Gson();
        for (int i = 0; i < 10; i++) {
            json = mPrefs.getString(Integer.toString(i), "");
            list.add(i, gson.fromJson(json, List.class));
        }
        Toast.makeText(this, "Load data selesai", Toast.LENGTH_SHORT).show();
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

                //NOTE : cek SelectBasePicture, background sama chain code yang dipake harus sama
                List<Integer> chainCode = ChainCodeGenerator.generateChainCode(mBinaryImageMatrix, BinaryColorType.WHITE);
                Log.d("[CHAIN_CODE]", chainCode.toString());
                int min = Integer.MAX_VALUE;
                int number = 0;
                int result;
                for(int i=0; i<10; i++){
                    if(list.get(i) != null) {
                        result = DiffChainCode.calcDiffDouble(chainCode, list.get(i));
                        if(result < min) {
                            min = result;
                            number = i;
                        }
                    }
                }

                TextView numberText = (TextView) findViewById(R.id.angkaText);
                numberText.setText("Angka " + Integer.toString(number));

                TextView chainText = (TextView) findViewById(R.id.chainCodeText);
                chainText.setText("Chain code " + chainCode);

            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex){
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chain_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_select_base_picture) {
            Intent i = new Intent(this, SelectBasePictureActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_load_data) {
            loadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
