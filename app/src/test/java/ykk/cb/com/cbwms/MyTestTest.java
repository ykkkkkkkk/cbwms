package ykk.cb.com.cbwms;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.util.BigdecimalUtil;

public class MyTestTest {

    @Test
    public void main() {
        String a = "  想把 你 画 成花,，最美的一朵画-——.。 ";
        System.out.println(deleteSymbol(a));
    }

    /**
     * 收货地址去符号（逗号，句号，空格，破折号）
     */
    private String deleteSymbol(String addRess) {
        addRess = addRess.replace(",",""); // 去小写逗号
        addRess = addRess.replace("，",""); // 去大写逗号
        addRess = addRess.replace(".",""); // 去小写句号
        addRess = addRess.replace("。",""); // 去大写句号
        addRess = addRess.trim(); // 前后去空
        addRess = addRess.replace(" ",""); // 去空格
        addRess = addRess.replace("-",""); // 去小写破折号
        addRess = addRess.replace("—",""); // 去大写破折号
        return addRess;
    }
}