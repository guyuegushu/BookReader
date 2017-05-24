package guyuegushu.myownapp.StaticGlobal;

import guyuegushu.myownapp.Model.MyItemInfo;

import java.util.Comparator;

/**
 * Created by guyuegushu on 2017/2/20.
 */

public class PinyinComparator implements Comparator<MyItemInfo> {
    @Override
    public int compare(MyItemInfo lhs, MyItemInfo rhs) {
        //a > b --> 1 , a和b互换，小的在前面这个时候
        if (lhs == null || rhs == null) {
            if (lhs == null && rhs == null) {
                return 0;
            } else if (lhs == null) {
                return 1;
            } else {
                return -1;
            }
        } else if (lhs.getLetterHead().equals("#") || rhs.getLetterHead().equals("#")) {
            if (lhs.getLetterHead().equals("#")) {
                return -1;
            } else if (rhs.getLetterHead().equals("#")) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return lhs.getLetterHead().compareTo(rhs.getLetterHead());
        }
    }
}
