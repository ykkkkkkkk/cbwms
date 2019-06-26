package ykk.cb.com.cbwms;

import org.junit.Test;

import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.util.BigdecimalUtil;

public class MyTestTest {

    @Test
    public void main() {
        String a = "1.1.06.00.00.08";
        int len = a.length();
        String result = a.substring(len-1, len);
        System.out.print(result);
    }
}