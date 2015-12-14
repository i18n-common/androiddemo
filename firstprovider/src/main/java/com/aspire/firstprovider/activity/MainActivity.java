package com.aspire.firstprovider.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aspire.firstprovider.R;
import com.aspire.firstprovider.provider.CustomContract;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private final ContentResolver mContentResolver = getContentResolver();
    private Button mCreateProvider;
    private Button mInsert;
    private Button mQuery;
    private Button mUpdate;
    private Button mDelete;
    private TextView mDbContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mCreateProvider = (Button) findViewById(R.id.create_provider);
        mCreateProvider.setOnClickListener(this);

        mInsert = (Button) findViewById(R.id.insert);
        mQuery = (Button) findViewById(R.id.query);
        mUpdate = (Button) findViewById(R.id.update);
        mDelete = (Button) findViewById(R.id.delete);
        mInsert.setOnClickListener(this);
        mQuery.setOnClickListener(this);
        mUpdate.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        mDbContent = (TextView) findViewById(R.id.db_content);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_provider:
                break;
            case R.id.insert:
                break;
            case R.id.query:
                break;
            case R.id.update:
                break;
            case R.id.delete:
                break;
            default:
                break;
        }

    }

    private Uri doInsert() {
        ContentValues values = new ContentValues();
        values.put(CustomContract.Person.COLUMN_ID, 1);
        values.put(CustomContract.Person.COLUMN_NAME, "peter");
        values.put(CustomContract.Person.COLUMN_AGE, 18);
        return mContentResolver.insert(CustomContract.Person.CONTENT_URI, values);
    }

    private void doQuery() {
        String[] projection = {CustomContract.Person.COLUMN_ID,
                CustomContract.Person.COLUMN_NAME, CustomContract.Person.COLUMN_AGE};
        mContentResolver.query(CustomContract.Person.CONTENT_URI, projection, null, null, null);

    }

    private void doUpdate() {

    }

    private void doDelete() {

    }
}
