package com.example.veb.bookreader;

/**
 * Created by VEB on 2016/10/11.
 */
public class TxtFile {
    private String txtFilePath;
    private String txtFileName;
    private long txtFileSize;

    public TxtFile(String txtFilePath, String txtFileName, long txtFileSize) {
        this.txtFilePath = txtFilePath;
        this.txtFileName = txtFileName;
        this.txtFileSize = txtFileSize;
    }

    public String getTxtFilePath() {
        return txtFilePath;
    }

    public String getTxtFileName() {
        return txtFileName;
    }

    public long getTxtFileSize() {
        return txtFileSize;
    }
}
