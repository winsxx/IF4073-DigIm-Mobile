package id.ac.itb.digim.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import id.ac.itb.digim.processor.skeletonizer.ZhangSuenBinaryThinner;

public class ZhangSuenActivity extends ActionBarActivity {
    private static final int RESULT_LOAD_IMG = 1;
    ImageMatrix<GreyscaleColor> mGreyscaleImageMatrix;
    ImageMatrix<GreyscaleColor> mGreyscaleResizeImageMatrix;
    ImageMatrix<BinaryColor> mBinaryImageMatrix;
    ImageMatrix<BinaryColor> mBinarySkeletonMatrix;
    Bitmap imageBitmap;
    List<List<Integer>> database;

    ImageView imageView;
    ImageView skeletonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhang_suen);
        imageView = (ImageView) findViewById(R.id.imageView);
        skeletonView = (ImageView) findViewById(R.id.skeletonView);
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
                //TODO : add median blur
//                mGreyscaleResizeImageMatrix = ImageScaling.bilinearResize(mGreyscaleImageMatrix, 5, 5);
                mBinaryImageMatrix = ImageConverter.greyscaleToBinaryMatrix(mGreyscaleImageMatrix);
                imageView.setImageBitmap(ImageConverter.imageMatrixToBitmap(mBinaryImageMatrix));

                mBinarySkeletonMatrix = ZhangSuenBinaryThinner.imageThinning(mBinaryImageMatrix, BinaryColorType.WHITE);
                skeletonView.setImageBitmap(ImageConverter.imageMatrixToBitmap(mBinarySkeletonMatrix));
//
//                List<Integer> chainCode = new ArrayList<>();
//                chainCode = ChainCodeGenerator.generateCoarseFourDirectionTurnChainCode(mBinaryFiveSquareImageMatrix, BinaryColorType.WHITE);
//
//                System.out.println("[CC] " + chainCode.toString());
//
//                int score = Integer.MAX_VALUE;
//                int min = Integer.MAX_VALUE;
//                int num = Integer.MAX_VALUE;
//
//                for (int i=0; i<database.size(); i++) {
//                    if (database.get(i).size() > 0) {
//                        score = DiffChainCode.editDistanceInt(chainCode, database.get(i));
//                    }
//
//                    if (min > score) {
//                        min = score;
//                        num = i;
//                    }
//                }
//
//
//                TextView numberText = (TextView) findViewById(R.id.textView2);
//                numberText.setText("Kode : " + chainCode.toString() + " angka: " + num);


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
        getMenuInflater().inflate(R.menu.menu_zhang_suen, menu);
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
}
