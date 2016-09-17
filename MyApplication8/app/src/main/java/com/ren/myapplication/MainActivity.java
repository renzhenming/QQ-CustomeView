package com.ren.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private QuickIndexView indexView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setContentView(new GooView(this));

       // initView();
    }

    private void initView() {
        indexView = (QuickIndexView) findViewById(R.id.indexview);
        listView = (ListView) findViewById(R.id.listview);
        indexView.setOnLetterUpdateListener(new QuickIndexView.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                Utils.showToast(getApplicationContext(),letter);

            }
        });
    }
}
