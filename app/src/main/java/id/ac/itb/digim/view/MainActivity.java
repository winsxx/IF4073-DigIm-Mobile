package id.ac.itb.digim.view;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import id.ac.itb.digim.R;

public class MainActivity extends ActionBarActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toEqualization(View view) {
        Intent i = new Intent(this, EqualizeActivity.class);
        startActivity(i);
    }

    public void toChainCode(View view) {
        Intent i = new Intent(this, ChainCodeActivity.class);
        startActivity(i);
    }

}
