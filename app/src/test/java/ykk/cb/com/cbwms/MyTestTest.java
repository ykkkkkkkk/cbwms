package ykk.cb.com.cbwms;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.util.BigdecimalUtil;

public class MyTestTest {

    @Test
    public void main() {
        List<String> list = new ArrayList<String>();
        list.add("AAA");
        list.add("BBB");
        List<String> list2 = new ArrayList<String>();
        list2.addAll(list);
        list.clear();
        for(String str : list2) {
            System.out.print(str);
        }
    }
}