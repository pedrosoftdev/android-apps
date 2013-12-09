package br.com.phsd.migalhatracker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDAO extends SQLiteOpenHelper {

	private static final int DB_VERSION = 2;
	private static String DB_NAME = "MigalhaTrackerDB";
	
	public SQLiteDAO(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String controle = "" +
				"CREATE TABLE TRAJETO (" +
				"ID INTEGER PRIMARY KEY, " +
				"NAVEGANDO CHAR, " +
				"NOME TEXT)";
		
		db.execSQL(controle);
		
		String posicao = "" +
				"CREATE TABLE POSICAO (" +
				"ID INTEGER PRIMARY KEY, " +
				"ID_TRAJETO INTEGER, " +
				"LATITUDE REAL, " +
				"LONGITUDE REAL, " +
				"MOMENTO TEXT, " +
				"FOREIGN KEY (ID_TRAJETO) REFERENCES TRAJETO (ID)" +
				")";
		
		db.execSQL(posicao);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
	}
	
	private void dropTables(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS POSICAO");
		db.execSQL("DROP TABLE IF EXISTS TRAJETO");
	}

}
