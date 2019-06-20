package ykk.cb.com.cbwms.comm;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ykk.cb.com.cbwms.R;

/*dialog基础类*/
public abstract class CommonDialog extends Dialog {
    protected Context mContext;
    protected View mContentView;
    private int width = WindowManager.LayoutParams.WRAP_CONTENT,
            height = WindowManager.LayoutParams.WRAP_CONTENT; //默认height为自动填充
    private int windowAnimations = -1;
    private Drawable background; //窗口背景drawable
    private int gravity = Gravity.CENTER;  //默认弹窗位置为屏幕中央

    public CommonDialog(Context context) {
        this(context, R.style.CommonDialog);
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
//        setWidth(getCommonWidth());
        mContentView = LayoutInflater.from(mContext).inflate(bindContentView(), null);
        initView(mContentView);
        setContentView(mContentView);
    }

//    protected int getCommonWidth() {
//        return getScreenWidth() - mContext.getResources().getDimensionPixelSize(R.dimen.common_dialog_margin);
//    }

    public View getContentView() {
        return mContentView;
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /*子类可以重写此方法自定义宽度*/
    public int getWidth() {
        return width;
    }

    /*窗口宽度默认左右边距30dp*/
    public void setWidth(int width) {
        this.width = width;
    }

    /*子类可以重写此方法自定义高度*/
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /*子类可以重写此方法自定义窗口动画*/
    public int getWindowAnimations() {
        return windowAnimations;
    }

    /*对话框弹出动画*/
    public void setWindowAnimations(@StyleRes int windowAnimations) {
        this.windowAnimations = windowAnimations;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.background = background;
    }

    /*重写此方法可以设置窗口background*/
    public Drawable getBackgroundDrawable() {
        return background;
    }

    public void setBackgroundDrawableResource(@DrawableRes int resId) {
        this.background = ContextCompat.getDrawable(mContext, resId);
    }

    /*重写此方法可以设置窗口弹出位置*/
    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            renderWindow(window);
            super.show();
        }
    }

    private void renderWindow(Window window) {
//        mContentView = LayoutInflater.from(mContext).inflate(bindContentView(), null);
//        initView(mContentView);
//        window.setContentView(mContentView);
        if (isFullScreen()) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        } else {
            window.setLayout(getWidth(), getHeight());
        }
        if (getWindowAnimations() != -1) {
            window.setWindowAnimations(getWindowAnimations());
        }
        if (getBackgroundDrawable() != null) {
            window.setBackgroundDrawable(getBackgroundDrawable());
        }
        if (getGravity() != -1) {
            window.setGravity(getGravity());
        }
    }

    protected abstract void initView(View mContentView);

    protected abstract int bindContentView();

    /*是否全屏显示*/
    protected boolean isFullScreen() {
        return false;
    }
}
