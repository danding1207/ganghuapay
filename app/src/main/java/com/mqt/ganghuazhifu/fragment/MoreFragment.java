package com.mqt.ganghuazhifu.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mqt.ganghuazhifu.App;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.activity.AboutUsActivity;
import com.mqt.ganghuazhifu.activity.ConnectUsActivity;
import com.mqt.ganghuazhifu.activity.FeedBackCenterActivity;
import com.mqt.ganghuazhifu.activity.TwoWeiMaActivity;
import com.mqt.ganghuazhifu.activity.UsingHelpActivity;
import com.mqt.ganghuazhifu.activity.WelcomeActivity;
import com.mqt.ganghuazhifu.adapter.FuntionsListAdapter;
import com.mqt.ganghuazhifu.bean.Funtions;
import com.mqt.ganghuazhifu.bean.ResponseHead;
import com.mqt.ganghuazhifu.bean.User;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener;
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener;
import com.mqt.ganghuazhifu.utils.DataBaiduPush;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.mqt.ganghuazhifu.utils.ScreenManager;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;
import com.mqt.ganghuazhifu.view.SwitchButton;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class MoreFragment extends BaseFragment implements OnRecyclerViewItemClickListener, SwitchButton.SwitchButtonStateChangedListener {

    private RecyclerView list_more_funtions;
    private ArrayList<Funtions> funtionsList;
    private FuntionsListAdapter adapter;
    private String version;
    private long time;
    private SwitchButton switchButton;

    public Activity activity;

    public void newInstence(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        setViewClick(view, R.id.linearLayout_version);
        setViewClick(view, R.id.button_log_out);
        list_more_funtions = (RecyclerView) view.findViewById(R.id.list_more_funtions);
        list_more_funtions.setLayoutManager(new LinearLayoutManager(getActivity()));
        funtionsList = new ArrayList<>();
        funtionsList.add(new Funtions("关于我们", null, null, 0, 1));
        funtionsList.add(new Funtions("使用帮助", null, null, 0, 1));
        funtionsList.add(new Funtions("意见反馈", null, null, 0, 1));
        funtionsList.add(new Funtions("联系我们", null, null, 0, 1));
        if (!App.Companion.getPhoneInfo().getAppVersion().equals("NULL")) {
            version = App.Companion.getPhoneInfo().getAppVersion();
            if (WelcomeActivity.Companion.isNewVersion()) {
                funtionsList.add(new Funtions("版本检查", "new", "V " + App.Companion.getPhoneInfo().getAppVersion(), 0, 2));
            } else {
                funtionsList.add(new Funtions("版本检查", null, "V " + App.Companion.getPhoneInfo().getAppVersion(), 0, 2));
            }
        }

        //
//        funtionsList.add(new Funtions("移动客服", null, null, 0, 1));

        User user = EncryptedPreferencesUtils.getUser();
        // 员工才会显示
        if ("10".equals(user.getEmployeeFlag())) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    UnitConversionUtils.dipTopx(getActivity(), 338));
            params.topMargin = UnitConversionUtils.dipTopx(getActivity(), 12);
            list_more_funtions.setLayoutParams(params);
            funtionsList.add(new Funtions("员工二维码", null, null, 0, 1));
        }

        adapter = new FuntionsListAdapter(getActivity(), 1);

        list_more_funtions.setHasFixedSize(true);
        adapter.updateList(funtionsList);
        adapter.setOnRecyclerViewItemClickListener(this);
        list_more_funtions.setAdapter(adapter);
        switchButton = (SwitchButton) view.findViewById(R.id.switchButton);
        if (DataBaiduPush.getPushStatus() != null) {
            if (DataBaiduPush.getPushStatus().endsWith("0")) {
                switchButton.setState(true);
            } else if (DataBaiduPush.getPushStatus().endsWith("1")) {
                switchButton.setState(false);
            }
        }
        switchButton.setOnSwitchButtonStateChangedListener(this);
        return view;
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.button_log_out:
                new MaterialDialog.Builder(getActivity())
                        .title("提醒")
                        .content("确定要退出登录？")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //startActivity(new Intent(getActivity(), LoginActivity.class));
