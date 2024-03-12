package com.skywalkers.foodassistant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class DefaultniActivity extends AppCompatActivity {







    // TODO: Izmeniti nazive izgenerisanih komponenti i dodeliti im imena prema funkcijama ili stvarima koje obavljaju
    // Takodje, dodati isplanirane stvari

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_pocetna);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_ostalo);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_statistika);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defaultni);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
