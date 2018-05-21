package com.mqt.ganghuazhifu.dao

import com.mqt.ganghuazhifu.bean.Message
import com.mqt.ganghuazhifu.bean.MessageRealm

import java.sql.SQLException
import java.util.ArrayList

import io.realm.RealmResults

/**
* Created by msc on 2016/9/27.
*/
interface MessageDao {

    /**
     * 插入一个消息

     * @param message 需要插入的消息对象
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun insert(message: MessageRealm)

    /**
     * 获得所有的消息列表

     * @return 消息列表
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun getAllMessage(): ArrayList<Message>

    /**
     * 通过id获得消息

     * @return 消息列表
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun getMessageById(id: Int): Message?

    /**
     * 判断是否有未读消息

     * @return
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun hasUnreadMessage(): Boolean

    /**
     * 获取未读消息数

     * @return
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun getUnreadMessageCount(): Int

    /**
     * 获得所有的消息列表

     * @return 消息列表
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun getAllRealmMessage(): RealmResults<MessageRealm>

    /**
     * 更新一个消息

     * @param message 需要更新的消息类
     * *
     * @return 更新后的对象
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun updateMessage(message: MessageRealm): MessageRealm

    /**
     * 根据标题修改消息

     * @param topic1 老标题
     * *
     * @param topic2 新标题
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun updateMessageTopic(topic1: String, topic2: String)

    /**
     * 根据内容修改消息

     * @param msg1 老内容
     * *
     * @param msg2 新内容
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun updateMessageMsg(msg1: String, msg2: String)

    /**
     * 根据id修改消息已讀標記

     * @param id 老标题
     * *
     * @param isreaded 新标题
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun updateMessageIsreadedById(id: Int, isreaded: Boolean)

    /**
     * 根据id删除消息

     * @param id 消息主键
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun deleteMessage(id: Int)

    /**
     * 异步添加消息

     * @param message 需要添加的消息对象
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun insertMessageAsync(message: MessageRealm)

    /**
     * 按标题或者内容查找第一个Message
     */
    @Throws(SQLException::class)
    fun findByTopicOrMsg(topic: String, msg: Int): MessageRealm?

    /**
     * 清楚所有

     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun deleteAll()

}
