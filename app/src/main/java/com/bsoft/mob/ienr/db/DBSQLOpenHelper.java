package com.bsoft.mob.ienr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSQLOpenHelper extends SQLiteOpenHelper {

	// private Context mContext;

	public DBSQLOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// this.mContext = context;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("DROP TABLE IF EXISTS " + "t_user");
		db.execSQL("DROP TABLE IF EXISTS " + "t_message");
		db.execSQL("DROP TABLE IF EXISTS " + "t_setting");
		db.execSQL("DROP TABLE IF EXISTS " + "t_offline");
		// 用户表
		db.execSQL("CREATE TABLE  t_user" + "("
				+ " _id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "remote_id TEXT NOT NULL," + "agent_id TEXT NOT NULL,"
				+ "user_name TEXT  ," + "password TEXT " + " );");
		db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS remote_agent_key ON t_user (remote_id,agent_id)");
		db.execSQL("INSERT INTO t_user(user_name,password,remote_id,agent_id) VALUES('root','sa',-1,-1)");

		// 消息主题表
		db.execSQL("CREATE TABLE  t_topic" + "("
				+ " _id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "topic INTEGER UNIQUE DEFAULT 1 " + " );");

		// 消息表
		db.execSQL("CREATE TABLE  t_message" + "("
				+ " _id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "user INTEGER DEFAULT 0," + "topic INTEGER NOT NULL ,"
				+ "content TEXT NOT NULL ," + "business_id TEXT  ,"
				+ "state INTEGER DEFAULT 0 ," + "receive_time TEXT NOT NULL ,"
				+ "agent_id TEXT ," + "remote_id INTEGER UNIQUE DEFAULT 0,"
				+ "foreign key (user) references t_user(_id),"
				+ "foreign key (topic) references t_topic(topic)" + " );");

		// 用户设置表
		db.execSQL("CREATE TABLE  t_setting" + "("
				+ " _id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "user TEXT NOT NULL ," + "vib INTEGER NOT NULL DEFAULT  -1 ,"
				+ "foreign key (user) references t_user(user_name)" + " );");

		//离线保存表
		db.execSQL("CREATE TABLE t_offline( _id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+"url TEXT NOT NULL ,"+"type INTEGER NOT NULL DEFAULT 1 ,"
				+"param TEXT ,"+"patient TEXT ,"+"record_name TEXT ,"+"flag INTEGER NOT NULL DEFAULT 0 ,"
				+"create_time TEXT ,"+"create_nurse TEXT ,"+"synchro_time TEXT ,"+"synchro_nurse TEXT );");
		alertAtVs2(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		switch (newVersion) {
		case 2:
			switch (oldVersion) {
			case 1:
				alertAtVs2(db);
				break;
			}
			break;
		}
	}

	private void alertAtVs2(SQLiteDatabase db) {

		// 修改设置表外键引用
		db.execSQL("CREATE TABLE  t_setting2" + "("
				+ " _id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "user TEXT NOT NULL ," + "vib INTEGER NOT NULL DEFAULT  -1 ,"
				+ "foreign key (user) references t_user(_id)" + " );");
		db.execSQL("INSERT INTO t_setting2 (_id, user, vib) SELECT _id, user, vib FROM t_setting;");
		db.execSQL("DROP TABLE t_setting;");
		db.execSQL("ALTER TABLE t_setting2 RENAME TO t_setting;");

	}
}
