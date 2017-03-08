package guyuegushu.myownapp.StaticGlobal;

import guyuegushu.myownapp.Model.MyItemInfo;

import java.util.Comparator;

/**
 * Created by guyuegushu on 2017/2/20.
 *
 */

public class PinyinComparator implements Comparator<MyItemInfo> {
    @Override
    public int compare(MyItemInfo lhs, MyItemInfo rhs) {
        if (lhs.getLetterHead().equals("#")) {
            return 1;
        } else if (rhs.getLetterHead().equals("#")) {
            return -1;
        } else {
            return lhs.getLetterHead().compareTo(rhs.getLetterHead());
        }
    }
}
