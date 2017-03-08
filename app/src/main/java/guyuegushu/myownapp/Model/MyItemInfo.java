package guyuegushu.myownapp.Model;

/**
 * Created by guyuegushu on 2016/10/11.
 * 保存信息的模型类
 */
public class MyItemInfo {
    private String path;
    private String name;
    private String size;
    private String letterHead;

    public MyItemInfo(Builder builder) {
        this.path = builder.path;
        this.name = builder.name;
        this.size = builder.size;
    }

    public static class Builder{
        private String path;
        private String name;
        private String size;

        public Builder path(String txtPath) {
            this.path = txtPath;
            return this;
        }

        public Builder name(String txtName) {
            this.name = txtName;
            return this;
        }

        public Builder size(String txtSize) {
            this.size = txtSize;
            return this;
        }

        public MyItemInfo build(){
            return new MyItemInfo(this);
        }
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
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
