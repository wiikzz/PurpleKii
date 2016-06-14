package com.wiikzz.ikz.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wiikzz.ikz.R;
import com.wiikzz.ikz.ui.widget.TinyNumberPicker;
import com.wiikzz.library.ui.BaseActivity;

import java.text.Format;

public class MainActivity extends BaseActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
        loadViewData();
    }

    @Override
    protected void initVariables() {
        // nothing to do
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText("QAQ");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }


        TinyNumberPicker tinyNumberPicker = (TinyNumberPicker) findViewById(R.id.tinyNumberPicker);
        assert tinyNumberPicker != null;
        tinyNumberPicker.setMinValue(0);
        tinyNumberPicker.setMaxValue(5);
        tinyNumberPicker.setValue(2);

        tinyNumberPicker.setDisplayedValues(new String[] {"00", "10", "20", "30", "40", "50"});

        tinyNumberPicker.setOnValueChangedListener(new TinyNumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(TinyNumberPicker picker, int oldVal, int newVal) {
                mTextView.setText("Last:" + oldVal + ", Now:" + newVal);
            }
        });

    }

    @Override
    protected void loadViewData() {
        // nothing to do
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
