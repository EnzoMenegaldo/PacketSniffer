/*  */
package com.packetsniffer.emenegal.packetsniffer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
// Start of user code protected additional OrmLiteDBHelper imports
import com.j256.ormlite.table.TableUtils;
import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.packetsniffer.emenegal.packetsniffer.activities.OrmLiteActionBarActivity;
// End of user code

/**
 * ORMLite Data base helper, designed to be used by android Activity
 */
public class OrmLiteDBHelper extends OrmLiteSqliteOpenHelper{
	
	public static final String LOG_TAG = "OrmLiteDBHelper";

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "packets"+"_"+ OrmLiteActionBarActivity.TIME+".db";
	// any time you make changes to your database objects, you may have to increase the database version
	// Start of user code OrmLiteDBHelper DB version MobilePrivacyProfiler
	private static final int DATABASE_VERSION = 1;
	// End of user code



	// the DAO object we use to access the PacketModel table
	private RuntimeExceptionDao<PacketModel, Integer> packetRuntimeDao = null;
	

	public OrmLiteDBHelper(Context context) {
		super(context, context.getExternalFilesDir(null).getAbsolutePath()
				+ File.separator + DATABASE_NAME , null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
	// Start of user code OrmLiteDBHelper onCreate MobilePrivacyProfiler
		try {
			Log.i(OrmLiteDBHelper.class.getName(), "onCreate");
			createAllTables(db);
		} catch (SQLException e) {
			Log.e(OrmLiteDBHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	// End of user code
	}
	public void createAllTables(SQLiteDatabase db)  throws SQLException {
		TableUtils.createTable(connectionSource, PacketModel.class);
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
	// Start of user code OrmLiteDBHelper onUpgrade MobilePrivacyProfiler
		try {
			Log.i(OrmLiteDBHelper.class.getName(), "onUpgrade");
			dropAllTables(db);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(OrmLiteDBHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	// End of user code
	}
	public void dropAllTables(SQLiteDatabase db)  throws SQLException {
		TableUtils.dropTable(connectionSource, PacketModel.class, true);
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our PacketModel class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<PacketModel, Integer> getNetActivityDao() {
		if (packetRuntimeDao == null) {
			packetRuntimeDao = getRuntimeExceptionDao(PacketModel.class);
		}
		return packetRuntimeDao;
	}



	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		packetRuntimeDao = null;
	}


}
