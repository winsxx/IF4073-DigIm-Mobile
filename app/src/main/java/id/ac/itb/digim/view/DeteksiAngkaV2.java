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
import id.ac.itb.digim.common.converter.ImageScaling;
import id.ac.itb.digim.processor.smoother.MedianColorBlur;

public class DeteksiAngkaV2 extends ActionBarActivity {
    private static final int RESULT_LOAD_IMG = 1;
    ImageMatrix<GreyscaleColor> mGreyscaleImageMatrix;
    ImageMatrix<GreyscaleColor> mGreyscaleBlurredImageMatrix;
    ImageMatrix<BinaryColor> mBinaryImageMatrix;
    ImageMatrix<BinaryColor> mBinaryFiveSquareImageMatrix;
    Bitmap imageBitmap;
    List<List<Integer>> database;

    ImageView imageView;
    ImageView fiveSquareImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deteksi_angka_v2);
        imageView = (ImageView) findViewById(R.id.imageView);
        fiveSquareImageView = (ImageView) findViewById(R.id.fiveSquareImageView);
        database = new ArrayList<>();
        database.add(getKode0());
        database.add(getKode1());
        database.add(getKode2());
        database.add(getKode3());
        database.add(getKode4());
        database.add(getKode5());
        database.add(getKode6());
        database.add(getKode7());
        database.add(getKode8());
        database.add(getKode9());
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

                mGreyscaleBlurredImageMatrix = MedianColorBlur.medianColorBlur(mGreyscaleImageMatrix,2);

                mBinaryImageMatrix = ImageConverter.greyscaleToBinaryMatrix(mGreyscaleBlurredImageMatrix);

                imageView.setImageBitmap(ImageConverter.imageMatrixToBitmap(mBinaryImageMatrix));

                mBinaryFiveSquareImageMatrix = ImageConverter.binaryToSquareMatrix(mBinaryImageMatrix);
                fiveSquareImageView.setImageBitmap(ImageConverter.imageMatrixToBitmap(mBinaryFiveSquareImageMatrix));

                List<Integer> chainCode = ChainCodeGenerator.generateChainCode(mBinaryFiveSquareImageMatrix, BinaryColorType.WHITE);

                System.out.println("[CC] " + chainCode.toString());

                int score = Integer.MAX_VALUE;
                int min = Integer.MAX_VALUE;
                int num = Integer.MAX_VALUE;

                for (int i=0; i<database.size(); i++) {
                    if (database.get(i).size() > 0) {
                        score = DiffChainCode.editDistanceInt(chainCode, database.get(i));
                    }

                    if (min > score) {
                        min = score;
                        num = i;
                    }
                }

                TextView numberText = (TextView) findViewById(R.id.textView2);
                numberText.setText("Kode : " + chainCode.toString() + " angka: " + num);


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
        getMenuInflater().inflate(R.menu.menu_deteksi_angka_v2, menu);
        return true;
    }

    public List<Integer> getKode0() {
        //1, 0, 7, 6, 7, 5, 6, 5, 4, 3, 1, 2, 2, 3, 5, 6, 6
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        list.add(7);
        list.add(6);
        list.add(7);
        list.add(5);
        list.add(6);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(3);
        list.add(5);
        list.add(6);
        list.add(6);
        return list;
    }


    public List<Integer> getKode1() {
        //[1, 5, 6, 6, 6, 6, 7, 1, 2, 2, 2, 2]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(6);
        list.add(6);
        list.add(6);
        list.add(6);
        list.add(7);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(2);
        list.add(2);
        return list;
    }


    public List<Integer> getKode2() {
//        [1, 0, 7, 5, 4, 3]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        list.add(7);
        list.add(5);
        list.add(4);
        list.add(3);
        return list;
    }


    public List<Integer> getKode3() {
        //[1, 5, 7, 1, 7, 6, 6, 5, 3, 3]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(7);
        list.add(1);
        list.add(7);
        list.add(6);
        list.add(6);
        list.add(5);
        list.add(3);
        list.add(3);
        return list;
    }


    public List<Integer> getKode4() {
        List<Integer> list = new ArrayList<>();
        return list;
    }


    public List<Integer> getKode5() {
        //[1, 0, 7, 5, 7, 6, 6, 5, 4, 4, 3, 1, 0, 3, 3, 1]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        list.add(7);
        list.add(5);
        list.add(7);
        list.add(6);
        list.add(6);
        list.add(5);
        list.add(4);
        list.add(4);
        list.add(3);
        list.add(1);
        list.add(0);
        list.add(3);
        list.add(3);
        list.add(1);
        return list;
    }


    public List<Integer> getKode6() {
        //[1, 5, 5, 6, 6, 7, 1]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(5);
        list.add(6);
        list.add(6);
        list.add(7);
        list.add(1);
        return list;
    }


    public List<Integer> getKode7() {
        //[1, 0, 0, 7, 6, 6, 5, 5, 3, 2, 1, 4, 3]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        list.add(0);
        list.add(7);
        list.add(6);
        list.add(6);
        list.add(5);
        list.add(5);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(4);
        list.add(3);
        return list;
    }

    public List<Integer> getKode8() {
        //[1, 0, 0, 7, 6, 6, 7, 5, 5, 4, 4, 3, 2, 2, 2, 2]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        list.add(0);
        list.add(7);
        list.add(6);
        list.add(6);
        list.add(7);
        list.add(5);
        list.add(5);
        list.add(4);
        list.add(4);
        list.add(3);
        list.add(2);
        list.add(2);
        list.add(2);
        list.add(2);
        return list;
    }


    public List<Integer> getKode9() {
        //[1, 5, 5, 6, 7, 0, 5, 7, 1, 1, 2, 2, 3, 5]
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(0);
        list.add(5);
        list.add(7);
        list.add(1);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(3);
        list.add(5);
        return list;
    }


}
