package com.mqt.ganghuazhifu.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSONObject;
import com.lqr.picselect.LQRPhotoSelectUtils;
import com.mqt.ganghuazhifu.App;
import com.mqt.ganghuazhifu.MainActivity;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.activity.AccountManagementActivity;
import com.mqt.ganghuazhifu.activity.ChangYongUserActivity;
import com.mqt.ganghuazhifu.activity.ChangePhoneActivity;
import com.mqt.ganghuazhifu.activity.LockPatternActivity;
import com.mqt.ganghuazhifu.activity.PushNumSettingActivity;
import com.mqt.ganghuazhifu.activity.SelectUnityListActivity;
import com.mqt.ganghuazhifu.activity.SetLockPatternActivity;
import com.mqt.ganghuazhifu.activity.VerifyPhoneActivity;
import com.mqt.ganghuazhifu.adapter.FuntionsListAdapter;
import com.mqt.ganghuazhifu.bean.Funtions;
import com.mqt.ganghuazhifu.bean.ResponseHead;
import com.mqt.ganghuazhifu.bean.User;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener;
import com.mqt.ganghuazhifu.utils.Bimp;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.mqt.ganghuazhifu.view.CategoryDialog;
import com.mqt.ganghuazhifu.view.CircleImageView;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class AccountFragment extends BaseFragment implements OnRecyclerViewItemClickListener, LQRPhotoSelectUtils.PhotoSelectListener {

    private RecyclerView list_more_funtions;
    //    private FrissonView frisson_view;
    private TextView tv_time, tv_name;
    private CircleImageView iv_head_pic;
    private ArrayList<Funtions> funtionsList;
    private FuntionsListAdapter adapter;
    private long time;
    private CategoryDialog dialog;
    private boolean isdownLoadFile = false;
    private boolean isinitData = false;
    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;

    public void newInstence() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, null);
        list_more_funtions = (RecyclerView) view.findViewById(R.id.list_more_funtions);
//        frisson_view = (FrissonView) view.findViewById(R.id.frisson_view);
        list_more_funtions.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_more_funtions.setHasFixedSize(true);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        // tv_name = (TextView) view.findViewById(R.id.tv_name);
        iv_head_pic = (CircleImageView) view.findViewById(R.id.iv_head_pic);
        // setViewClick(view, R.id.linearLayout_info);
        // setViewClick(view, R.id.iv_pic);
        setViewClick(view, R.id.ll_head_pic);
        initView();
        return view;
    }

    public void initData() {
        if (isinitData)
            return;
        User user = EncryptedPreferencesUtils.getUser();
        CusFormBody body = HttpRequestParams.INSTANCE.getParamsForAccountInfo(user.getLoginAccount(), "1001000001");
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getProcessQuery(), false, "AccountInfo", body,
                (isError, response, type, error) -> {
                    if (isError) {
                        Logger.e(error.toString());
                        initView();
                    } else {
                        Logger.i(response.toString());
                        JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                        JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                        String ProcessCode = ResponseHead.getString("ProcessCode");
                        if (ProcessCode.equals("0000")) {
                            isinitData = true;
                            String QryResults = ResponseFields.getString("QryResults");
                            User user1 = JSONObject.parseObject(QryResults, User.class);
                            EncryptedPreferencesUtils.setEmail(user1.getEmail());
                            EncryptedPreferencesUtils.setOccupation(user1.getOccupation());
                            EncryptedPreferencesUtils.setIdcardNb(user1.getIdcardNb());
                            EncryptedPreferencesUtils.setRealName(user1.getRealName());
                            EncryptedPreferencesUtils.setGender(user1.getGender());
                            EncryptedPreferencesUtils.setPhoneNb(user1.getPhoneNb());
                            EncryptedPreferencesUtils.setPhoneFlag(user1.getPhoneFlag());
                        }
                        initView();
                    }
                });
    }

    public void initView() {
        User user = EncryptedPreferencesUtils.getUser();
        Logger.e(user.toString());
        funtionsList = new ArrayList<>();
        if (user.getLoginAccount() != null) {
            funtionsList.add(new Funtions("登录名", null, user.getLoginAccount(), R.mipmap.ic_launcher, 2));
        }
        if (user.getLoginTime() != null) {
            if (user.getLoginTime().equals("[]")) {
                if (tv_time != null)
                    tv_time.setText("首次登录");
            } else {
                if (tv_time != null)
                    tv_time.setText("上次登录时间：" + user.getLoginTime().replace("T", " "));
            }
        }
        if (user.getPhoneNb() != null) {
            if (user.getPhoneNb().length() == 11) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(user.getPhoneNb().substring(0, 3));
                buffer.append("****");
                buffer.append(user.getPhoneNb().substring(7, 11));
                funtionsList.add(new Funtions("手机", null, buffer.toString(), R.mipmap.ic_launcher, 1));
            } else {
                funtionsList.add(new Funtions("手机", null, user.getPhoneNb(), R.mipmap.ic_launcher, 1));
            }
        } else {
            funtionsList.add(new Funtions("手机", null, null, R.mipmap.ic_launcher, 2));
        }

        funtionsList.add(new Funtions("推送户号设置", null, null, R.mipmap.ic_launcher, 1));

        if (user != null && user.getPayeeNm() != null && !user.getPayeeNm().equals("[]")) {
            funtionsList.add(new Funtions("常用缴费单位", null, user.getPayeeNm(), R.mipmap.ic_launcher, 2));
        } else {
            funtionsList.add(new Funtions("常用缴费单位", null, "未设置", R.mipmap.ic_launcher, 1));
        }

        if ("0".equals(user.getFunction1())) {
            funtionsList.add(new Funtions("常用联系人", "new", null, R.mipmap.ic_launcher, 1));
        } else {
            funtionsList.add(new Funtions("常用联系人", null, null, R.mipmap.ic_launcher, 1));
        }

