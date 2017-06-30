package org.secfirst.umbrella.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import info.guardianproject.iocipher.File;
import info.guardianproject.iocipher.FileInputStream;
import info.guardianproject.iocipher.VirtualFileSystem;

import static java.util.Arrays.copyOf;

public class IOCipherContentProvider extends ContentProvider {
    public static final String TAG = "IOCipherContentProvider";
    public static final String SHARE_FILE = "share.db";
    public static final Uri FILES_URI = Uri
            .parse("content://org.secfirst.umbrella/");
    private MimeTypeMap mimeTypeMap;
    private static final String[] COLUMNS = {
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE };

    @Override
    public boolean onCreate() {
        mimeTypeMap = MimeTypeMap.getSingleton();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        return mimeTypeMap.getMimeTypeFromExtension(fileExtension);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        ParcelFileDescriptor[] pipe = null;
        InputStream in = null;

        try {
            pipe = ParcelFileDescriptor.createPipe();
            String path = uri.getPath();
            Log.i(TAG, "streaming " + path);
            // BufferedInputStream could help, AutoCloseOutputStream conflicts
            in = new FileInputStream(new File(path));
            new PipeFeederThread(in, new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1])).start();
        } catch (IOException e) {
            Log.e(TAG, "Error opening pipe", e);
            throw new FileNotFoundException("Could not open pipe for: "
                    + uri.toString());
        }

        return (pipe[0]);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        java.io.File f = new java.io.File(Global.INSTANCE.getCacheDir(), SHARE_FILE);
        if (!f.exists()) {
            throw new RuntimeException("Operation not supported");
        }
        String path = f.getPath();

        VirtualFileSystem vfs = VirtualFileSystem.get();
        vfs.setContainerPath(path);
        if (!vfs.isMounted()) vfs.mount(path, Global.INSTANCE.getOrmHelper().getPassword());

        File file = new File("/", uri.getPath().replace(FILES_URI.getPath(),""));

        if (projection == null) {
            projection = COLUMNS;
        }

        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int i = 0;
        for (String col : projection) {
            if (OpenableColumns.DISPLAY_NAME.equals(col)) {
                cols[i] = OpenableColumns.DISPLAY_NAME;
                values[i++] = file.getName();
            } else if (OpenableColumns.SIZE.equals(col)) {
                cols[i] = OpenableColumns.SIZE;
                values[i++] = file.length();
            }
        }

        cols = copyOf(cols, i);
        values = copyOf(values, i);

        final MatrixCursor cursor = new MatrixCursor(cols, 1);
        cursor.addRow(values);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        throw new RuntimeException("Operation not supported");
    }

    static class PipeFeederThread extends Thread {
        InputStream in;
        OutputStream out;

        PipeFeederThread(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            byte[] buf = new byte[8192];
            int len;

            try {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "File transfer failed:", e);
            }
        }
    }
}