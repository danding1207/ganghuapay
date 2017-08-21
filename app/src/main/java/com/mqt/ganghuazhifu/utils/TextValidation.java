package com.mqt.ganghuazhifu.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.mqt.ganghuazhifu.App;

public class TextValidation {

	public static void setRegularValidation(final Activity context, final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String name = editText.getText().toString();

				if (before == 0) {
					if (!RegularExpressionUtils.regularExpression_Eng_num_char(name)) {
						ToastUtil.Companion.toastWarning("输入不合法,请输入英文字母，数字和字符!");
						editText.setText(name.substring(0, name.length() - count));
						CharSequence text = editText.getText();
						if (text instanceof Spannable) {
							Spannable spanText = (Spannable) text;
							Selection.setSelection(spanText, text.length());
						}
					}
					if (name.length() > 30) {
						ToastUtil.Companion.toastWarning("密码长度不大于30!");
						editText.setText(name.substring(0, 29));
						CharSequence text = editText.getText();
						if (text instanceof Spannable) {
							Spannable spanText = (Spannable) text;
							Selection.setSelection(spanText, text.length());
						}
					}
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public static void setRegularValidationName(final Activity context, final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String name = editText.getText().toString();

				if (before == 0) {

					if (start == 0) {
						if (!RegularExpressionUtils.regularExpression_Eng(name)) {
							ToastUtil.Companion.toastWarning("输入不合法,首位必须为英文字母!");
							editText.setText(name.substring(0, name.length() - count));
							CharSequence text = editText.getText();
							if (text instanceof Spannable) {
								Spannable spanText = (Spannable) text;
								Selection.setSelection(spanText, text.length());
							}
						}
					} else {
						if (!RegularExpressionUtils.regularExpression_Eng_num_char(name)) {
							ToastUtil.Companion.toastWarning("输入不合法,请输入英文字母，数字和字符!");
							editText.setText(name.substring(0, name.length() - count));
							CharSequence text = editText.getText();
							if (text instanceof Spannable) {
								Spannable spanText = (Spannable) text;
								Selection.setSelection(spanText, text.length());
							}
						}
						if (name.length() > 30) {
							ToastUtil.Companion.toastWarning("用户名长度不大于30!");
							editText.setText(name.substring(0, 29));
							CharSequence text = editText.getText();
							if (text instanceof Spannable) {
								Spannable spanText = (Spannable) text;
								Selection.setSelection(spanText, text.length());
							}
						}
					}
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public static void setRegularValidationChina(final Activity context, final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String name = editText.getText().toString();
				if (before == 0) {
					if (!RegularExpressionUtils.regularExpression_Chinese(name)) {
						ToastUtil.Companion.toastWarning("输入不合法,请输入中文!");
						editText.setText(name.substring(0, name.length() - count));
						CharSequence text = editText.getText();
						if (text instanceof Spannable) {
							Spannable spanText = (Spannable) text;
							Selection.setSelection(spanText, text.length());
						}
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public static void setRegularValidationIDCard(final Activity context, final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String name = editText.getText().toString();
				if (before == 0) {
					if (!RegularExpressionUtils.regularExpression_IdCard(name)) {
						ToastUtil.Companion.toastWarning("输入不合法,请输入数字或x!");
						editText.setText(name.substring(0, name.length() - count));
						CharSequence text = editText.getText();
						if (text instanceof Spannable) {
							Spannable spanText = (Spannable) text;
							Selection.setSelection(spanText, text.length());
						}
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public static void setRegularValidationNumberDecimal(final Context context, final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			private String beforeText;
			private int last = 2;
			private boolean flag = false;
			private boolean flag2 = false;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString().equals(".")) {
					editText.setText("0.");
					flag = true;
					flag2 = true;
					CharSequence text = editText.getText();
					if (text instanceof Spannable) {
						Spannable spanText = (Spannable) text;
						Selection.setSelection(spanText, text.length());
					}
				}
				if (before == 0 && TextUtils.isEmpty(beforeText) && s.toString().equals("0")) {
					flag2 = true;
				}
				if (before == 0 && start == 1) {
					if (!beforeText.endsWith(".") && s.toString().endsWith(".")) {

					} else {

						if (flag2) {
							String ss = s.toString().substring(1, s.toString().length());
							if (!ss.equals("0")) {
								flag2 = false;
							}
							editText.setText(ss);
							CharSequence text = editText.getText();
							if (text instanceof Spannable) {
								Spannable spanText = (Spannable) text;
								Selection.setSelection(spanText, text.length());
							}

						}

					}

				}

				if (before == 0 && !beforeText.endsWith(".") && s.toString().endsWith(".")) {
					flag = true;
				}

				if (flag && before == 1) {
					last++;
				}

				if (flag && before == 0) {
					if (last >= 0) {
						last--;
					} else {
						editText.setText(beforeText);
						CharSequence text = editText.getText();
						if (text instanceof Spannable) {
							Spannable spanText = (Spannable) text;
							Selection.setSelection(spanText, text.length());
						}
					}
				}

				if (before == 1 && beforeText.endsWith(".") && !s.toString().endsWith(".")) {
					flag = false;
					last = 2;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				beforeText = s.toString();

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public static void setOnFocusChangeListener(OnFocusChangeListener listener, final EditText editText) {
		editText.setOnFocusChangeListener(listener);
	}

}