//                            ScreenManager.getScreenManager().popAllActivity();
                                getActivity().finish();
                            }
                        })
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();
                break;
        }
    }

    private void checkVersion() {
        CusFormBody body = HttpRequestParams.INSTANCE.getParamsForAdvertisement("01", "01");
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getProcessQuery(), false, "Advertisement", body,
                new OnHttpRequestListener() {
                    @Override
                    public void OnCompleted(Boolean isError, JSONObject response, int type, IOException error) {
                        if (isError) {
                            Logger.e(error.toString());
                        } else {
                            Logger.d(response.toString());
                            JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                            JSONArray ResponseFields = response.getJSONArray("ResponseFields");
                            String ProcessCode = ResponseHead.getString("ProcessCode");
                            if (ProcessCode.equals("0000") && ResponseFields != null) {
                                JSONObject QryResults1 = ResponseFields.getJSONObject(0);
                                JSONObject QryResults2 = ResponseFields.getJSONObject(1);
                                final String appPath = QryResults2.getString("comval");
                                String comval = QryResults1.getString("comval");
                                comval = comval.substring(1, comval.length());
                                Logger.i(comval);
                                String[] ss = comval.split("\\.");
                                String[] sss = version.split("\\.");
                                Logger.i("length--->" + ss.length);

                                int a = Integer.parseInt(ss[0]);
                                int b = Integer.parseInt(ss[1]);
                                int c = Integer.parseInt(ss[2]);
                                int d = Integer.parseInt(ss[3]);
                                int aa = Integer.parseInt(sss[0]);
                                int bb = Integer.parseInt(sss[1]);
                                int cc = Integer.parseInt(sss[2]);
                                int dd = Integer.parseInt(sss[3]);

                                if (a > aa) {
                                    Logger.i("新版本：" + comval + "当前版本：" + version + "  需要强制更新");
                                    mandatoryUpdate(comval, appPath);
                                } else if (a >= aa && b > bb) {
                                    Logger.i("新版本：" + comval + "当前版本：" + version + "  需要强制更新");
                                    mandatoryUpdate(comval, appPath);
                                } else if (a >= aa && b >= bb && c > cc) {
                                    Logger.i("新版本：" + comval + "当前版本：" + version + "  需要强制更新");
                                    mandatoryUpdate(comval, appPath);
                                } else if (a >= aa && b >= bb && c >= cc && d > dd) {
                                    Logger.i("新版本：" + comval + "当前版本：" + version + "  需要提醒更新");
                                    proposedUpdate(comval, appPath);
                                } else {
                                    Logger.i("新版本：" + comval + "当前版本：" + version + "  不需要更新");
                                    ToastUtil.Companion.toastInfo("已经是最新版本!");
                                }

                            }
                        }
                    }
                });
    }

    /**
     * 提交是否打开推送功能
     */
    private void submit() {
        User user = EncryptedPreferencesUtils.getUser();
        CusFormBody body = HttpRequestParams.INSTANCE.getPushStatus(user.getLoginAccount(), DataBaiduPush.getPushStatus());
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getPushNotice(), false, "PushNotice", body,
                new OnHttpRequestListener() {
                    @Override
                    public void OnCompleted(Boolean isError, JSONObject response, int type, IOException error) {
                        if (isError) {
                            Logger.e(error.toString());
                        } else {
                            String Response = response.getString("ResponseHead");
                            if (Response != null) {
                                ResponseHead head = JSONObject.parseObject(Response, ResponseHead.class);
                                if (head != null && head.ProcessCode.equals("0000")) {
                                    if (DataBaiduPush.getPushStatus().endsWith("0")) {
                                        ToastUtil.Companion.toastInfo("启用推送功能!");
                                    } else if (DataBaiduPush.getPushStatus().endsWith("1")) {
                                        ToastUtil.Companion.toastInfo("关闭推送功能!");
                                    }
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 强制更新
     */
    private void mandatoryUpdate(final String newVersion, final String appPath) {
        // 强制更新
        new MaterialDialog.Builder(getActivity())
                .title("提醒")
                .content("您当前版本过低，请您更新版本" + newVersion + "，享受新的体验。")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String networkType = HttpRequest.Companion.getNetworkType(getActivity());
                        if (networkType.equals("WIFI")) {
                            WelcomeActivity.Companion.download(getActivity(), appPath, newVersion);
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("提醒")
                                    .content("当前网络不是wifi，是否继续更新")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            WelcomeActivity.Companion.download(getActivity(), appPath, newVersion);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            System.exit(0);
                                            ScreenManager.getScreenManager().popAllActivity();
                                            finish();
                                        }
                                    })
                                    .cancelable(false).canceledOnTouchOutside(false)
                                    .positiveText("立即更新").negativeText("退出应用").show();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        System.exit(0);
                        ScreenManager.getScreenManager().popAllActivity();
                        finish();
                    }
                })
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("立即更新")
                .negativeText("退出应用")
                .show();
    }

    /**
     * 强制更新
     */
    private void proposedUpdate(final String newVersion, final String appPath) {
// 提醒更新
        new MaterialDialog.Builder(getActivity())
                .title("提醒")
                .content("有新的版本，更新版本" + newVersion)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String networkType = HttpRequest.Companion.getNetworkType(getActivity());
                        if (networkType.equals("WIFI")) {
                            WelcomeActivity.Companion.download(getActivity(), appPath, newVersion);
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("提醒")
                                    .content("当前网络不是wifi，是否继续更新")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            WelcomeActivity.Companion.download(getActivity(), appPath, newVersion);
                                        }
                                    })
                                    .cancelable(false).canceledOnTouchOutside(false)
                                    .positiveText("立即更新").negativeText("下次更新").show();
                        }
                    }
                })
                .positiveText("立即更新")
                .negativeText("下次更新")
                .show();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (time == 0 || System.currentTimeMillis() - time > 1000) {
            switch (position) {
                case 0:
                    startActivity(new Intent(getActivity(), AboutUsActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getActivity(), UsingHelpActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(getActivity(), FeedBackCenterActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(getActivity(), ConnectUsActivity.class));
                    break;
                case 4:
                    checkVersion();
                    break;
                case 5:
                    startActivity(new Intent(getActivity(), TwoWeiMaActivity.class));
                    break;
            }
            time = System.currentTimeMillis();
        }
    }


    @Override
    public void SwitchButtonStateChanged(boolean isOpen) {
        if (isOpen) {
            if (DataBaiduPush.getPushStatus().endsWith("1")) {
                DataBaiduPush.setPushStatus("10");
                submit();
            }
        } else {
            if (DataBaiduPush.getPushStatus().endsWith("0")) {
                DataBaiduPush.setPushStatus("11");
                submit();
            }
        }
    }
}
