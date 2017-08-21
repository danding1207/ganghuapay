package com.mqt.ganghuazhifu.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

	private static int VERSION = 1;
	private AtomicInteger mOpenCounter = new AtomicInteger();

	private static DatabaseManager instance;
	private SQLiteDatabase mDatabase;

	/**
	 * 在SQLiteOpenHelper的子类当中，必须有该构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param name
	 *            数据库名称
	 * @param factory
	 * @param version
	 *            当前数据库的版本，值必须是整数并且是递增的状态
	 */
	public DatabaseManager(Context context, String name, CursorFactory factory, int version) {
		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
	}

	public DatabaseManager(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseManager(Context context, String name) {
		this(context, name, VERSION);
	}

	public static synchronized void initializeInstance(Context context, String name, int version) {
		if (instance == null) {
			instance = new DatabaseManager(context, name, version);
		}
		VERSION = version;
	}

	public static synchronized DatabaseManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(DatabaseManager.class.getSimpleName()
					+ " is not initialized, call initializeInstance(..) method first.");
		}
		return instance;
	}
	
	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			mDatabase = getWritableDatabase();
		}
		return mDatabase;
	}

	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mDatabase.close();
		}
	}
	
    //该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法  
    @Override
    public void onCreate(SQLiteDatabase db) {  
        //execSQL用于执行SQL语句
        db.execSQL("create table message(id integer PRIMARY KEY AUTOINCREMENT, isreaded int, topic varchar(20), msg varchar(20), time varchar(20))");  
    }
  
    @Override  
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {  
    }

	
	
}