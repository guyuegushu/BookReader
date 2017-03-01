package guyuegushu.myownapp.Model;

/**
 * Created by guyuegushu on 2016/10/11.
 *
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

    public void setLetterHead(String pinyin) {
        String sortString = pinyin.substring(0, 1).toUpperCase();

        if (sortString.matches("[A-Z]")) {
            letterHead = sortString;
        } else {
            letterHead = "#";
        }
    }

}
