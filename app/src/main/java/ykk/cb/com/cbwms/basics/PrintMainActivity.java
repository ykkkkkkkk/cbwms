package ykk.cb.com.cbwms.basics;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.MyViewPager;
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter;
import ykk.cb.com.cbwms.util.blueTooth.BluetoothDeviceListDialog;
import ykk.cb.com.cbwms.util.blueTooth.Constant;
import ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager;
import ykk.cb.com.cbwms.util.blueTooth.ThreadPool;
import ykk.cb.com.cbwms.util.blueTooth.Utils;
import ykk.cb.com.cbwms.util.interfaces.IFragmentKeyeventListener;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.cb.com.cbwms.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER;
import static ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;

public class PrintMainActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.tv_connState)
    TextView tvConnState;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_2)
    TextView tv2;
    private PrintMainActivity context = this;
    private static final String TAG = "PrintFragmentsActivity";
    private TextView curText;
    private IFragmentKeyeventListener fragment2Listener;
    private String barcode; // 打印的条码
    private boolean isConnected; // 蓝牙是否连接标识
    private int tabFlag;
    private int id = 0; // 设备id
    private ThreadPool threadPool;
    private Material mtl;
    private ProdOrder prodOrder;
    /**
     * 连接状态断开
     */
    private static final int CONN_STATE_DISCONN = 0x007;
    /**
     * 使用打印机指令错误
     */
    private static final int PRINTER_COMMAND_ERROR = 0x008;
    private static final int CONN_PRINTER = 0x12;

    @Override
    public int setLayoutResID() {
        return R.layout.ab_print_main;
    }

    @Override
    public void initData() {

        curText = tv1;
        List<Fragment> listFragment = new ArrayList<Fragment>();
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
//        fragment2.setArguments(bundle2); // 传参数
        PrintFragment1 fragment1 = new PrintFragment1();
        PrintFragment2 fragment2 = new PrintFragment2();

        listFragment.add(fragment1);
        listFragment.add(fragment2);
//        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), listFragment));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);

        //ViewPager页面切换监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabSelected(tv1);
                        viewPager.setCurrentItem(0, false);
                        break;
                    case 1:
                        tabSelected(tv2);
                        viewPager.setCurrentItem(1, false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(TextView tv) {
        if(curText.getId() == tv.getId()) {
            return;
        }
        curText.setText(getValues(curText).replace("✔",""));
        curText.setTextColor(Color.parseColor("#666666" +""));
        tv.setText(getValues(tv)+"✔");
        tv.setTextColor(Color.parseColor("#FF3300"));
        curText = tv;
    }

    @OnClick({R.id.btn_close, R.id.tv_1, R.id.tv_2})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                context.finish();

                break;
            case R.id.tv_1: // 物料
                tabSelected(tv1);
                viewPager.setCurrentItem(0, false);

                break;
            case R.id.tv_2: // 扫码打印
                tabSelected(tv2);
                viewPager.setCurrentItem(1, false);

                break;
        }
    }

    /**
     * Fragment打印回调
     */
    public void setFragmentPrint(int flag, String result) {
        tabFlag = flag;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        String date = Comm.getSysDate(7);
        mtl = JsonUtil.stringToObject(result, Material.class);
        barcode = mtl.getBarcodeTable().getBarcode();

        if(isConnected) {
            sendLabel();
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }
    public void setFragmentPrint2(int flag, String result) {
        tabFlag = flag;
        if(tabFlag != flag) {
//            isConnected = false;
        }
        String date = Comm.getSysDate(7);
        BarCodeTable bt = JsonUtil.strToObject(result, BarCodeTable.class);
        switch (bt.getCaseId()) {
            case 34: // 生产任务单
                prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);
                barcode = bt.getBarcode();

                break;
        }
        if(isConnected) {
            sendLabel();
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
        }
    }

    public void setFragmentKeyeventListener(IFragmentKeyeventListener fragmentKeyeventListener) {
        this.fragment2Listener = fragmentKeyeventListener;
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                /*蓝牙连接*/
                case Constant.BLUETOOTH_REQUEST_CODE: {
                    /*获取蓝牙mac地址*/
                    String macAddress = data.getStringExtra(BluetoothDeviceListDialog.EXTRA_DEVICE_ADDRESS);
                    //初始化话DeviceConnFactoryManager
                    new DeviceConnFactoryManager.Build()
                            .setId(id)
                            //设置连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                            //设置连接的蓝牙mac地址
                            .setMacAddress(macAddress)
                            .build();
                    //打开端口
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                    break;
                }
            }
        }
    }

    /**
     * 设置物料打码格式
     * @param tsc
     */
    private void setMtlFormat(LabelCommand tsc) {
        tsc.addText(150, 28, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"【物料条码】");
        tsc.addText(20, 90, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料名称："+mtl.getfName());
        tsc.addText(20, 120, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料代码："+mtl.getfNumber());
        tsc.addText(20, 150, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料规格："+isNULLS(mtl.getMaterialSize()));
        tsc.addText(20, 216, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"条码：");

        // 绘制一维条码
        tsc.add1DBarcode(120, 190, LabelCommand.BARCODETYPE.CODE39, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, barcode);
    }

    /**
     * 设置生产订单打码格式（大标签）
     * @param tsc
     */
    private void setDeliOrderFormat(LabelCommand tsc) {
        int beginXPos = 20; // 开始横向位置
        int beginYPos = 26; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离
        String date = Comm.getSysDate(7);

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户名称："+isNULLS(prodOrder.getCustName())+" \n");
        rowHigthSum = beginYPos + 38;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"联系人："+isNULLS(prodOrder.getReceivePerson())+" \n");
        tsc.addText(260, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"日期："+date+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"电话："+isNULLS(prodOrder.getReceiveTel())+" \n");
        tsc.addText(260, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"生产序号："+isNULLS(prodOrder.getProdSeqNumber())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"收货地址："+isNULLS(prodOrder.getReceiveAddress())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单号："+prodOrder.getFbillno()+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"产品名称："+isNULLS(prodOrder.getMtlFname())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"产品规格："+isNULLS(prodOrder.getMtlSize())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"备注："+isNULLS(prodOrder.getRemarks())+" \n");
        rowHigthSum = rowHigthSum + 51;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"条码： \n");
        // 绘制一维条码
        tsc.add1DBarcode(115, rowHigthSum-20, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, barcode);
    }
    /**
     * 设置生产订单打码格式（小标签）
     * @param tsc
     */
    private void setDeliOrderFormat2(LabelCommand tsc) {
        int beginXPos = 20; // 开始横向位置
        int beginYPos = 26; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 31; // 每行之间的距离
        String date = Comm.getSysDate(7);

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户名称："+isNULLS(prodOrder.getCustName())+" \n");
        rowHigthSum = beginYPos + 38;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单号："+isNULLS(prodOrder.getFbillno())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"产品名称："+isNULLS(prodOrder.getMtlFname())+" \n");

        String leaf = isNULLS(prodOrder.getLeaf());
        String leaf2 = isNULLS(prodOrder.getLeaf1());
        String strTmp = "";
        if(leaf.length() > 0 && leaf2.length() > 0) {
            strTmp = leaf+" , "+leaf2;
        } else if(leaf.length() > 0) {
            strTmp = leaf;
        } else if(leaf2.length() > 0) {
            strTmp = leaf2;
        }
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"布料/叶片："+strTmp+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"产品规格："+isNULLS(prodOrder.getMtlSize())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"备注："+isNULLS(prodOrder.getRemarks())+" \n");
    }


    /**
     * 发送标签
     *
     */
    void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(76, 48);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(10);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
        // 设置原点坐标
        tsc.addReference(0, 0);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();
            // 绘制简体中文
        switch (tabFlag) {
            case 0: // 物料条码
                setMtlFormat(tsc);

                break;
            case 1: // 生产订单--大标签
                setDeliOrderFormat(tsc);

                break;
            case 2: // 生产订单--小标签
                setDeliOrderFormat2(tsc);

                break;
        }
//        tsc.addText(20, 250, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,"【物料条码】");
//        tsc.addText(20, 300, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料名称："+);
//        // 绘制一维条码
//        tsc.add1DBarcode(20, 250, LabelCommand.BARCODETYPE.CODE128, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "SMARNET");
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响

        tsc.addSound(2, 100);
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(datas);
    }

    /**
     * 蓝牙监听广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                // 蓝牙连接断开广播
                case ACTION_USB_DEVICE_DETACHED:
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                    break;
                case DeviceConnFactoryManager.ACTION_CONN_STATE:
                    int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                    int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
                    switch (state) {
                        case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                            if (id == deviceId) {
                                tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                                tvConnState.setTextColor(Color.parseColor("#666666")); // 未连接-灰色
                                isConnected = false;
                            }
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                            tvConnState.setText(getString(R.string.str_conn_state_connecting));
                            tvConnState.setTextColor(Color.parseColor("#6a5acd")); // 连接中-紫色
                            isConnected = false;

                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
//                            tvConnState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
                            tvConnState.setText(getString(R.string.str_conn_state_connected));
                            tvConnState.setTextColor(Color.parseColor("#008800")); // 已连接-绿色
                            sendLabel();
                            isConnected = true;

                            break;
                        case CONN_STATE_FAILED:
                            Utils.toast(context, getString(R.string.str_conn_fail));
                            tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                            tvConnState.setTextColor(Color.parseColor("#666666")); // 未连接-灰色
                            isConnected = false;

                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_STATE_DISCONN:
                    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].closePort(id);
                    }
                    break;
                case PRINTER_COMMAND_ERROR:
                    Utils.toast(context, getString(R.string.str_choice_printer_command));
                    break;
                case CONN_PRINTER:
                    Utils.toast(context, getString(R.string.str_cann_printer));
                    break;
                case MESSAGE_UPDATE_PARAMETER:
                    String strIp = msg.getData().getString("Ip");
                    String strPort = msg.getData().getString("Port");
                    //初始化端口信息
                    new DeviceConnFactoryManager.Build()
                            //设置端口连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI)
                            //设置端口IP地址
                            .setIp(strIp)
                            //设置端口ID（主要用于连接多设备）
                            .setId(id)
                            //设置连接的热点端口号
                            .setPort(Integer.parseInt(strPort))
                            .build();
                    threadPool = ThreadPool.getInstantiation();
                    threadPool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy()");
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
        }
    }

    private String getConnDeviceInfo() {
        String str = "";
        DeviceConnFactoryManager deviceConnFactoryManager = DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id];
        if (deviceConnFactoryManager != null
                && deviceConnFactoryManager.getConnState()) {
            if ("USB".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "USB\n";
                str += "USB Name: " + deviceConnFactoryManager.usbDevice().getDeviceName();
            } else if ("WIFI".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "WIFI\n";
                str += "IP: " + deviceConnFactoryManager.getIp() + "\t";
                str += "Port: " + deviceConnFactoryManager.getPort();
            } else if ("BLUETOOTH".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "BLUETOOTH\n";
                str += "MacAddress: " + deviceConnFactoryManager.getMacAddress();
//                deviceConnFactoryManager.get
            } else if ("SERIAL_PORT".equals(deviceConnFactoryManager.getConnMethod().toString())) {
                str += "SERIAL_PORT\n";
                str += "Path: " + deviceConnFactoryManager.getSerialPortPath() + "\t";
                str += "Baudrate: " + deviceConnFactoryManager.getBaudrate();
            }
        }
        return str;
    }

    /**
     * 连接蓝牙前的判断
     */
