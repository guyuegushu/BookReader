package com.example.veb.bookreader;

/**
 * Created by VEB on 2016/10/11.
 */
public class MyTxtInfo {
    private String txtPath;
    private String txtName;
    private String txtSize;
    private String letterHead;

    public MyTxtInfo(String txtPath, String txtName, String txtSize) {
        this.txtPath = txtPath;
        this.txtName = txtName;
        this.txtSize = txtSize;
        LetterHead();
    }

    public String getTxtPath() {
        return txtPath;
    }

    public String getTxtName() {
        return txtName;
    }

    public String getTxtSize() {
        return txtSize;
    }

    public String getLetterHead() {
        return letterHead;
    }

    public void setLetterHead(String letterHead) {
        this.letterHead = letterHead;
    }

    private void LetterHead() {
        String pinyin = GlobalApplication.getParser().getSelling(getTxtName());
        String firstLetter = pinyin.substring(0,1).toUpperCase();

        if (firstLetter.matches("[A-Z]")) {
            setLetterHead(firstLetter);
        } else {
            setLetterHead("#");
        }
    }
}
