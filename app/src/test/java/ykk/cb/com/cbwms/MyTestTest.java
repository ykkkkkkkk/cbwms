package ykk.cb.com.cbwms;

import org.junit.Test;

import java.util.List;

import ykk.cb.com.cbwms.model.CombineSalOrderEntry;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.sal.DeliOrder;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.JsonUtil;

public class MyTestTest {

    @Test
    public void main() {
        String a = "[{\"fId\":100501,\"fbillno\":\"FHTZD000002\",\"deliDate\":\"2018-07-27 00:00:00.0\",\"custId\":111036,\"custNumber\":\"1.4.01.0001\",\"custName\":\"北京世骏装饰工程有限公司（荣耀国际金融中心10F电动窗帘）\",\"deliOrgId\":100001,\"deliOrgNumber\":\"003\",\"deliOrgName\":\"佛山工厂\",\"deliOrg\":null,\"mtlId\":103690,\"mtl\":null,\"mtlFnumber\":\"941.101\",\"mtlFname\":\"15灵韵手动铝百叶\",\"mtlUnitName\":\"平方米\",\"stockId\":0,\"stockName\":null,\"stock\":null,\"deliFqty\":4.0,\"deliFremainoutqty\":4.0,\"deliveryWay\":null,\"entryId\":100501,\"isCheck\":0}]";
//        List<DeliOrder> list = JsonUtil.stringToList(a, DeliOrder.class);
//        for(int i=0; i<list.size(); i++){
//            System.out.println("abc:"+ list.get(i).toString());
//        }
        for(int i=0; i<5; i++) {
            System.out.println("parentIIIIIIIIIII");
            for(int j=0, size2=5; j<size2; j++) {
                System.out.println("sonJJJJJJJJJJJJJJJ");
                if(j == 2) {
                    break;
                }
            }
        }

    }
}