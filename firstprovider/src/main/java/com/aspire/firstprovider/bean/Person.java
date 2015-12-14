package com.aspire.firstprovider.bean;

/**
 * Created by lijun on 2015/12/13.
 */
public class Person {
    /* should bigger than 0 */
    private int mId;
    private String mName;
    private int mAge;

    public int getmAge() {
        return mAge;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
