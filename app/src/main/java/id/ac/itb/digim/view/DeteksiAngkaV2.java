package id.ac.itb.digim.view;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import id.ac.itb.digim.R;

public class DeteksiAngkaV2 extends ActionBarActivity {

    ImageView image;
    ImageView fiveSquareImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deteksi_angka_v2);
        image = (ImageView) findViewById(R.id.bitmapView);
        image = (ImageView) findViewById(R.id.fiveSquareImageView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deteksi_angka_v2, menu);
        return true;
    }


}
