package com.aspire.androiddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String BROADCAST_WRITE_TO_FILE = "com.aspire.write_to_file";
    public static final int REFRESH_CONTENT_VIEW = 0x1001;

    public static final String SDCARD_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView mFileContent;
    private static final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Handler sHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }

            switch (msg.what) {
                case REFRESH_CONTENT_VIEW:
                    String content = (String)msg.obj;
                    activity.refreshContentView(content);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFileContent = (TextView) findViewById(R.id.myTextView);
        registerReceiver();
        WriteToFileReceiver.getInstance().setHandler(sHandler);

//        File file = createFileOnSDCard("test.txt");
//        resetFile(file);
//        writeToFile(file, "this is a test text");
//        //writeToFile(file, "this is second line");
//        //writeToFile(file, "this is third line");
//        mFileContent.setText(readFromFile(file));
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BROADCAST_WRITE_TO_FILE);
        registerReceiver(WriteToFileReceiver.getInstance(), filter);
    }

    private static class WriteToFileReceiver extends BroadcastReceiver{

        private static final WriteToFileReceiver sReceiver = new WriteToFileReceiver();

        private Handler mHandler;

        private WriteToFileReceiver() {}

        public static WriteToFileReceiver getInstance() {
            return sReceiver;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    final File outFile = createFileOnSDCard("test.txt");
                    File inFile = new File(SDCARD_ROOT_PATH + File.separator + "file1.txt");
                    writeToFile(outFile, inFile);
                    String content = readFromFile(outFile);
                    if (null != mHandler) {
                        Message msg = mHandler.obtainMessage(REFRESH_CONTENT_VIEW, content);
                        msg.sendToTarget();
                    } else {
                        Log.e(TAG, "mHandler is null, must assign value to it!");
                    }
                }
            });
        }

        protected void setHandler(Handler handler) {
            this.mHandler = handler;
        }
    }

    private void refreshContentView (String newContent) {
        if (null != mFileContent) {
            mFileContent.setText(newContent);
        }
    }

    private static synchronized File createFileOnSDCard(String fileName) {
        Log.e(TAG, "sdcard path: " + SDCARD_ROOT_PATH);
        File file = new File(SDCARD_ROOT_PATH + File.separator + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "create file " + fileName + " failed: " + e.toString());
                return null;
            }
        }
        return file;
    }

    private synchronized void resetFile(File file) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.setLength(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized int writeToFile(File file, String content) {
        if (null == file || !file.exists()) {
            Log.e(TAG, "file not exist, please create first!");
            return -1;
        }

        if (null == content || 0 == content.length()) {
            Log.d(TAG, "Content is null");
            return -1;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            FileLock fileLock = fos.getChannel().tryLock();
            if (null != fileLock) {
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(content.getBytes());
                bos.flush();
                bos.close();
                fileLock.release();
            } else {
                Log.d(TAG, "Another process is writing to file: " + file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private static synchronized int writeToFile(File outFile, File inFile) {
        if (null == outFile || !outFile.exists()) {
            Log.e(TAG, "file not exist, please create first!");
            return -1;
        }

        if (null == inFile || 0 == inFile.length()) {
            Log.d(TAG, "inFile is null");
            return -1;
        }

        try {
            Log.d(TAG, "being prepared to write to file");
            FileOutputStream fos = new FileOutputStream(outFile, true);
            FileLock fileLock = fos.getChannel().lock();
            if (null != fileLock) {
                Log.d(TAG, "==>lock file begin");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                byte[] buf = new byte[8192];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));
                int lengthRead;
                while (-1 != (lengthRead = bis.read(buf))) {
                    bos.write(buf, 0, lengthRead);
                }
                bos.write(System.getProperty("line.separator").getBytes());
                bos.flush();
                Thread.sleep(2000);
                bis.close();
                bos.close();
                if (fileLock.isValid()) {
                    fileLock.release();
                }
                Log.d(TAG, "==>lock file end");
            } else {
                Log.d(TAG, "Another process is writing to file: " + outFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (OverlappingFileLockException ofe) {
            ofe.printStackTrace();
            return -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static synchronized String readFromFile(File file) {
        if (!file.exists()) {
            Log.e(TAG, "File read is not exist");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(file, true);
            FileLock fileLock = fos.getChannel().lock();
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] buf = new byte[8192];
            int lengthRead;
            while(-1 != (lengthRead = bis.read(buf))) {
                sb.append(new String(buf, 0, lengthRead));
            }
            bis.close();
            fos.close();
            if (fileLock.isValid()) {
                Log.d(TAG, "to release filelock");
                fileLock.release();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    private void unRegisterReceiver() {
        unregisterReceiver(WriteToFileReceiver.getInstance());
    }
}
