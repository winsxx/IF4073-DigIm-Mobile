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

public class MerkMobilActivity extends ActionBarActivity {

    private static final int RESULT_LOAD_IMG = 1;
    ImageMatrix<GreyscaleColor> mGreyscaleImageMatrix;
    ImageMatrix<BinaryColor> mBinaryImageMatrix;
    Bitmap imageBitmap;
    ImageView im;
    SharedPreferences mPrefs;
    List<List<Integer>> database; //{0 : Terios; 1: Avanza// }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merk_mobil);
        im = (ImageView) findViewById(R.id.imageView);
        mPrefs = getSharedPreferences("BASE_PICTURE", MODE_PRIVATE);
        database = new ArrayList<List<Integer>>();
        database.add(getListTerios());
        database.add(getListAvanza());
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

                int index = Integer.MAX_VALUE;

                Log.d("[ALL_CHAIN_CODE_SIZE]", String.valueOf(allChainCode.size()));

                int score = Integer.MAX_VALUE;

                for (int idx = 0; idx < database.size(); idx++) {
                    int curScore = Integer.MAX_VALUE;
                    int distance;

                    System.out.println("Data ke - " + idx);

                    for (int i = 0; i<allChainCode.size(); i++) {
                        if (allChainCode.get(i).size() < 5) {
                            continue;
                        }

                        distance = DiffChainCode.editDistanceInt(allChainCode.get(i), database.get(idx));
                        System.out.println("Untuk chain code : " + allChainCode.get(i) + " distancenya : " + distance);

                        if (distance < curScore) {
                            curScore = distance;
                        }
                    }

                    if (curScore < score) {
                        score = curScore;
                        index = idx;
                    }
                }

                System.out.println("index akhir: " + index + ";score akhir: " + score);

                TextView numberText = (TextView) findViewById(R.id.textView2);
                numberText.setText("Merek mobil : " + getMerk(index));

            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merek_mobil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cek_kode_belok) {
            Intent i = new Intent(this, CekKodeBelokActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getMerk(int code) {
        String merk;
        switch (code) {
            case 0 : merk = "Terios";
                break;
            case 1 : merk = "Avanza";
                break;
            default: merk = "Tidak tahu";
        }
        return merk;
    }


    public List<Integer> getListTerios() {
        List<Integer> list = new ArrayList<>();
//        [3, 3, 1, 1, 3, 3, 3, 3, 1, 1, 1, 3, 3, 2, 3, 3]
        list.add(3);list.add(3);
        list.add(1);list.add(1);
        list.add(3);list.add(3);
        list.add(3);list.add(3);
        list.add(1);list.add(1);
        list.add(1);list.add(3);
        list.add(3);list.add(2);
        list.add(3);list.add(3);
        return list;
    }

    public List<Integer> getListAvanza() {
        List <Integer> list = new ArrayList<>();
//        [3, 1, 1, 3, 3, 1, 1, 3, 1, 3, 3, 3, 1, 3, 1, 3, 2, 1, 3, 3, 1, 1, 3, 3, 2, 3, 3, 1, 1, 3]
        list.add(3);list.add(1);
        list.add(1);list.add(3);
        list.add(3);list.add(1);
        list.add(1);list.add(3);
        list.add(1);list.add(3);
        list.add(3);list.add(3);
        list.add(1);list.add(3);
        list.add(1);list.add(3);
        list.add(2);list.add(1);
        list.add(3);list.add(3);
        list.add(1);list.add(1);
        list.add(3);list.add(3);
        list.add(2);list.add(3);
        list.add(3);list.add(1);
        list.add(1);list.add(3);
        return list;
    }

}
