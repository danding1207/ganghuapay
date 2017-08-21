package com.mqt.ganghuazhifu.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.mqt.ganghuazhifu.App;
import com.mqt.ganghuazhifu.MainActivity;
import com.mqt.ganghuazhifu.R;
import com.mqt.ganghuazhifu.activity.RecordDetailActivity;
import com.mqt.ganghuazhifu.activity.WelcomeActivity;
import com.mqt.ganghuazhifu.adapter.RecordsListAdapter;
import com.mqt.ganghuazhifu.bean.Record;
import com.mqt.ganghuazhifu.bean.RecordsList;
import com.mqt.ganghuazhifu.bean.Searcher;
import com.mqt.ganghuazhifu.http.CusFormBody;
import com.mqt.ganghuazhifu.http.HttpRequest;
import com.mqt.ganghuazhifu.http.HttpRequestParams;
import com.mqt.ganghuazhifu.http.HttpURLS;
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener;
import com.mqt.ganghuazhifu.utils.DateTextUtils;
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils;
import com.mqt.ganghuazhifu.utils.ToastUtil;
import com.mqt.ganghuazhifu.utils.UnitConversionUtils;
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@SuppressLint("NewApi")
public class RecordFragment extends BaseFragment implements OnRecyclerViewItemClickListener, DatePickerDialog.OnDateSetListener, ObservableScrollViewCallbacks {
    public static final String DATEPICKER_TAG = "RecordFragment";
    private View view;
    private PullToRefreshRecyclerView pullToRefreshRecyclerView;
    private PopupWindow popupWindow;
    private LinearLayout linearLayout_popwindows, linearLayout_fenlei, linearLayout_frameLayout_1, linearLayout_ignole;
    private EditText et_popwidow_search;
    private TextView tv_start_time, tv_end_time, tv_unPay, tv_all, tv_fenlei;
    private RadioGroup radioGroup_category;
    private Spinner spinner_popupwindow;
    private TextView title;
    private List<Record> list = new ArrayList<>();
    private ArrayList<RecordsList> lists;
    private RecordsListAdapter adapter;
    private DatePickerDialog dialog;
    private int dialogType = 1;//1;start DatePickerDialog;2,end DatePickerDialog
    private String startDate, endDate, status, statusone, statustwo, pmttp, ordernb, payeecode, username, usernb,
            useraddr;
    private ArrayList<String> questionList;
    private int page = 0;
    private long time;
    private String pageLast = page + "";

    public void newInstence(TextView title) {
        this.title = title;
    }

    public void upDataList(int position, Record Record) {
        list.get(position).status = Record.status;
        list.get(position).paystatus = Record.paystatus;
        list.get(position).nfcpayflag = Record.nfcpayflag;
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, null);
        pullToRefreshRecyclerView = (PullToRefreshRecyclerView) view.findViewById(R.id.list_recorder);

        pullToRefreshRecyclerView.removeHeader();

        pullToRefreshRecyclerView.setSwipeEnable(true);

        pullToRefreshRecyclerView.setScrollViewCallbacks(this);

        pullToRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pullToRefreshRecyclerView.setPagingableListener(() -> {
            page++;
            if (Integer.parseInt(pageLast) <= page) {
                ToastUtil.Companion.toastInfo("最后一页了!");
                pullToRefreshRecyclerView.onFinishLoading(false, false);
                pageLast = page + "";
            } else {
                initdata();
            }
        });

        pullToRefreshRecyclerView.setOnRefreshListener(() -> {
            page = 0;
            initdata();
            if (Integer.parseInt(pageLast) == 1) {
                ToastUtil.Companion.toastInfo("已到达第一页!");
                pageLast = page + "";
            }
        });

        pullToRefreshRecyclerView.setLoadmoreString("正在加载...");
        pullToRefreshRecyclerView.onFinishLoading(true, false);


        adapter = new RecordsListAdapter(getActivity());
        adapter.setOnRecyclerViewItemClickListener(this);

        pullToRefreshRecyclerView.setAdapter(adapter);

