package ykk.cb.com.cbwms.entrance.page0;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page0.adapter.QualityMissionAdapter;
import ykk.cb.com.cbwms.entrance.page0.adapter.QualityMissionDialogAdapter;
import ykk.cb.com.cbwms.model.QualityMissionEntry;
import ykk.cb.com.cbwms.model.QualityMissionEntryResult;
import ykk.cb.com.cbwms.model.QualityPlanDetail;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class QualityMissionActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.viewRadio3)
    View viewRadio3;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_canNum)
    TextView tvCanNum;

    private QualityMissionActivity context = this;
    private static final int SUCC1 = 100, UNSUCC1 = 550;
    private static final int MODIFY = 200, UNMODIFY = 500;
    private static final int SEL_NUM = 10, SEL_NUM2 = 11, SEL_NUM3 = 12;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private QualityMissionAdapter mAdapter;
    private List<QualityMissionEntry> listDatas = new ArrayList<>();
    private List<QualityPlanDetail> listQpd = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    public char entryStatus = '1'; // 检验状态( 1、未检验，2、检验中，3、检验完毕)
    private View curRadio;
    private int curPos, curPos2;
    private User user;
    private DecimalFormat df = new DecimalFormat("#.####");

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<QualityMissionActivity> mActivity;

        public MyHandler(QualityMissionActivity activity) {
            mActivity = new WeakReference<QualityMissionActivity>(activity);
        }

        public void handleMessage(Message msg) {
            QualityMissionActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<QualityMissionEntry> list = JsonUtil.strToList2((String) msg.obj, QualityMissionEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);


                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();
                        m.xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
                        m.xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

                        break;
                    case MODIFY: // 更新成功
                        m.toasts("提交数据成功✔");
                        m.initLoadDatas();

                        break;
                    case UNMODIFY: // 更新失败！
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍后再试！");

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_item0_qualitymission;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new QualityMissionAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                if(entryStatus == '3') return;
                curPos = pos - 1;
                writeNumDialog();
            }
        });
    }

    @Override
    public void initData() {
        curRadio = viewRadio1;
        getUserInfo();
        initLoadDatas();
    }

    @OnClick({R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.lin_tab3})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                entryStatus = '1';
                tabSelected(viewRadio1);
                tvCanNum.setVisibility(View.VISIBLE);
                initLoadDatas();

                break;
            case R.id.lin_tab2:
                entryStatus = '2';
                tvCanNum.setVisibility(View.VISIBLE);
                tabSelected(viewRadio2);
                initLoadDatas();

                break;
            case R.id.lin_tab3:
                entryStatus = '3';
                tvCanNum.setVisibility(View.GONE);
                tabSelected(viewRadio3);
                initLoadDatas();

                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("purchaseMission/findQualityMissionEntry_app");
        FormBody formBody = new FormBody.Builder()
                .add("staffId", String.valueOf(user.getStaffId()))
                .add("entryStatus", String.valueOf(entryStatus))
                .add("limit", String.valueOf(limit))
                .add("pageSize", "20")
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("QualityMissionEntry_ListActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交检验数量
     */
    private void run_modifyFqty_app(String num1, String num2, String num3, char entryStatus) {
        showLoadDialog("提交中...");
        String mUrl = getURL("purchaseMission/modifyFqty_app");
        QualityMissionEntry qmEntry = listDatas.get(curPos);
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(qmEntry.getId()))
                .add("entryStatus", String.valueOf(entryStatus))
                .add("checkedFqty", String.valueOf(num1))
                .add("qualifiedFqty", String.valueOf(num2))
                .add("unQualifiedFqty", String.valueOf(num3))
                .add("strJson", JsonUtil.objectToString(qmEntry))
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNMODIFY);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY);
                    return;
                }

                Message msg = mHandler.obtainMessage(MODIFY, result);
                Log.e("run_modifyFqty_app --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        isLoadMore = false;
        initLoadDatas();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        isLoadMore = true;
        limit += 1;
        run_okhttpDatas();
    }

    /**
     * 检验数量输入
     */
    private AlertDialog alertDialog;
    private QualityMissionDialogAdapter dialogAdapter;
    private List<QualityMissionEntry> listItems;
    private TextView tvCountNum1, tvCountNum2, tvCountNum3;
    private void writeNumDialog() {
        if(df == null) df = new DecimalFormat("#.####");
        View v = context.getLayoutInflater().inflate(R.layout.ab_item0_qualitymission_dialog, null);
        alertDialog = null;
        dialogAdapter = null;
        tvCountNum1 = null;
        tvCountNum2 = null;
        tvCountNum3 = null;
        alertDialog = new AlertDialog.Builder(context).setView(v).create();

        // 初始化id
        TextView tvNo = (TextView) v.findViewById(R.id.tv_no);
        TextView tvMtlName = (TextView) v.findViewById(R.id.tv_mtlName);
        tvCountNum1 = (TextView) v.findViewById(R.id.tv_countNum1);
        tvCountNum2 = (TextView) v.findViewById(R.id.tv_countNum2);
        tvCountNum3 = (TextView) v.findViewById(R.id.tv_countNum3);

        XRecyclerView  xRecyclerView2 = (XRecyclerView) v.findViewById(R.id.xRecyclerView);
        Button btnClose = (Button) v.findViewById(R.id.btn_close);
        Button btnSubmit = (Button) v.findViewById(R.id.btn_submit);
        Button btnSubmit2 = (Button) v.findViewById(R.id.btn_submit2);

        QualityMissionEntry qmE = listDatas.get(curPos);
        listQpd.clear();
        List<QualityPlanDetail> list = qmE.getQualityPlanDetailList();
        listQpd.addAll(list);
        // 如果该项目没有检验数，就默认用单据数填上检验数
        for(int i=0; i<listQpd.size(); i++) {
            QualityPlanDetail qpd = listQpd.get(i);
            if(qpd.getQualityMissionEntryResult().getQualityCheckFqty() == 0) {
                qpd.getQualityMissionEntryResult().setQualityCheckFqty(qmE.getFqty());
            }
        }
        tvNo.setText(Html.fromHtml("<font color='#666666'>单据编号：</font>"+qmE.getMission().getMissionNumber()));
        tvMtlName.setText(Html.fromHtml("<font color='#666666'>物料名称：</font>"+qmE.getMaterialName()));
        // 复制list
        List<QualityPlanDetail> listTemp = new ArrayList<>();
        for(int i=0; i<listQpd.size(); i++) {
            listTemp.add(listQpd.get(i));
        }
        // 利用冒泡排序得到最大的不良数
        for (int i = 0; i < listQpd.size() - 1; i++) {//外层循环控制排序趟数
            for (int j = 0; j < listQpd.size() - 1 - i; j++) {//内层循环控制每一趟排序多少次
                QualityPlanDetail qpd1 = listTemp.get(j);
                QualityMissionEntryResult qmeR1 = qpd1.getQualityMissionEntryResult();
                QualityPlanDetail qpd2 = listTemp.get(j+1);
                QualityMissionEntryResult qmeR2 = qpd2.getQualityMissionEntryResult();

                if (qmeR1.getResultUnQualifiedFqty() < qmeR2.getResultUnQualifiedFqty()) {
                    QualityPlanDetail temp = qpd1;
                    listTemp.set(j, qpd2);
                    listTemp.set(j+1, temp);
                }
            }
        }
        QualityPlanDetail qpdTemp = listTemp.get(0);
        tvCountNum1.setText(df.format(qpdTemp.getQualityMissionEntryResult().getQualityCheckFqty()));
        tvCountNum2.setText(df.format(qpdTemp.getQualityMissionEntryResult().getResultQualifiedFqty()));
        tvCountNum3.setText(df.format(qpdTemp.getQualityMissionEntryResult().getResultUnQualifiedFqty()));
        // 初始化listView
        xRecyclerView2.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView2.setLayoutManager(new LinearLayoutManager(context));
        dialogAdapter = new QualityMissionDialogAdapter(context, listQpd);
        xRecyclerView2.setAdapter(dialogAdapter);
        xRecyclerView2.setLoadingListener(context);

        xRecyclerView2.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView2.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        dialogAdapter.setCallBack(new QualityMissionDialogAdapter.MyCallBack() {
            @Override
            public void onClick_num1(QualityPlanDetail entity, int position) {
                Log.e("num", "行：" + position);
                curPos2 = position;
                showInputDialog("检验数", String.valueOf(entity.getQualityMissionEntryResult().getQualityCheckFqty()), "0", SEL_NUM);
            }

            @Override
            public void onClick_num2(QualityPlanDetail entity, int position) {
                Log.e("num", "行：" + position);
                curPos2 = position;
                showInputDialog("合格数", String.valueOf(entity.getQualityMissionEntryResult().getResultQualifiedFqty()), "0", SEL_NUM2);
            }

            @Override
            public void onClick_num3(QualityPlanDetail entity, int position) {
                Log.e("num", "行：" + position);
                curPos2 = position;
                showInputDialog("不良品", String.valueOf(entity.getQualityMissionEntryResult().getResultUnQualifiedFqty()), "0", SEL_NUM3);
            }

        });

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                alertDialog.dismiss();
            }
        });
        // 提交
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_submit: // 提交--检验中
                        /*检验状态 1、未检验，2、检验中，3、检验完毕*/
                        submit(v, '2');

                        break;
                    case R.id.btn_submit2: // 提交--已完成
                        /*检验状态 1、未检验，2、检验中，3、检验完毕*/
                        submit(v, '3');

                        break;
                }
            }
        };
        btnSubmit.setOnClickListener(click);
        btnSubmit2.setOnClickListener(click);

        Window window = alertDialog.getWindow();
        alertDialog.setCancelable(false);
        alertDialog.show();
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 提交的方法
     */
    private void submit(View v, char entryStatus) {
//        QualityMissionEntry qmE = listDatas.get(curPos2);
//        String strNum1 = getValues(etNum1).trim();
//        String strNum2 = getValues(etNum2).trim();
//        String strNum3 = getValues(etNum3).trim();
//        double num1 = parseDouble(strNum1);
//        double num2 = parseDouble(strNum2);
//        double num3 = parseDouble(strNum3);
//        if(num1 == 0) {
//            Comm.showWarnDialog(context,"请输入“检验数”！");
//            return;
//        }
//        if(strNum2.length() == 0) {
//            Comm.showWarnDialog(context,"请输入“合格数”！");
//            return;
//        }
//        if(strNum2.length() == 0) {
//            Comm.showWarnDialog(context,"请输入“不良数”！");
//            return;
//        }
//        if(num2 > num1) {
//            Comm.showWarnDialog(context,"“合格数”不能大于“检验数”！");
//            return;
//        }
//        if(num3 > num1) {
//            Comm.showWarnDialog(context,"“不良数”不能大于“检验数”！");
//            return;
//        }
//        if(num1 > (qmE.getFqty() - qmE.getCheckedFqty())) {
//            Comm.showWarnDialog(context,"“检验数”不能大于“可检数”！");
//            return;
//        }
//        // 已检数加上检验数等于单据数，状态等于已完成
//        entryStatus = (num1+qmE.getCheckedFqty() == qmE.getFqty() ? '3' : entryStatus);
//
        run_modifyFqty_app(getValues(tvCountNum1), getValues(tvCountNum2), getValues(tvCountNum3), entryStatus);
        hideKeyboard(v);

        alertDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_NUM: //输入数量	返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        listQpd.get(curPos2).getQualityMissionEntryResult().setQualityCheckFqty(num);
                        dialogAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_NUM2: //输入数量	返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        QualityPlanDetail qme = listQpd.get(curPos2);
                        qme.getQualityMissionEntryResult().setResultQualifiedFqty(num);

                        double num1 = qme.getQualityMissionEntryResult().getQualityCheckFqty();
                        if (num1 > 0) {
                            double sum = num1 - num;
                            qme.getQualityMissionEntryResult().setResultUnQualifiedFqty(sum);
                        }
                        // 复制list
                        List<QualityPlanDetail> listTemp = new ArrayList<>();
                        for(int i=0; i<listQpd.size(); i++) {
                            listTemp.add(listQpd.get(i));
                        }
                        // 利用冒泡排序得到最大的不良数
                        for (int i = 0; i < listTemp.size() - 1; i++) {//外层循环控制排序趟数
                            for (int j = 0; j < listTemp.size() - 1 - i; j++) {//内层循环控制每一趟排序多少次
                                QualityPlanDetail qpd1 = listTemp.get(j);
                                QualityMissionEntryResult qmeR1 = qpd1.getQualityMissionEntryResult();
                                QualityPlanDetail qpd2 = listTemp.get(j+1);
                                QualityMissionEntryResult qmeR2 = qpd2.getQualityMissionEntryResult();

                                if (qmeR1.getResultUnQualifiedFqty() < qmeR2.getResultUnQualifiedFqty()) {
                                    QualityPlanDetail temp = qpd1;
                                    listTemp.set(j, qpd2);
                                    listTemp.set(j+1, temp);
                                }
                            }
                        }
                        QualityPlanDetail qpdTemp = listTemp.get(0);
                        tvCountNum1.setText(df.format(qpdTemp.getQualityMissionEntryResult().getQualityCheckFqty()));
                        tvCountNum2.setText(df.format(qpdTemp.getQualityMissionEntryResult().getResultQualifiedFqty()));
                        tvCountNum3.setText(df.format(qpdTemp.getQualityMissionEntryResult().getResultUnQualifiedFqty()));

                        dialogAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_NUM3: //输入数量	返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        QualityPlanDetail qme = listQpd.get(curPos2);
                        qme.getQualityMissionEntryResult().setResultUnQualifiedFqty(num);

                        double num1 = qme.getQualityMissionEntryResult().getQualityCheckFqty();
                        if (num1 > 0) {
                            double sum = num1 - num;
                            qme.getQualityMissionEntryResult().setResultQualifiedFqty(sum);
                        }
                        // 复制list
                        List<QualityPlanDetail> listTemp = new ArrayList<>();
                        for(int i=0; i<listQpd.size(); i++) {
                            listTemp.add(listQpd.get(i));
                        }
                        // 利用冒泡排序得到最大的不良数
                        for (int i = 0; i < listTemp.size() - 1; i++) {//外层循环控制排序趟数
                            for (int j = 0; j < listTemp.size() - 1 - i; j++) {//内层循环控制每一趟排序多少次
                                QualityPlanDetail qpd1 = listTemp.get(j);
                                QualityMissionEntryResult qmeR1 = qpd1.getQualityMissionEntryResult();
                                QualityPlanDetail qpd2 = listTemp.get(j+1);
                                QualityMissionEntryResult qmeR2 = qpd2.getQualityMissionEntryResult();

                                if (qmeR1.getResultUnQualifiedFqty() < qmeR2.getResultUnQualifiedFqty()) {
                                    QualityPlanDetail temp = qpd1;
                                    listTemp.set(j, qpd2);
                                    listTemp.set(j+1, temp);
                                }
                            }
                        }
                        QualityPlanDetail qpdTemp = listTemp.get(0);
                        tvCountNum1.setText(df.format(qpdTemp.getQualityMissionEntryResult().getQualityCheckFqty()));
                        tvCountNum2.setText(df.format(qpdTemp.getQualityMissionEntryResult().getResultQualifiedFqty()));
                        tvCountNum3.setText(df.format(qpdTemp.getQualityMissionEntryResult().getResultUnQualifiedFqty()));

                        dialogAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler);
            context.finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        closeHandler(mHandler);
        super.onDestroy();
    }

}
