package com.example.john.finalproject.Life.express;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		initViews();
		initListeners();
		initData();
	}

    // 关闭软键盘
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

	Toast mToast;
	public void ShowToast(String text) {
		if (!TextUtils.isEmpty(text)) {
            try{
                if (mToast == null)
                    mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                else mToast.setText(text);
                mToast.show();
            }catch (Exception e){
                e.printStackTrace();
            }
		}
	}

	public abstract void setContentView();

	public abstract void initViews();

	public abstract void initListeners();

	public abstract void initData();
}
