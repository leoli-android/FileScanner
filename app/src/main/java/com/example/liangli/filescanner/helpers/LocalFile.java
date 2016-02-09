package com.example.liangli.filescanner.helpers;

/**
 * Created by liangli on 2/9/16.
 *
 * Local file record format.
 */
public class LocalFile {

    private String mFileName;
    private String mFileExtension;
    private long mFileSize;

    public LocalFile(String fileName, String fileExtension, long fileSize) {
        this.mFileName = fileName;
        this.mFileExtension = fileExtension;
        this.mFileSize = fileSize;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFileExtension() {
        return mFileExtension;
    }

    public long getFileSize() {
        return mFileSize;
    }

}
