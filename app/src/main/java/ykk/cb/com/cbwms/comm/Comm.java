package ykk.cb.com.cbwms.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import ykk.cb.com.cbwms.R;


/**
 * 这是一个公共类
 *
 * @author ykk
 *
 */
public class Comm {
	public static final String publicPaths = Environment.getExternalStorageDirectory().toString()+"/cbwms/";

	/**
	 * 字符串截取
	 * (index=0,从0的位置开始截取，否则从大于0的位置截取)
	 */
	public static String subString(String str,char ch, int index) {
		String result = null;
		if (str != null && str != "") {
			if (index == 0) {
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == ch) {
						result = str.substring(0, i);
					}
				}
			}else{
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == ch) {
						result = str.substring(i+1);
					}
				}
			}

		}
		return result;
	}
	/**
	 * 转换成int类型
	 */
	public static int toInt(String str){
		int result = 0;
		try { result = str!=null && str!=""?Integer.parseInt(str):0; }
		catch (Exception e) { result = 0; }
		return result;
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideInputMode(Context context) {
		((Activity) context).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	/**
	 * 检测网络连接状态
	 */
//	public static boolean isConnect(Context context){
//		try {
//			ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//			if(con != null){
//				//获取网络连接管理对象
//				NetworkInfo info = con.getActiveNetworkInfo();
//				if(info != null && info.isConnected()){
//					//判断当前网络是否已经连接
//					if(info.getState() == NetworkInfo.State.CONNECTED){
//						return true;
//					}
//				}
//			}
//		} catch (Exception e) {
//			Log.d("error", e.toString());
//		}
//		return false;
//	}

	/**
	 * 得到系统时间 0:显示日期和时间,1:日期和时间加周期，2:简版日期时间和毫秒数
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSysDate(int type) {
		SimpleDateFormat dateFormat = null;
		switch (type) {
			case 0:
				dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				break;
			case 1:
				dateFormat = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm:ss");
				break;
			case 2:
				dateFormat = new SimpleDateFormat("yyMdHmsSS");
				break;
			case 3:
				dateFormat = new SimpleDateFormat("yyyyMMdd");
				break;
			case 4:
				dateFormat = new SimpleDateFormat("yyMdHHmmssSS");
				break;
			case 5:
				dateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
				break;
			case 6:
				dateFormat = new SimpleDateFormat("HH:mm");
				break;
			case 7:
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				break;
			case 8:
				dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				break;
		}
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 字符串不能为空的终极判断 父方法
	 *
	 * @param obj
	 * @return
	 */
	public static String isNULLS(Object obj) {
		return (obj != null && !obj.equals("null")) ? obj.toString() : "";
	}
	public static String isNULLS(JSONObject json, String key) throws JSONException {
		return json.has(key) ? isNULLS(json.getString(key)) : "";
	}

	/**
	 * 说明:(字符串不能为空的终极判断 ) 作者: y1
	 */
	public static String isNULL2(Object obj, String defaVal) {
		String result = isNULLS(obj);
		return result.length() > 0 ? result : defaVal;
	}

	/**
	 * 数字字符串转成Double
	 * 如果Double.parseDouble(null);这个是报空指针异常
	 */
	public static double parseDouble(Object obj) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Double.parseDouble(result) : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	public static double parseDouble(Object obj, double defaVal) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Double.parseDouble(result) : defaVal;
		} catch (NumberFormatException e) {
			return defaVal;
		}
	}

	/**
	 * 数字字符串转成Int
	 * 如果Integer.parseInt(null);这个是报空指针异常
	 */
	public static int parseInt(Object obj) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Integer.parseInt(result) : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 显示系统日期的方法
	 *
	 * @param context
	 * @param dateView
	 * @param viewType
	 *            (0:TextView, 1:EditText)
	 */
	public static void showDateDialog(Activity context, final View dateView,
									  final int viewType) {
		// Date date = new Date();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String currentDate = sdf.format(date);
		String currentDate = getSysDate(7);
		int currentYear = Integer.parseInt(currentDate.substring(0, 4));
		int currentMonth = Integer.parseInt(currentDate.substring(5, 7));
		int currentDay = Integer.parseInt(currentDate.substring(8, 10));

		// 新建日期对话框
		final DatePickerDialog dateDialog = new DatePickerDialog(context, null,
				currentYear, currentMonth - 1, currentDay);
		// 设置对话框标题，默认为当前日期+星期
		dateDialog.setTitle(currentDate);
		// 手动设置按钮
		/*
		 * 确定按钮
		 */
		// dateDialog.setCancelable(false); //不允许使用返回键
		dateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 通过dateDialog.getDatePicker（）获得dialog上的datepicker组件，然后获得日期信息
						DatePicker datePicker = dateDialog.getDatePicker();
						int year = datePicker.getYear();
						int month = datePicker.getMonth() + 1;
						int day = datePicker.getDayOfMonth();
						String strMonth = month + "";
						String strDay = day + "";
						if (month < 10) {
							strMonth = "0" + month;
						}
						if (day < 10) {
							strDay = "0" + day;
						}
						String text = year + "-" + strMonth + "-" + strDay;
						// 给对应的控件赋值
						if (viewType == 0) {
							TextView tv = (TextView) dateView;
							tv.setText(text);
						} else if (viewType == 1) {
							EditText tv = (EditText) dateView;
							tv.setText(text);
						}
						dialog.dismiss();
					}
				});
		/*
		 * 取消按钮
		 */
		dateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dateDialog.show();
	}

	/**
	 * 克隆分为两种：1.浅克隆（地址指向一样，A的数据改变，B也随着改变。），深克隆（互不相干）
	 * list深度克隆
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		List<T> dast = (List<T>) in.readObject();
		return dast;
	}

	/**
	 * 克隆分为两种：1.浅克隆（地址指向一样，A的数据改变，B也随着改变。），深克隆（互不相干）
	 * 对象深度克隆
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T deepCopy(T src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		T dast = (T) in.readObject();
		return dast;
	}

	/**
	 * 关闭的dialog
	 */
	/**
	 * 关闭
	 * @param context
	 * @param message
	 * @return
	 */
	public static void showDialogClose(Activity context, String message) {
		AlertDialog.Builder build = new AlertDialog.Builder(context);
		build.setIcon(R.drawable.caution);
		build.setTitle("系统提示您");
		build.setMessage(message);
		build.setNegativeButton("关闭", null);
		build.setCancelable(false);
		build.show();
	}

	/**
	 * 生成13位唯一条码(根据表的自增长id来生成)
	 */
	public static String randBarcode(String id) {
		int idLen = id.length() + 1; // 这里为什么加1，应为id的前面要加个0占位
		int remainLen = 13 - idLen;
		// 随机生成数字码
		StringBuilder code = new StringBuilder();
		Random rand = new Random();// 随机生成类
		int randInt = 0;
		for (int i = 0; i < remainLen; i++) {
			randInt = rand.nextInt(10);
			code.append(randInt == 0 ? 1 : randInt);
		}

		return code.toString() + "0" + id;
	}

	/**
	 * 提示框
	 * @return
	 */
	public static void showWarnDialog(Activity mContext, String message) {
		AlertDialog.Builder build = new AlertDialog.Builder(mContext);
		build.setIcon(R.drawable.caution);
		build.setTitle("系统提示");
		build.setMessage(message);
		build.setNegativeButton("知道了", null);
		build.setCancelable(false);
		build.show();
	}

	/**
	 * 判断字符串最好一位是否为字母
	 * @param s
	 * @return
	 */
	public static boolean isLetter(String s) {
		if(isNULLS(s).length() == 0) return false;
		int len = s.length()-1; // 得到最好一位字符串的长度
		char c = s.charAt(len);
		int i = (int) c;
		if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) return true;
		else return false;
	}

	/**
	 * 得到客户，如果最好一位为字母，就剔除
	 * @param s
	 * @return
	 */
	public static String getRealCustName(String s) {
		int len = s.length()-1; // 得到最好一位字符串的长度
		char c = s.charAt(len);
		int i = (int) c;
		if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) return s.substring(0, s.length()-1);
		else return s;
	}

}