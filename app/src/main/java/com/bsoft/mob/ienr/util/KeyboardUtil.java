/**   
 * @Title: KeyboardUtil.java 
 * @Package com.bsoft.mob.ienr.util
 * @Description: 键盘帮助类 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-25 下午2:30:22 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.bsoft.mob.ienr.R;

/**
 * @ClassName: KeyboardUtil
 * @Description: 键盘帮助类
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-11-25 下午2:30:22
 * jian 件大家啊量较大加大技术的解决单身极大时间
 * 大叔大婶大叔大婶多奥术大师大卡死了都
 *
 * 
 */
@Deprecated
public class KeyboardUtil {
	private KeyboardView keyboardView;
	private Keyboard temperature;// 数字键盘

	private EditText ed;

	public KeyboardUtil(View root, Context context) {
		temperature = new Keyboard(context, R.xml.temperature);
		keyboardView = (KeyboardView) root.findViewById(R.id.keyboard_view);
		keyboardView.setKeyboard(temperature);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
//		keyboardView.setPreviewEnabled(false);
		//

	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
				hideKeyboard();
			}else if (primaryCode == Keyboard.KEYCODE_DONE) {
//				ed.requestFocus(View.FOCUS_FORWARD);
//				ed.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
				/*ed.setImeOptions();
				BaseInputConnection mInputConnection = new BaseInputConnection(ed, true);
				mInputConnection.sendKeyEvent(new KeyEvent())*/
			/*	Instrumentation inst = new Instrumentation();
				inst.sendCharacterSync(KeyEvent.KEYCODE_ENTER);*/
//				ed.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
			}else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == 3600) {
				editable.insert(start, "36.");
			} else if (primaryCode == 3700) {
				editable.insert(start, "37.");
			} else if (primaryCode == 3701) {
				editable.insert(start, "☆");
			} else if (primaryCode == 3800) {
				editable.insert(start, "38.");
			} else if (primaryCode == 3801) {
				editable.insert(start, "※");
			} else if (primaryCode == 3802) {
//				no_op
				/*ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						return false;
					}
				});*/
			} else if (primaryCode == 3900) {
				editable.insert(start, "39.");
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};

	public void showKeyboard(EditText edit) {
		this.ed = edit;
		keyboardView.setOnKeyboardActionListener(listener);
		keyboardView.postDelayed(new Runnable() {

			@Override
			public void run() {
				int visibility = keyboardView.getVisibility();
				if (visibility == View.GONE || visibility == View.INVISIBLE) {
					keyboardView.setVisibility(View.VISIBLE);
				}
			}
		}, 400);
	}

	public void hideKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.VISIBLE) {
			keyboardView.setVisibility(View.INVISIBLE);
		}
	}

	public void configEdit(EditText editText) {
		if (editText==null){
			return;
		}
//		editText.setImeActionLabel("回",EditorInfo.IME_ACTION_NEXT);
       /* editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				event.dispatch()
                return false;
            }
        });*/
	}
}
