package com.mqt.ganghuazhifu.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageRealm extends RealmObject {

	//主键必须添加注解
	@PrimaryKey
	private int id;// id
	private String topic;// 标题
	private String msg;// 内容
	private String time;// 时间
	private boolean isreaded;// 已读/未读

	public MessageRealm() {
	}

	public MessageRealm(int id, String topic, String msg, String time, boolean isreaded) {
		super();
		this.id = id;
		this.topic = topic;
		this.msg = msg;
		this.time = time;
		this.isreaded = isreaded;
	}

	public Message getMessage() {
		return new Message(id, topic, msg, time, isreaded);
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
