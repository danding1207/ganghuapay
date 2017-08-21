package com.mqt.ganghuazhifu.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.mqt.ganghuazhifu.BaseActivity;
//import com.mqt.ganghuazhifu.MainActivity;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.activity.MessageActivity;
import com.mqt.ganghuazhifu.activity.MessageCenterActivity;
import com.mqt.ganghuazhifu.activity.TaskActivity;
import com.mqt.ganghuazhifu.bean.BaiduPushData;
import com.mqt.ganghuazhifu.bean.MessageRealm;
import com.mqt.ganghuazhifu.dao.MessageDao;
import com.mqt.ganghuazhifu.dao.MessageDaoImpl;
import com.mqt.ganghuazhifu.utils.DataBaiduPush;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.mqt.ganghuazhifu.utils.ScreenManager;
import com.mqt.ganghuazhifu.utils.Utils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;

/*
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 *onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 *onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调

 * 返回值中的errorCode，解释如下：
 *0 - Success
 *10001 - Network Problem
 *10101  Integrate Check Error
 *30600 - Internal Server Error
 *30601 - Method Not Allowed
 *30602 - Request Params Not Valid
 *30603 - Authentication Failed
 *30604 - Quota Use Up Payment Required
 *30605 -Data Required Not Found
 *30606 - Request Time Expires Timeout
 *30607 - Channel Token Timeout
 *30608 - Bind Relation Not Found
 *30609 - Bind Number Too Many

 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 *
 */
public class PushReceiver extends PushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = PushReceiver.class.getSimpleName();

	/**
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 *
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId,
			String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid=" + appid + " userId=" + userId
				+ " channelId=" + channelId + " requestId=" + requestId;
		Logger.d(TAG, responseString);
		if (errorCode == 0) {
			BaiduPushData baiduPushData = new BaiduPushData(Integer.toString(errorCode), appid, userId, channelId,
					requestId);
			EncryptedPreferencesUtils.setBaiduPushData(baiduPushData);
		} else if (errorCode == 10001) {
			String api_key = Utils.getMetaValue(context, "com.baidu.push.apikey");
			Logger.e(TAG, "api_key--->" + api_key);
			// 百度云推送，向百度注册设备
			PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY, api_key);// 私人开发使用
		}
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 接收透传消息的函数。
	 *
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
		Logger.e(TAG, messageString);
		String topic = null;
		String msg = null;
		String time = null;
		if (!TextUtils.isEmpty(message)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(message);
				if (!customJson.isNull("title")) {
					topic = customJson.getString("title");
				}
				if (!customJson.isNull("description")) {
					msg = customJson.getString("description");
				}
				if (!customJson.isNull("datetime")) {
					time = customJson.getString("datetime");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			showNotifation(context, topic, msg, time);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showNotifation(Context context, String topic, String msg, String time) throws SQLException {
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		long[] pattern = new long[] { 500, 500, 500 };
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(topic).setContentText(msg).setSound(alarmSound)
				.setVibrate(pattern).setAutoCancel(true);


		//数据库操作
		MessageDao messageDao = new MessageDaoImpl(context);
		int strid = messageDao.getAllMessage().size()+1;
		Logger.e(TAG, "id--->" + strid);
		MessageRealm message = new MessageRealm(strid, topic, msg, time, false);
		messageDao.insert(message);
		ShortcutBadger.applyCount(context, messageDao.getUnreadMessageCount());

		//数据库操作
//		if (MainActivity.viewPager != null && MainActivity.ib_pic_right != null) {
//			if (MainActivity.viewPager.getCurrentItem() == 0) {
//				MainActivity.ib_pic_right.setImageResource(R.drawable.message_new);
//			} else {
//				MainActivity.isNewMessage = true;
//			}
//		}
		if (ScreenManager.getScreenManager().isCurrentActivity(MessageCenterActivity.class)) {
			if(ScreenManager.getScreenManager().currentActivity()!=null) {
				((MessageCenterActivity) ScreenManager.getScreenManager().currentActivity()).initData();
			}
		}
		Intent resultIntent = new Intent(context, MessageActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		resultIntent.putExtra(MessageActivity.Companion.getMESSAGEID(), message.getMessage().getId());
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, strid, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);// 当点击消息时就会向系统发送openintent意图
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Random r = new Random();
		int d = r.nextInt();
		Logger.e(TAG, "------d------" + d);
		mNotificationManager.notify(d, mBuilder.build());
		
	}

	/**
	 * 接收通知到达的函数。
	 *
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */

	@Override
	public void onNotificationArrived(Context context, String title, String description, String customContentString) {
		// String notifyString = "onNotificationArrived title=\"" + title + "\"
		// description=\"" + description
		// + "\" customContent=" + customContentString;
		// Log.e(TAG, notifyString);
		// // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		// if (!TextUtils.isEmpty(customContentString)) {
		// JSONObject customJson = null;
		// try {
		// customJson = new JSONObject(customContentString);
		// String myvalue = null;
		// if (!customJson.isNull("mykey")) {
		// myvalue = customJson.getString("mykey");
		// }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	/**
	 * 接收通知点击的函数。
	 *
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		// String notifyString = "通知点击 title=\"" + title + "\" description=\"" +
		// description + "\" customContent="
		// + customContentString;
		// Log.e(TAG, notifyString);
		// // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		// if (!TextUtils.isEmpty(customContentString)) {
		// JSONObject customJson = null;
		// try {
		// customJson = new JSONObject(customContentString);
		// String myvalue = null;
		// if (!customJson.isNull("mykey")) {
		// myvalue = customJson.getString("mykey");
		// }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// DataBaiduPush.setTitle(title);
		// DataBaiduPush.setDescription(description);
		//
		// if (ScreenManager.getScreenManager().isWelcomeActivity()) {
		// // 程序还没有启动
		// updateContent(context, title, description);
		// } else {
		// updateLoginActivity(context, title, description);
		// }
	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
	}

	private void updateContent(Context context, String title, String description) {
		Logger.e(TAG, "updateContent");
		Intent intent = new Intent();
		intent.setClass(context.getApplicationContext(), TaskActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("title", title);
		intent.putExtra("description", description);
		intent.putExtra("push", true);
		context.getApplicationContext().startActivity(intent);
	}

	private void updateLoginActivity(Context context, String title, String description) {
		Logger.e(TAG, "updateLoginActivity");
		DataBaiduPush.setTopActivity((BaseActivity) ScreenManager.getScreenManager().currentActivity());
		new MaterialDialog.Builder(DataBaiduPush.getTopActivity())
				.title(title)
				.content(description)
				.positiveText("确定")
				.show();
	}

}
