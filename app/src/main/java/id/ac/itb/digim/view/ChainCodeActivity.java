package id.ac.itb.digim.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
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
import id.ac.itb.digim.common.ImageMatrix;
import id.ac.itb.digim.common.color.BinaryColor;
import id.ac.itb.digim.common.color.BinaryColorType;
import id.ac.itb.digim.common.color.GreyscaleColor;
import id.ac.itb.digim.common.converter.ImageConverter;


public class ChainCodeActivity extends ActionBarActivity {

    private static final int RESULT_LOAD_IMG = 1;
    ImageMatrix<GreyscaleColor> mGreyscaleImageMatrix;
    ImageMatrix<BinaryColor> mBinaryImageMatrix;
    Bitmap imageBitmap;
    ImageView im;
    SharedPreferences mPrefs;
    List<List<Double>> list;

    public static int minDistance(List<Integer> word1, List<Double> word2) {
        int len1 = word1.size();
        int len2 = word2.size();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            int c1 = word1.get(i);
            for (int j = 0; j < len2; j++) {
                int c2 = word2.get(j).intValue();

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain_code);
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


                List<List<Integer>> allChainCode = new ArrayList<List<Integer>>();
                allChainCode = ChainCodeGenerator.getAllChainCode(mBinaryImageMatrix, BinaryColorType.BLACK, true);

                String num = "";

                Log.d("[ALL_CHAIN_CODE_SIZE]", String.valueOf(allChainCode.size()));

                for (int idx = 0; idx < allChainCode.size(); idx++) {
                    if (allChainCode.get(idx).size() < 100) {
                        continue;
                    }
                    System.out.println("CC: [" + allChainCode.get(idx).size() + "] " + allChainCode.get(idx).toString());
                    int min = Integer.MAX_VALUE;
                    int number = 0;
                    int result;
                    for (int i = 0; i < 10; i++) {
                        if (list.get(i) != null) {
                            result = calcDiffChainCode(allChainCode.get(idx), list.get(i));
                            if (result < min) {
                                min = result;
                                number = i;
                            }
                        }
                    }

                    if (min < 25) {
                        System.out.println("Hasil tebakan num " + String.valueOf(number) + " min " + min);
                        num += String.valueOf(number) + " ";
                    }
                }

                TextView numberText = (TextView) findViewById(R.id.textView2);
                numberText.setText("Gambar diatas adalah angka " + num);

            } else {
                Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public int calcDiffChainCode(List<Integer> list1, List<Double> list2) {
        int[] listCount1 = new int[8];
        for(int i=0; i<list1.size(); i++) {
            int code = list1.get(i).intValue();
            listCount1[code]++;
        }

        int[] listCount2 = new int[8];
        for(int i=0; i<list2.size(); i++) {
            int code = list2.get(i).intValue();
            listCount2[code]++;
        }

        int result = 0;
        for(int i=0; i<8; i++) {
            //result += Math.abs(Math.log(listCount1[i]+1) - Math.log(listCount2[i]+1));
            result += Math.abs(listCount1[i] - listCount2[i]);
        }
        return result;
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
