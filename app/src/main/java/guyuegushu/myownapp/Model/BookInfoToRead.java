package guyuegushu.myownapp.Model;

/**
 * Created by guyuegushu on 2017/6/28
 * 保存信息的模型类
 */
public class BookInfoToRead {
    private String address;
    private String chapter;
    private int page;
    private int current;
    private int forward;
    private int back;

    public BookInfoToRead(Builder builder) {
        this.address = builder.address;
        this.chapter = builder.chapter;
        this.page = builder.page;
        this.current = builder.current;
        this.forward = builder.forward;
        this.back = builder.back;
    }

    public static class Builder {
        private String address;
        private String chapter;
        private int page;
        private int current;
        private int forward;
        private int back;

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder chapter(String chapter) {
            this.chapter = chapter;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder current(int current) {
            this.current = current;
            return this;
        }

        public Builder forward(int forward) {
            this.forward = forward;
            return this;
        }

        public Builder back(int back) {
            this.back = back;
            return this;
        }

        public BookInfoToRead build() {
            return new BookInfoToRead(this);
        }
    }

    public String getAddress() {
        return address;
    }

    public String getChapter() {
        return chapter;
    }

    public int getPage() {
        return page;
    }

    public int getCurrent() {
        return current;
    }

    public int getForward() {
        return forward;
    }

    public int getBack() {
        return back;
    }
}
