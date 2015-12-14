package com.aspire.firstprovider.provider;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by lijun on 2015/12/10.
 */
public final class CustomContract {

    public static final String AUTHORITY = CustomContract.class.getPackage().getName() + ".provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Person {
        public static final String LOWER_CLASS_NAME = Person.class.getName().toLowerCase();
        public static final Uri CONTENT_URI = Uri.withAppendedPath(CustomContract.CONTENT_URI,
                LOWER_CLASS_NAME);

        // table name
        public static final String TABLE_NAME = "person";

        // column names
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGE = "age";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd." + CustomContract.AUTHORITY + "." + Person.class.getName().toLowerCase();

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd." + CustomContract.AUTHORITY + "." + Person.class.getName().toLowerCase();

        public static final String DEFAULT_SORT_ORDER = COLUMN_NAME + "ASC";
    }
}
