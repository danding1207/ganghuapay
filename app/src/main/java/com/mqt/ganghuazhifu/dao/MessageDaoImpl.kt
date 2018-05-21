package com.mqt.ganghuazhifu.dao

import android.content.Context
import com.mqt.ganghuazhifu.bean.Message
import com.mqt.ganghuazhifu.bean.MessageRealm
import com.mqt.ganghuazhifu.utils.RealmUtils
import java.sql.SQLException
import java.util.ArrayList
import io.realm.RealmResults
import io.realm.Sort

/**
* Created by msc on 2016/9/27.
*/
class MessageDaoImpl(private val mContext: Context) : MessageDao {

    /**
     * 同步插入
     * @param message 需要插入的消息对象
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    override fun insert(message: MessageRealm) {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//必须先开启事务
        mRealm.copyToRealm(message)//把User对象复制到Realm
        mRealm.commitTransaction()//提交事务
        mRealm.close()//必须关闭，不然会造成内存泄漏
    }

    /**
     * 返回所有的消息对象,并按照時間排序

     * @return 消息对象表
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    override fun getAllMessage(): ArrayList<Message> {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//必须先开启事务
        val results = mRealm.where(MessageRealm::class.java).findAll()
        results.sort("time", Sort.ASCENDING)//针对字符串的排序，但目前并不是支持所有字符集
        val list = results.mapTo(ArrayList<Message>()) { it.message }
        mRealm.commitTransaction()//提交事务
        mRealm.close()//必须关闭，不然会造成内存泄漏
        return list
    }

    @Throws(SQLException::class)
    override fun getMessageById(id: Int): Message? {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()
        val messageRealm = mRealm.where(MessageRealm::class.java).equalTo("id", id).findFirst()//删除id列值为id的行
        val message = messageRealm?.message
        mRealm.commitTransaction()//提交事务
        mRealm.close()//必须关闭，不然会造成内存泄漏
        return message
    }

    @Throws(SQLException::class)
    override fun hasUnreadMessage(): Boolean {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        val results = mRealm.where(MessageRealm::class.java).findAll()
        results.sort("time", Sort.DESCENDING)//针对字符串的排序，但目前并不是支持所有字符集
        val hasUnreadMessage = !results.none { it.message.isreaded }
        mRealm.commitTransaction()//提交事务
        mRealm.close()
        return hasUnreadMessage
    }

    @Throws(SQLException::class)
    override fun getUnreadMessageCount(): Int {
        var unreadMessageCount = 0
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        val results = mRealm.where(MessageRealm::class.java).findAll()
        results.sort("time", Sort.DESCENDING)//针对字符串的排序，但目前并不是支持所有字符集
        for (message in results) {
            if (!message.message.isreaded) {
                unreadMessageCount++
                break
            }
        }
        mRealm.commitTransaction()//提交事务
        mRealm.close()
        return unreadMessageCount
    }

    @Throws(SQLException::class)
    override fun getAllRealmMessage(): RealmResults<MessageRealm> {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        val results = mRealm.where(MessageRealm::class.java).findAll()
        results.sort("time", Sort.DESCENDING)//针对字符串的排序，但目前并不是支持所有字符集
        mRealm.commitTransaction()//提交事务
        mRealm.close()
        return results
    }

    /**
     * 更新一个Message

     * @param message 需要更新的用户类
     * *
     * @return 返回更新后的Message
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    override fun updateMessage(message: MessageRealm): MessageRealm {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        val message1 = mRealm.copyToRealmOrUpdate(message)
        mRealm.commitTransaction()//提交事务
        mRealm.close()//必须关闭事务
        return message1
    }

    @Throws(SQLException::class)
    override fun updateMessageTopic(topic1: String, topic2: String) {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        mRealm.where(MessageRealm::class.java)
                .equalTo("topic", topic1)//查询出name为name1的User对象
                .findFirst()?.message?.setTopic(topic2)//修改查询出的第一个对象的名字
        mRealm.commitTransaction()
        mRealm.close()

    }

    @Throws(SQLException::class)
    override fun updateMessageMsg(msg1: String, msg2: String) {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        mRealm.where(MessageRealm::class.java)
                .equalTo("msg", msg1)//查询出name为name1的User对象
                .findFirst()?.message?.setMsg(msg2)//修改查询出的第一个对象的名字
        mRealm.commitTransaction()
        mRealm.close()
    }

    @Throws(SQLException::class)
    override fun updateMessageIsreadedById(id: Int, isreaded: Boolean) {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        mRealm.where(MessageRealm::class.java)
                .equalTo("id", id)//查询出name为name1的User对象
                .findFirst()?.setIsreaded(isreaded)//修改查询出的第一个对象的名字
        mRealm.commitTransaction()
        mRealm.close()
    }

    @Throws(SQLException::class)
    override fun deleteMessage(id: Int) {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()
        mRealm.where(MessageRealm::class.java).equalTo("id", id).findFirst()?.deleteFromRealm()//从数据库删除
        mRealm.commitTransaction()
        mRealm.close()
    }

    /**
     * 异步插入Message

     * @param message 需要添加的Message对象
     * *
     * @throws SQLException
     */
    @Throws(SQLException::class)
    override fun insertMessageAsync(message: MessageRealm) {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        //一个Realm只能在同一个线程访问，在子线程中进行数据库操作必须重新获取realm对象
        //        mRealm.beginTransaction();//开启事务
        mRealm.executeTransaction { realm ->
            realm.beginTransaction()//开启事务
            realm.copyToRealm(message)
            realm.commitTransaction()
            realm.close()//记得关闭事务
        }
        mRealm.close()//外面也不能忘记关闭事务
    }

    @Throws(SQLException::class)
    override fun findByTopicOrMsg(topic: String, msg: Int): MessageRealm? {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()//开启事务
        val message = mRealm.where(MessageRealm::class.java)
                .equalTo("topic", topic)//相当于where name = name1
                .or()//或，连接查询条件，没有这个方式时会默认是&连接
                .equalTo("msg", msg)//相当于where age = age1
                .findFirst()
        //整体相当于select * from (表名) where name = (传入的name) or age = （传入的age）limit 1;
        mRealm.commitTransaction()
        mRealm.close()
        return message
    }

    @Throws(SQLException::class)
    override fun deleteAll() {
        val mRealm = RealmUtils.getInstance(mContext)!!.realm
        mRealm.beginTransaction()
        mRealm.where(MessageRealm::class.java).findAll().deleteAllFromRealm()
        mRealm.commitTransaction()
        mRealm.close()
    }

}