//        funtionsList.add(new Funtions("我的发票", null, null, R.mipmap.ic_launcher, 1));

        if (user != null && user.getEmail() != null && !user.getEmail().equals("[]")) {
            funtionsList.add(new Funtions("邮箱", null, user.getEmail(), R.mipmap.ic_launcher, 1));
        } else {
            funtionsList.add(new Funtions("邮箱", null, "未设置", R.mipmap.ic_launcher, 1));
        }
//        funtionsList.add(new Funtions("安保问题管理", null, null, R.mipmap.ic_launcher, 1));
        funtionsList.add(new Funtions("登录密码修改", null, null, R.mipmap.ic_launcher, 1));
        if (user != null && user.getGesturePwd() != null && !TextUtils.isEmpty(user.getGesturePwd())) {
            if (user.getGesturePwd().equals("9DD4E461268C8034F5C8564E155C67A6"))
                funtionsList.add(new Funtions("手势密码管理", null, "已失效", R.mipmap.ic_launcher, 1));
            else
                funtionsList.add(new Funtions("手势密码管理", null, "已设置", R.mipmap.ic_launcher, 1));
        } else {
            funtionsList.add(new Funtions("手势密码管理", null, "未设置", R.mipmap.ic_launcher, 1));
        }

        adapter = new FuntionsListAdapter(getActivity(), 1);

        adapter.updateList(funtionsList);
        adapter.setOnRecyclerViewItemClickListener(this);
        list_more_funtions.setAdapter(adapter);

        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(getActivity(), this, false);

        dialog = new CategoryDialog(getActivity(), type -> {
            String sdcardState = Environment.getExternalStorageState();
            switch (type) {
                case CategoryDialog.CAMERATYPE:
                    if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                        // 调用相机进行拍照
                        mLqrPhotoSelectUtils.takePhoto();
                    } else {
                        ToastUtil.Companion.toastError("sdcard已拔出，不能拍摄照片");
                    }
                    break;
                case CategoryDialog.PHOTOTYPE:
                    if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                        // 调用图库选取图片
                        mLqrPhotoSelectUtils.selectPhoto();
                    } else {
                        ToastUtil.Companion.toastError("sdcard已拔出，不能拍摄照片");
                    }
                    break;
            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downLoadFile();
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.ll_head_pic:
                dialog.showDialog();
                break;
        }
    }

    protected void updataFuntion() {
        User user = EncryptedPreferencesUtils.getUser();
        CusFormBody body = HttpRequestParams.INSTANCE.getParamsForSetFuntion(user.getLoginAccount(), "1");
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getUserUpdate(), false, "SetFuntion", body,
                (isError, response, type, error) -> {
                    if (isError) {
                        Logger.e(error.toString());
                    } else {
                        Logger.i(response.toString());
                        JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                        String ProcessCode = ResponseHead.getString("ProcessCode");
                        String ProcessDes = ResponseHead.getString("ProcessDes");
                        if (ProcessCode.equals("0000")) {
                            MainActivity.bottom_navigation.setNotification("", 2);
                            EncryptedPreferencesUtils.setFunction1("1");
                            initView();
                        } else {
                            ToastUtil.Companion.toastError(ProcessDes);
                        }
                    }
                });
    }

    /**
     * 以下为拍照功能
     */
    public static Bitmap headerpic;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        App.Companion.setAppCount(0);
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
        Logger.t("LockPattern").i("requestCode: " + requestCode);
        Logger.t("LockPattern").i("resultCode: " + resultCode);
        Logger.t("LockPattern").i("data: " + data);

    }

    private void upLoadFile() {
        CusFormBody body = HttpRequestParams.INSTANCE.getParamsForImageHandle(bitmapToBase64(headerpic), "10");
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getImageHandle(), true, "imageHandle", body,
                (isError, response, type, error) -> {
                    if (isError) {
                        Logger.e(error.toString());
                    } else {
                        Logger.i(response.toString());
                        JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                        String Response = response.getString("ResponseHead");
                        if (Response != null) {
                            ResponseHead head = JSONObject.parseObject(Response, ResponseHead.class);
                            if (head != null && head.ProcessCode.equals("0000")) {
                                iv_head_pic.setImageBitmap(headerpic);
                            } else {
                                if (head != null && head.ProcessDes != null) {
                                    ToastUtil.Companion.toastError(head.ProcessDes);
                                }
                            }
                        }
                    }
                });
    }

    public void downLoadFile() {
        if (isdownLoadFile)
            return;
        CusFormBody body = HttpRequestParams.INSTANCE.getParamsForImageHandle(null, "11");
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getImageHandle(), false, "imageHandle", body,
                (isError, response, type, error) -> {
                    if (isError) {
                        Logger.e(error.toString());
                    } else {
                        JSONObject ResponseFields = response.getJSONObject("ResponseFields");
                        String Response = response.getString("ResponseHead");
                        if (Response != null && ResponseFields != null) {
                            ResponseHead head = JSONObject.parseObject(Response, ResponseHead.class);
                            if (head != null && head.ProcessCode.equals("0000")) {
                                String ImageBase64 = ResponseFields.getString("ImageBase64");
                                if (ImageBase64 != null) {
                                    headerpic = base64ToBitmap(ImageBase64);
                                    iv_head_pic.setImageBitmap(headerpic);
//                                    frisson_view.setBitmap(headerpic);
                                    isdownLoadFile = true;
                                }
                            } else {
                                if (head != null && head.ProcessDes != null) {
                                    ToastUtil.Companion.toastError(head.ProcessDes);
                                }
                            }
                        }
                    }
                });
    }

    /**
     * bitmap转为base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (time == 0 || System.currentTimeMillis() - time > 1000) {
            User user = EncryptedPreferencesUtils.getUser();
            if (user.getPhoneNb() != null) {
                Intent intent;
                switch (position) {
                    case 0:// 个人资料修改
                        startActivity(new Intent(getActivity(), AccountManagementActivity.class));
                        break;
                    case 1:
                        intent = new Intent(getActivity(), ChangePhoneActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), PushNumSettingActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        if (user != null && user.getPayeeNm() != null && !user.getPayeeNm().equals("[]")) {
                            ToastUtil.Companion.toastWarning("已设置，不能修改!");
                        } else {
                            initUnityDialog();
                        }
                        break;
                    case 4:
                        intent = new Intent(getActivity(), ChangYongUserActivity.class);
                        startActivity(intent);
                        if ("0".equals(user.getFunction1())) {
                            updataFuntion();
                        }
                        break;
//                    case 5:
//                        FapiaoListActivity.Companion.startActivity(getActivity());
//                        break;
                    case 5:
                        ToastUtil.Companion.toastWarning("请登录官网修改!");
                        break;
                    case 6:
                        VerifyPhoneActivity.Companion.startActivity(getActivity(), 4, user.getPhoneNb());
                        break;
                    case 7:
                        if (user != null && user.getGesturePwd() != null && !TextUtils.isEmpty(user.getGesturePwd())) {
                            if (user.getGesturePwd().equals("9DD4E461268C8034F5C8564E155C67A6")) {
                                intent = new Intent(getActivity(), LockPatternActivity.class);
                                intent.putExtra("TYPE", 2);
                                startActivity(intent);
                            } else
                                startActivity(new Intent(getActivity(), SetLockPatternActivity.class));
                        } else {
                            intent = new Intent(getActivity(), LockPatternActivity.class);
                            intent.putExtra("TYPE", 2);
                            startActivity(intent);
                        }
                        break;
//                    case 8://安保问题管理
//                        VerifyPasswordActivity.Companion.startActivity(getActivity(), 1);
//                        break;
                }
            } else {
                ToastUtil.Companion.toastWarning("获取用户信息失败，正在重新获取!");
                initData();
            }
            time = System.currentTimeMillis();
        }
    }

    private void initUnityDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("提醒")
                .content("请您选择常用缴费单位！")
                .onPositive((dialog1, which) -> {
                    Intent intent = new Intent(getActivity(), SelectUnityListActivity.class);
                    intent.putExtra("TYPE", 2);
                    startActivity(intent);
                })
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("确定");
        User user = EncryptedPreferencesUtils.getUser();
        if (user != null && user.getAscriptionFlag() != null && "11".equals(user.getAscriptionFlag())) {
            builder.show();
        }
    }

    @Override
    public void onFinish(File outputFile, Uri outputUri) {
        Logger.t("LockPattern").i("图片选择成功");
        Logger.t("LockPattern").i("outputFile：" + outputFile.getAbsolutePath());
        Logger.t("LockPattern").i("outputUri：" + outputUri.getPath());

        File compressedImage = new Compressor.Builder(getActivity())
                .setMaxWidth(160)
                .setMaxHeight(160)
                .setQuality(55)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(outputFile);

        if (compressedImage != null) {
            Logger.t("LockPattern").i("图片压缩成功");
            Logger.t("LockPattern").i("file.size--->" + compressedImage.getTotalSpace());
            headerpic = Bimp.getLoacalBitmap(compressedImage);
            upLoadFile();
        }

    }
}
