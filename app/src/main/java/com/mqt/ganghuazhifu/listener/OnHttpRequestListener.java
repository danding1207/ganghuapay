package com.mqt.ganghuazhifu.listener;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;

/**
 * Created by danding1207 on 17/2/6.
 */
public interface OnHttpRequestListener {
        void OnCompleted(Boolean isError, JSONObject response, int type, IOException error);  // 0:null;1:数组;2:对象;
}
