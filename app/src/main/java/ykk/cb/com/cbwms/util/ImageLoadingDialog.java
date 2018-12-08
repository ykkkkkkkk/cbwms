package ykk.cb.com.cbwms.util;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import ykk.cb.com.cbwms.R;

public class ImageLoadingDialog extends Dialog{

	public ImageLoadingDialog(Context context) {
		super(context, R.style.ImageloadingDialogStyle);
	}
	
	private ImageLoadingDialog(Context context,int theme){
		super(context,theme);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_imageloading);
	}
}