//    private void connectBluetoothBefore() {
//        //获取蓝牙适配器实例。如果设备不支持蓝牙则返回null
//        if (mBluetoothAdapter == null) {
//            toasts("设备不支持蓝牙！");
//            return;
//        }
//        // 判断蓝牙是否开启
//        if (!mBluetoothAdapter.isEnabled()) {
//            // 蓝牙未开启，打开蓝牙
//            openBluetooth();
//            return;
//        }
//        // 判断状态为连接
//        if (isConnected) {
//            print();
//        } else {
//            pair();
//        }
//    }
//
//    /**
//     * 打开蓝牙
//     */
//    private void openBluetooth() {
//        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
//            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enabler);
//        }
//    }
//
//    /**
//     * 得到已配对的列表进行配对
//     */
//    private void pair() {
//        alertDialog = null;
//        View v = context.getLayoutInflater().inflate(R.layout.bluetooth_oklist, null);
//        alertDialog = new AlertDialog.Builder(context).setView(v).create();
//        // 初始化id
//        Button btn_close = (Button) v.findViewById(R.id.btn_close);
//        LinearLayout lin_oklist = v.findViewById(R.id.lin_oklist);
//        if (lin_oklist.getChildCount() > 0) { // 每次都清空子View
//            lin_oklist.removeAllViews();
//        }
//
//        // 单击事件
//        View.OnClickListener click = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = null;
//                switch (v.getId()) {
//                    case R.id.btn_close: // 关闭
//                        if (alertDialog != null && alertDialog.isShowing()) {
//                            alertDialog.dismiss();
//                        }
//                        break;
//                }
//            }
//        };
//        btn_close.setOnClickListener(click);
//        // 得到已配对的蓝牙设备
//        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
//        if (devices.size() > 0) {
//            for (BluetoothDevice blueDevice : devices) {
//                String str = blueDevice.getName() + " : (" + blueDevice.getAddress() + ")";
//                addView(lin_oklist, str); // 添加到布局
//            }
//
//            Window window = alertDialog.getWindow();
//            alertDialog.setCancelable(false);
//            alertDialog.show();
//            window.setGravity(Gravity.CENTER);
//        } else {
//            toasts("请先配对蓝牙打印机！");
//        }
//    }
//
//    /**
//     * 添加到LinearLayout
//     */
//    private void addView(LinearLayout lin, String twoMenuName) {
//        View v = LayoutInflater.from(context).inflate(R.layout.bluetooth_oklist_item, null);
//        final TextView tv_item = (TextView) v.findViewById(R.id.tv_item);
//        tv_item.setText(twoMenuName);
//        // 设置单击事件
//        tv_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 点击就配对，然后关闭这个dialog
//                String item = getValues(tv_item);
//                // 截取的格式为：名称:(20:20:20:20:20),只截取括号里的
//                String address = item.substring(item.indexOf("(") + 1, item.indexOf(")"));
//                printUtils.openport(address);
//
//                if (alertDialog != null && alertDialog.isShowing()) {
//                    alertDialog.dismiss();
//                }
//            }
//        });
//        ViewGroup parent = (ViewGroup) tv_item.getParent();
//        if (parent != null) {
//            parent.removeAllViews();
//        }
//        // 添加到容器中
//        lin.addView(tv_item);
//    }
//
//    /**
//     * 广播监听蓝牙状态
//     */
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context2, Intent intent) {
//            String action = intent.getAction();
//            switch (action) {
//                case BluetoothDevice.ACTION_ACL_CONNECTED: // 已连接
//                    toasts("已连接蓝牙设备√");
//                    isConnected = true;
//                    connectBluetoothBefore();
//
//                    break;
//                case BluetoothDevice.ACTION_ACL_DISCONNECTED:// 断开连接
//                    //蓝牙连接被切断
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    String name = device.getName();
//                    toasts(name + "的连接已断开！");
//                    isConnected = false;
//
//                    break;
//            }
//        }
//    };
//
//    /**
//     * 蓝牙监听需要添加的Action
//     */
//    private IntentFilter makeFilters() {
//        IntentFilter intentFilter = new IntentFilter();
////        intentFilter.addAction("android.openBluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
////        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
////        intentFilter.addAction("android.openBluetooth.BluetoothAdapter.STATE_OFF");
////        intentFilter.addAction("android.openBluetooth.BluetoothAdapter.STATE_ON");
//        return intentFilter;
//    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 第二个Fragment 才监听删除键
        if (viewPager.getCurrentItem() == 1 && fragment2Listener!=null){
            boolean isBool = fragment2Listener.onFragmentKeyEvent(event);
            if(!isBool) {
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

}
