package com.mqt.ganghuazhifu.listener;

import android.view.View;
import android.widget.LinearLayout;

/**
 * Ϊ�˽���� ScrollView����Ƕ��GridView ListView
 * ��Ӧ�ĳ����¼�
 * @author yang.lei
 * @version 1.0.0
 * @date 2014-12-8
 */
public interface OnItemLongClickListener {
	void onItemLongClick(LinearLayout parent, View view, int position, long id);
}