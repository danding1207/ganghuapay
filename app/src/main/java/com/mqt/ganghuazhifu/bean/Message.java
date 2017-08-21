package com.mqt.ganghuazhifu.bean;

public class Message {

	public int id;// id
	public String topic;// 标题
	public String msg;// 内容
	public String time;// 时间
	public boolean isreaded;// 已读/未读

	public Message() {
	}

	public Message(int id, String topic, String msg, String time, boolean isreaded) {
		super();
		this.id = id;
		this.topic = topic;
		this.msg = msg;
		this.time = time;
		this.isreaded = isreaded;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isreaded() {
		return isreaded;
	}

	public void setIsreaded(boolean isreaded) {
		this.isreaded = isreaded;
	}
}