        linearLayout_popwindows = (LinearLayout) view.findViewById(R.id.linearLayout_popwindows);
        linearLayout_ignole = (LinearLayout) view.findViewById(R.id.linearLayout_ignole);
        linearLayout_popwindows.setOnTouchListener((v, event) -> getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null && App.Companion.getManager().hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS));

        linearLayout_ignole.setOnTouchListener((v, event) -> {
            MainActivity.isShowPopupWindow = false;
            title.setText("筛选");
            handler.sendEmptyMessage(2);
            return false;
        });

        linearLayout_frameLayout_1 = (LinearLayout) view.findViewById(R.id.linearLayout_frameLayout_1);
        et_popwidow_search = (EditText) view.findViewById(R.id.et_popwidow_search);
        radioGroup_category = (RadioGroup) view.findViewById(R.id.radioGroup_category);
        spinner_popupwindow = (Spinner) view.findViewById(R.id.spinner_popupwindow);

        questionList = new ArrayList<>();
        questionList.add("全部");
        questionList.add("已付款");
        questionList.add("待付款");
        questionList.add("已取消");
        questionList.add("支付失败");
        questionList.add("待退款");
        questionList.add("已退款");
        questionList.add("核签失败");
        questionList.add("金额不符");
        // questionList.add("未缴费");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item,
                questionList);
        adapter.setDropDownViewResource(R.layout.activity_registration_set_question_item);
        spinner_popupwindow.setAdapter(adapter);

        tv_start_time = (TextView) view.findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) view.findViewById(R.id.tv_end_time);

        tv_unPay = (TextView) view.findViewById(R.id.tv_unPay);
        tv_all = (TextView) view.findViewById(R.id.tv_all);
        tv_fenlei = (TextView) view.findViewById(R.id.tv_fenlei);
        linearLayout_fenlei = (LinearLayout) view.findViewById(R.id.linearLayout_fenlei);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int endyear = calendar.get(Calendar.YEAR);
        int endmonth = calendar.get(Calendar.MONTH) + 1;
        int endday = calendar.get(Calendar.DAY_OF_MONTH);
        int startyear;
        int startmonth;
        int startday;
        if (endmonth == 1) {
            startyear = endyear - 1;
            startmonth = 12;
        } else {
            startyear = endyear;
            startmonth = endmonth - 1;
        }

        startday = getDays(startyear, startmonth);

        tv_end_time.setText(
                endyear + "-" + DateTextUtils.DateToString(endmonth) + "-" + DateTextUtils.DateToString(endday));
        tv_start_time.setText(
                startyear + "-" + DateTextUtils.DateToString(startmonth) + "-" + DateTextUtils.DateToString(startday));
        // endDate = endyear + DateTextUtils.DateToString(endmonth) +
        // DateTextUtils.DateToString(endday);
        // startDate = startyear + DateTextUtils.DateToString(startmonth) +
        // DateTextUtils.DateToString(startday);
        setViewClick(view, R.id.tv_end_time);
        setViewClick(view, R.id.tv_start_time);
        setViewClick(view, R.id.tv_all);
        setViewClick(view, R.id.tv_unPay);
        setViewClick(view, R.id.linearLayout_fenlei);
        initPopupWindow();
        status = null;
        pmttp = null;
        statusone = "PR00";
        statustwo = "PR01";
        dialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        initdata();
        return view;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:// show
                    if (linearLayout_popwindows != null) {
                        linearLayout_popwindows.setClickable(true);
                        linearLayout_popwindows.setVisibility(View.VISIBLE);
                        linearLayout_popwindows.requestFocus();
                    }
                    break;
                case 2:// 取消
                    // popupWindow.dismiss();
                    linearLayout_frameLayout_1.setClickable(true);
                    et_popwidow_search.setText(null);
                    linearLayout_popwindows.setVisibility(View.GONE);
                    break;
                case 3:// 确定
                    // popupWindow.dismiss();
                    linearLayout_frameLayout_1.setClickable(true);
                    linearLayout_popwindows.setVisibility(View.GONE);
                    Searcher searcher = getSearchInfo();
                    et_popwidow_search.setText(null);

                    Logger.d("type--->" + searcher.getType());
                    Logger.d("startTime--->" + searcher.getStartTime());
                    Logger.d("endTime--->" + searcher.getEndTime());
                    Logger.d("category--->" + searcher.getCategory());
                    Logger.d("status--->" + searcher.getStatus());
                    Logger.d("keyWords--->" + searcher.getKeyWords());

                    if (searcher.getStartTime() != null) {
                        startDate = searcher.getStartTime().replace("-", "");
                    }
                    if (searcher.getEndTime() != null) {
                        endDate = searcher.getEndTime().replace("-", "");
                    }
                    if (searcher.getKeyWords() != null) {
                        ordernb = searcher.getKeyWords();
                    }
                    if (searcher.getCategory() != null) {
                        pmttp = searcher.getCategory();
                    }
                    status = searcher.getStatus();
                    page = 0;
                    statusone = null;
                    statustwo = null;
                    initdata();
                    ordernb = null;
                    break;
            }
        }

        ;
    };

    public Handler getHandler() {
        return handler;
    }

    private int getDays(int startyear, int startmonth) {
        switch (startmonth) {
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if ((startyear % 4 == 0 && startyear % 100 != 0) || startyear % 400 == 0) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return 31;
        }
    }

    public void initdata() {
        CusFormBody body = HttpRequestParams.INSTANCE.getParamsForProcessQuery(username, usernb, useraddr, status, statusone,
                statustwo, pmttp, null, ordernb, payeecode, null, null, startDate, endDate, page);
        HttpRequest.Companion.getInstance().httpPost(getActivity(), HttpURLS.INSTANCE.getProcessQuery(), false, "ProcessQuery", body,
                (isError, response, type, error) -> {
                    pullToRefreshRecyclerView.setOnRefreshComplete();
                    pullToRefreshRecyclerView.onFinishLoading(true, false);
                    if (isError) {
                        Logger.e(error.toString());
                    } else {
                        Logger.i(response.toString());
                        JSONObject ResponseHead = response.getJSONObject("ResponseHead");
                        String CurrentCount = ResponseHead.getString("CurrentCount");
                        String OrgnSerialNumber = ResponseHead.getString("OrgnSerialNumber");
                        String PageCount = ResponseHead.getString("PageCount");
                        int NoPay = ResponseHead.getIntValue("NoPay");
                        int NoWriteTable = ResponseHead.getIntValue("NoWriteTable");
//                        MainActivity.mainMenu.setTextNum(NoPay + NoWriteTable);
                        if (MainActivity.bottom_navigation != null) {
                            if (NoPay + NoWriteTable != 0)
                                MainActivity.bottom_navigation.setNotification((NoPay + NoWriteTable) + "", 1);
                            else
                                MainActivity.bottom_navigation.setNotification("", 1);
                        }
                        if (!TextUtils.isEmpty(PageCount))
                            pageLast = PageCount;
                        if (PageCount == null || Integer.parseInt(PageCount) == 0) {
//                            MainActivity.mainMenu.setTextNum(0);
                            MainActivity.bottom_navigation.setNotification("", 1);
                            ToastUtil.Companion.toastInfo("当前条件没有查询到数据!");
                        }
                        String ProcessCode = ResponseHead.getString("ProcessCode");
                        String ProcessDes = ResponseHead.getString("ProcessDes");
                        String QueryCd = ResponseHead.getString("QueryCd");
                        String RecordCount = ResponseHead.getString("RecordCount");
                        List<Record> records = new ArrayList<>();
                        switch (type) {
                            case 0:
                                break;
                            case 1:
                                String ResponseFields = response.getString("ResponseFields");
                                if (ProcessCode.equals("0000")) {
                                    records = JSONObject.parseArray(ResponseFields, Record.class);
                                }
                                break;
                            case 2:
                                JSONObject ResponseFields1 = response.getJSONObject("ResponseFields");
                                if (ProcessCode.equals("0000")) {
                                    String QryResults = ResponseFields1.getString("QryResults");
                                    records.add(JSONObject.parseObject(QryResults, Record.class));
                                    break;
                                }
                        }
                        if (list != null) {
                            if (page != 0) {
                                list.addAll(records);
                            } else {
                                list = records;
                            }
                            initAdapter();
                        }
                    }
                });
    }

    /**
     * 假数据
     */
    private void initAdapter() {

        for (Record record : list) {
            String ss = record.ordersettime;
            if (ss.lastIndexOf("T") != -1) {
                record.ordersettime = ss.substring(0, ss.lastIndexOf("T"));
            }
        }

        int num = 0;
        for (Record record : list) {
            if ("PR01".equals(record.status) || "10".equals(record.nfcpayflag) && "11".equals(record.nfcflag) && "PR00".equals(record.status)) {
                num++;
            }
        }

        for (Record record : list) {
            record.tag = 2;
            record.year = Integer.parseInt(record.ordersettime.substring(0, record.ordersettime.indexOf("-")));
            record.month = Integer.parseInt(record.ordersettime.substring(record.ordersettime.indexOf("-") + 1,
                    record.ordersettime.lastIndexOf("-")));
        }

        ComparatorRecordByYearAndMonth comparator = new ComparatorRecordByYearAndMonth();
        Collections.sort(list, comparator);

        lists = new ArrayList<>();

        List<Record> temp = new ArrayList<Record>();

        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                Record record1 = list.get(i);
                Record record2 = list.get(i + 1);
                if (record1.year == record2.year && record1.month == record2.month && i != list.size() - 2) {
                    temp.add(record1);
                } else if (i == list.size() - 2) {
                    if (record1.year == record2.year && record1.month == record2.month) {
                        temp.add(record1);
                        temp.add(record2);
                        lists.add(new RecordsList(temp, record1.year, record1.month));
                    } else {
                        temp.add(record1);
                        lists.add(new RecordsList(temp, record1.year, record1.month));
                        temp = new ArrayList<>();
                        temp.add(record2);
                        lists.add(new RecordsList(temp, record2.year, record2.month));
                    }
                } else {
                    temp.add(record1);
                    lists.add(new RecordsList(temp, record1.year, record1.month));
                    temp = new ArrayList<>();
                }
            }
        } else if (list.size() == 1) {
            lists.add(new RecordsList(list, list.get(0).year, list.get(0).month));
        }

        for (RecordsList recordsList : lists) {
            recordsList.QryResults.get(0).tag = 1;
            recordsList.QryResults.get(recordsList.QryResults.size() - 1).tag = 3;
            if (recordsList.QryResults.size() - 1 == 0) {
                recordsList.QryResults.get(0).tag = 4;
            }
        }
        adapter.updateRecordsList(lists);
    }

    public Searcher getSearchInfo() {
        Searcher searcher = new Searcher();
        if (TextUtils.isEmpty(et_popwidow_search.getText().toString())) {
            searcher.setType(2);
        } else {
            searcher.setType(1);
            searcher.setKeyWords(et_popwidow_search.getText().toString());
        }

        if (!TextUtils.isEmpty(tv_start_time.getText().toString())) {
            searcher.setStartTime(tv_start_time.getText().toString());
        }

        if (!TextUtils.isEmpty(tv_end_time.getText().toString())) {
            searcher.setEndTime(tv_end_time.getText().toString());
        }

        if (radioGroup_category.getCheckedRadioButtonId() != -1) {
            switch (radioGroup_category.getCheckedRadioButtonId()) {
                case R.id.radioButton_category_0:
                    searcher.setCategory(null);
                    break;
                case R.id.radioButton_category_1:
                    searcher.setCategory("010001");
                    break;
                case R.id.radioButton_category_2:
                    searcher.setCategory("010002");
                    break;
                case R.id.radioButton_category_3:
                    searcher.setCategory("020001");
                    break;
            }

            for (int i = 0; i < radioGroup_category.getChildCount(); i++) {
                RadioButton rade = (RadioButton) radioGroup_category.getChildAt(i);
                rade.setChecked(false);
            }
        }

        if (spinner_popupwindow.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
            switch (spinner_popupwindow.getSelectedItemPosition()) {
                case 0:
                    searcher.setStatus(null);
                    break;
                case 1:
                    searcher.setStatus("PR00");
                    break;
                case 2:
                    searcher.setStatus("PR01");
                    break;
                case 3:
                    searcher.setStatus("PR02");
                    break;
                case 4:
                    searcher.setStatus("PR03");
                    break;
                case 5:
                    searcher.setStatus("PR04");
                    break;
                case 6:
                    searcher.setStatus("PR05");
                    break;
                case 7:
                    searcher.setStatus("PR16");
                    break;
                case 8:
                    searcher.setStatus("PR17");
                    break;
                // case 9:
                // searcher.status = "PR08";
                // break;
            }
        }
        return searcher;
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all:
                page = 0;
                status = null;
                pmttp = null;
                statusone = null;
                statustwo = null;
                tv_all.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                tv_all.setBackgroundResource(R.drawable.record_head_press);
                tv_unPay.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_green_slow));
                tv_unPay.setBackground(null);
                tv_fenlei.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_green_slow));
                linearLayout_fenlei.setBackground(null);
                page = 0;
                initdata();
                break;
            case R.id.tv_unPay:
                status = null;
                pmttp = null;
                statusone = "PR00";
                statustwo = "PR01";
                tv_all.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_green_slow));
                tv_all.setBackground(null);
                tv_unPay.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                tv_unPay.setBackgroundResource(R.drawable.record_head_press);
                tv_fenlei.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_green_slow));
                linearLayout_fenlei.setBackground(null);
                page = 0;
                initdata();
                break;
            case R.id.linearLayout_fenlei:
                popupWindow.showAsDropDown(linearLayout_fenlei);
                tv_all.setTextColor(getResources().getColor(R.color.dark_green_slow));
                tv_all.setBackground(null);
                tv_unPay.setTextColor(getResources().getColor(R.color.dark_green_slow));
                tv_unPay.setBackground(null);
                tv_fenlei.setTextColor(getResources().getColor(R.color.white));
                linearLayout_fenlei.setBackgroundResource(R.drawable.record_head_press);
                break;
            case R.id.tv_end_time:
                dialogType = 2;
                dialog.setYearRange(1985, 2028);
                dialog.show(getActivity().getFragmentManager(), DATEPICKER_TAG);
                break;
            case R.id.tv_start_time:
                dialogType = 1;
                dialog.setYearRange(1985, 2028);
                dialog.show(getActivity().getFragmentManager(), DATEPICKER_TAG);
                break;
        }
    }

    private void initPopupWindow() {
        View viewcategory = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_category, null);
        popupWindow = new PopupWindow(viewcategory, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);

        if (WelcomeActivity.Companion.getScreenwidth() == 0 || WelcomeActivity.Companion.getScreenhigh() == 0) {
            WelcomeActivity.Companion.setScreenwidth(EncryptedPreferencesUtils.getScreenSize()[0]);
            WelcomeActivity.Companion.setScreenhigh(EncryptedPreferencesUtils.getScreenSize()[1]);
        }
        Logger.d("screenwidth--->" + WelcomeActivity.Companion.getScreenwidth());
        Logger.d("screenhigh--->" + WelcomeActivity.Companion.getScreenhigh());

        popupWindow.setWidth((WelcomeActivity.Companion.getScreenwidth() - UnitConversionUtils.dipTopx(getActivity(), 24)) / 3);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(false);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(true);
        // popupWindow.setAnimationStyle(R.style.mystyle);

        viewcategory.findViewById(R.id.tv_gas).setOnClickListener(v -> {
            popupWindow.dismiss();
            pmttp = "010001";
            status = null;
            statusone = null;
            statustwo = null;
            page = 0;
            initdata();
        });
        viewcategory.findViewById(R.id.tv_busi).setOnClickListener(v -> {
            popupWindow.dismiss();
            pmttp = "010002";
            status = null;
            statusone = null;
            statustwo = null;
            page = 0;
            initdata();
        });
        viewcategory.findViewById(R.id.tv_water).setOnClickListener(v -> {
            popupWindow.dismiss();
            pmttp = "020001";
            status = null;
            statusone = null;
            statustwo = null;
            page = 0;
            initdata();
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (time == 0 || System.currentTimeMillis() - time > 1000) {
            if (list.size() > position) {
                Intent intent = new Intent(getActivity(), RecordDetailActivity.class);
                intent.putExtra("TYPE", 1);
                intent.putExtra("Record", Parcels.wrap(list.get(position)));
                intent.putExtra("Position", (position));
                getActivity().startActivity(intent);
                time = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        tv_start_time.setText(year + "-" + DateTextUtils.DateToString(monthOfYear + 1) + "-"
                + DateTextUtils.DateToString(dayOfMonth));
        startDate = year + DateTextUtils.DateToString(monthOfYear + 1)
                + DateTextUtils.DateToString(dayOfMonth);
        tv_end_time.setText(yearEnd + "-" + DateTextUtils.DateToString(monthOfYearEnd + 1) + "-"
                + DateTextUtils.DateToString(dayOfMonthEnd));
        endDate = yearEnd + DateTextUtils.DateToString(monthOfYearEnd + 1) + DateTextUtils.DateToString(dayOfMonthEnd);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
        } else if (scrollState == ScrollState.DOWN) {
        }
    }


    class ComparatorRecordByYearAndMonth implements Comparator<Record> {
        @Override
        public int compare(Record record1, Record record2) {
            if (record1.year > record2.year) {
                return -1;
            } else if (record1.year == record2.year) {
                if (record1.month > record2.month) {
                    return -1;
                } else if (record1.month == record2.month) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }
    }

}
