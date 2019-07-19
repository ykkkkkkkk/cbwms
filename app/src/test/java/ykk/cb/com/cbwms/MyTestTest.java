package ykk.cb.com.cbwms;

import org.junit.Test;

import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.util.BigdecimalUtil;

public class MyTestTest {

    @Test
    public void main() {
        double a = BigdecimalUtil.add(10.8, BigdecimalUtil.sub(2.3, 2.2));
        System.out.print(a);
    }
}