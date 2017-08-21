package com.mqt.ganghuazhifu.event;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by msc on 2016/3/28.
 */
public class Event {

    private @Constant.Result int mEventResult;
    private @Constant.GetResultWay int mGetResultWay;
    private JSONObject response;
    private int type;
    private Throwable throwable;

    public Event(@Constant.Result int mEventResult, @Constant.GetResultWay int mGetResultWay, JSONObject response, int type, Throwable throwable) {
        this.mEventResult = mEventResult;
        this.mGetResultWay = mGetResultWay;
        this.response = response;
        this.type = type;
        this.throwable = throwable;
    }

    public @Constant.Result int getEventResult() {
        return mEventResult;
    }

    public @Constant.GetResultWay int getGetResultWay() {
        return mGetResultWay;
    }

    public JSONObject getResult() {
        return response;
    }

    public int getType() {
        return type;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public static final class Builder {

        private @Constant.Result int mEventResult;
        private @Constant.GetResultWay int mGetResultWay;
        private JSONObject response;
        private int type;
        private Throwable throwable;

        public Event.Builder setEventResult(@Constant.Result int mEventResult) {
            this.mEventResult = mEventResult;
            return this;
        }

        public Event.Builder setGetResultWay(@Constant.GetResultWay int mGetResultWay) {
            this.mGetResultWay = mGetResultWay;
            return this;
        }

        public Event.Builder setResponse(JSONObject response) {
            this.response = response;
            return this;
        }

        public Event.Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Event.Builder setThrowable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Event build() {
            return new Event( mEventResult,  mGetResultWay,  response,  type,  throwable);
        }
    }

}