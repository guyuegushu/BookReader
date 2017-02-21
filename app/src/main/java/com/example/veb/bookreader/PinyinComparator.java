package com.example.veb.bookreader;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/2/20.
 */

public class PinyinComparator implements Comparator<MyTxtInfo> {
    @Override
    public int compare(MyTxtInfo lhs, MyTxtInfo rhs) {
        if (lhs.getLetterHead().equals("#")) {
            return 1;
        } else if (rhs.getLetterHead().equals("#")) {
            return -1;
        } else {
            return lhs.getLetterHead().compareTo(rhs.getLetterHead());
        }
    }
}
