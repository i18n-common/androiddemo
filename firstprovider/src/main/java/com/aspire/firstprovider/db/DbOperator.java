package com.aspire.firstprovider.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.aspire.firstprovider.bean.Person;
import com.aspire.firstprovider.provider.CustomContract;
import com.aspire.firstprovider.toolbox.CommonLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijun on 2015/12/13.
 */
public class DbOperator {

    private static final Uri COMMON_URI = CustomContract.Person.CONTENT_URI;
    private Context mContext;
    private ContentResolver mContentResolver;

    public DbOperator(Context context) {
        this.mContext = context;
        mContentResolver = context.getContentResolver();
    }

    /**
     *
     * @param person
     * @return -1: insert failed; 0: insert succeed
     */
    public int onInsert(Person person) {
        // parameter validation check
        if (person.getmId() <= 0) {
            throw new IllegalArgumentException("Id of Person object should bigger than 0, Id: " + person.getmId());
        }
        if (null == person.getmName()) {
            throw new IllegalArgumentException("Name of Person object should not be null");
        }

        ContentValues values = new ContentValues();
        values.put(CustomContract.Person.COLUMN_ID, person.getmId());
        values.put(CustomContract.Person.COLUMN_NAME, person.getmName());
        // age=0: age is not initialized
        if (person.getmAge() != 0) {
            values.put(CustomContract.Person.COLUMN_AGE, person.getmAge());
        }
        Uri uri = mContentResolver.insert(COMMON_URI, values);
        return uri == null ? -1 : 0;
    }

    /**
     *
     * @param personList
     * @return the number of rows inserted successfully
     */
    public int onBulkInsert(List<Person> personList) {
        ArrayList<ContentValues> cvList = new ArrayList<ContentValues>();
        Person person;
        ContentValues values;
        for (int i = 0; i < personList.size(); i++) {
            person = personList.get(i);
            if (person.getmId() <=0 || null == person.getmName()) {
                continue;
            }
            values = new ContentValues();
            values.put(CustomContract.Person.COLUMN_ID, person.getmId());
            values.put(CustomContract.Person.COLUMN_NAME, person.getmName());
            if (person.getmAge() != 0) {
                values.put(CustomContract.Person.COLUMN_AGE, person.getmAge());
            }
            cvList.add(values);
        }
        return mContentResolver.bulkInsert(COMMON_URI, (ContentValues[])cvList.toArray());
    }

    public List<Person> onQuery(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        List<Person> personList = new ArrayList<Person>();
        Cursor cursor = mContentResolver.query(COMMON_URI, projection, selection, selectionArgs, sortOrder);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(CustomContract.Person.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CustomContract.Person.COLUMN_NAME));
                    int age = cursor.getInt(cursor.getColumnIndexOrThrow(CustomContract.Person.COLUMN_AGE));
                    Person person = new Person();
                    person.setmId(id);
                    person.setmName(name);
                    person.setmAge(age);
                    personList.add(person);
                } catch (IllegalArgumentException iae) {
                    CommonLog.e("%s", iae.toString());
                }
            }
        }
        return personList;
    }

    public List<Person> onQuery(String[] projection, String selection, String[] selectionArgs) {
        return onQuery(projection, selection, selectionArgs, null);
    }

    public List<Person> onQuery(String[] projection, String sortOrder) {
        return onQuery(projection, null, null, sortOrder);
    }

    public List<Person> onQuery(String[] projection) {
        return onQuery(projection, null, null);
    }

    public List<Person> onQuery() {
        return onQuery(null);
    }

    public int onUpdate(Person person, String where, String[] selectionArgs) {
        ContentValues values = new ContentValues();

        //mContentResolver.update(COMMON_URI, );
        return -1;
    }
}
