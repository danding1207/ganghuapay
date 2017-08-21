package com.mqt.ganghuazhifu.view;

import java.util.Date;
import java.util.Random;
import com.mqt.ganghuazhifu.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class PasswordsKeyBoardDialog extends Dialog implements View.OnClickListener, OnLongClickListener {

	private Window window = null;
	private PasswordsKeyBoardDialog myDialog;
	private String[] charts_x = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "x" };
	private String[] charts = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };

	private TextView tv_passwords, tv_determine;
	private TextView tv_number_1, tv_number_2, tv_number_3;
	private TextView tv_number_4, tv_number_5, tv_number_6;
	private TextView tv_number_7, tv_number_8, tv_number_9;
	private TextView tv_number_10, tv_number_11;
	private ImageView tv_number_12;

	private TextView editView;// 区分控件用
	private Activity activity;
	private OnPasswordsCompletedListener listener;

	private int mode = 1;// 1，没有x，纯数字；2，有x，身份证专用；3，银行卡专用（每4位有空格）。

	public PasswordsKeyBoardDialog(Activity context, OnPasswordsCompletedListener listener) {
		super(context, R.style.dialog_floating);
		myDialog = this;
		this.listener = listener;
		this.activity = context;
		View views = LayoutInflater.from(context).inflate(R.layout.passwords_keyboard_dialog, null);
		tv_passwords = (TextView) views.findViewById(R.id.tv_passwords);
		tv_determine = (TextView) views.findViewById(R.id.tv_determine);
		tv_number_1 = (TextView) views.findViewById(R.id.tv_number_1);
		tv_number_2 = (TextView) views.findViewById(R.id.tv_number_2);
		tv_number_3 = (TextView) views.findViewById(R.id.tv_number_3);
		tv_number_4 = (TextView) views.findViewById(R.id.tv_number_4);
		tv_number_5 = (TextView) views.findViewById(R.id.tv_number_5);
		tv_number_6 = (TextView) views.findViewById(R.id.tv_number_6);
		tv_number_7 = (TextView) views.findViewById(R.id.tv_number_7);
		tv_number_8 = (TextView) views.findViewById(R.id.tv_number_8);
		tv_number_9 = (TextView) views.findViewById(R.id.tv_number_9);
		tv_number_10 = (TextView) views.findViewById(R.id.tv_number_10);
		tv_number_11 = (TextView) views.findViewById(R.id.tv_number_11);
		tv_number_12 = (ImageView) views.findViewById(R.id.tv_number_12);
		tv_determine.setOnClickListener(this);
		tv_number_1.setOnClickListener(this);
		tv_number_2.setOnClickListener(this);
		tv_number_3.setOnClickListener(this);
		tv_number_4.setOnClickListener(this);
		tv_number_5.setOnClickListener(this);
		tv_number_6.setOnClickListener(this);
		tv_number_7.setOnClickListener(this);
		tv_number_8.setOnClickListener(this);
		tv_number_9.setOnClickListener(this);
		tv_number_10.setOnClickListener(this);
		tv_number_11.setOnClickListener(this);
		tv_number_12.setOnClickListener(this);
		tv_number_12.setOnLongClickListener(this);
		this.setContentView(views);
		windowDeploy(context);
	}

	private void initChars() {
		switch (mode) {
		case 3:// 3，银行卡专用（每4位有空格）；
		case 1:// 1，没有x，纯数字；
			charts = doSort(charts);
			tv_number_1.setText(charts[0]);
			tv_number_2.setText(charts[1]);
			tv_number_3.setText(charts[2]);
			tv_number_4.setText(charts[3]);
			tv_number_5.setText(charts[4]);
			tv_number_6.setText(charts[5]);
			tv_number_7.setText(charts[6]);
			tv_number_8.setText(charts[7]);
			tv_number_9.setText(charts[8]);
			// tv_number_10.setText(charts[9]);
			tv_number_11.setText(charts[9]);
			tv_number_10.setVisibility(View.INVISIBLE);
			break;
		case 2:// 2，有x，身份证专用。
			charts_x = doSort(charts_x);
			tv_number_1.setText(charts_x[0]);
			tv_number_2.setText(charts_x[1]);
			tv_number_3.setText(charts_x[2]);
			tv_number_4.setText(charts_x[3]);
			tv_number_5.setText(charts_x[4]);
			tv_number_6.setText(charts_x[5]);
			tv_number_7.setText(charts_x[6]);
			tv_number_8.setText(charts_x[7]);
			tv_number_9.setText(charts_x[8]);
			tv_number_10.setText(charts_x[9]);
			tv_number_11.setText(charts_x[10]);
			tv_number_10.setVisibility(View.VISIBLE);
			break;
		}

	}

	public PasswordsKeyBoardDialog(Activity context) {
		this(context, null);
	}

	// 设置窗口显示
	public void windowDeploy(Context context) {
		window = getWindow(); // 得到对话框
		window.setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		wl.width = dm.widthPixels;
		wl.height = LayoutParams.WRAP_CONTENT;
		wl.gravity = Gravity.BOTTOM; // 设置重力
		window.setAttributes(wl);
	}

	public interface OnPasswordsCompletedListener {
		void OnPasswordsCompleted(TextView editView, String passwords);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_determine:
			if (listener != null) {
				listener.OnPasswordsCompleted(editView, tv_passwords.getText().toString().trim());
			}
			dismiss();
			break;
		case R.id.tv_number_1:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[0]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[0]);
				break;
			}
			break;
		case R.id.tv_number_2:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[1]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[1]);
				break;
			}
			break;
		case R.id.tv_number_3:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[2]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[2]);
				break;
			}
			break;
		case R.id.tv_number_4:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[3]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[3]);
				break;
			}
			break;
		case R.id.tv_number_5:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[4]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[4]);
				break;
			}
			break;
		case R.id.tv_number_6:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[5]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[5]);
				break;
			}
			break;
		case R.id.tv_number_7:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[6]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[6]);
				break;
			}
			break;
		case R.id.tv_number_8:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[7]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[7]);
				break;
			}
			break;
		case R.id.tv_number_9:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[8]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[8]);
				break;
			}
			break;
		case R.id.tv_number_10:
			switch (mode) {
			case 1:// 1，没有x，纯数字；
				// tv_passwords.append(charts[9]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[9]);
				break;
			}
			break;
		case R.id.tv_number_11:
			switch (mode) {
			case 3:// 1，没有x，纯数字；
				if (tv_passwords.getText().toString().trim().length() == 4
						|| tv_passwords.getText().toString().trim().length() == 9
						|| tv_passwords.getText().toString().trim().length() == 14
						|| tv_passwords.getText().toString().trim().length() == 19) {
					tv_passwords.append(" ");
				}
			case 1:// 1，没有x，纯数字；
				tv_passwords.append(charts[9]);
				break;
			case 2:// 2，有x，身份证专用。
				tv_passwords.append(charts_x[10]);
				break;
			}
			break;
		case R.id.tv_number_12:
			if (tv_passwords.getText().toString().length() > 0) {

				if (mode == 3 && (tv_passwords.getText().toString().trim().length() == 6
						|| tv_passwords.getText().toString().trim().length() == 11
						|| tv_passwords.getText().toString().trim().length() == 16
						|| tv_passwords.getText().toString().trim().length() == 21)) {

					tv_passwords.setText(tv_passwords.getText().toString().substring(0,
							tv_passwords.getText().toString().length() - 2));

				} else {
					tv_passwords.setText(tv_passwords.getText().toString().substring(0,
							tv_passwords.getText().toString().length() - 1));
				}

			}
			break;
		}
	}

	public String[] doSort(String[] sArr) {
		String[] tempArr = new String[sArr.length];
		Random random = new Random(new Date().getTime());
		int randomIndex = -1;
		for (int i = 0; i < tempArr.length; i++) {
			while (tempArr[i] == null) {
				randomIndex = random.nextInt(sArr.length);
				if (sArr[randomIndex] != null) {
					tempArr[i] = sArr[randomIndex];
					sArr[randomIndex] = null;
				}
			}
		}
		return tempArr;
	}

	public void showDialog(TextView editView, int maxLength, String older, int mode) {
		if (!myDialog.isShowing()) {
			this.editView = editView;
			this.mode = mode;
			initChars();
			InputFilter[] fArray = new InputFilter[1];
			fArray[0] = new InputFilter.LengthFilter(maxLength);
			tv_passwords.setFilters(fArray);
			tv_passwords.setText(older);
			myDialog.show();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.tv_number_12:
			if (tv_passwords.getText().toString().length() > 0) {
				tv_passwords.setText("");
			}
			return true;
		}
		return false;
	}

}
