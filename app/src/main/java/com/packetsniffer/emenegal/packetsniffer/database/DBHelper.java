/*  */
package com.packetsniffer.emenegal.packetsniffer.database;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;


//Start of user code additional import for DBHelper
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;


import android.content.Context;
//End of user code
/**
 * Context class used to simplify the access to the different DAOs of the application
 */
public class DBHelper {
	//Start of user code additional variables for DBHelper
	private static final String TAG = DBHelper.class.getSimpleName();
	public static final DBHelper INSTANCE = new DBHelper();
	//End of user code


	public Dao<PacketModel, Integer> packetModels;
	//public RuntimeExceptionDao<PacketModel, Integer> netActivityDao;

	
	private DBHelper(){
	}

	private DBHelper(
		Dao<PacketModel, Integer> packetModels
	){

		this.packetModels = packetModels;
	}

	//Start of user code additional methods for DBHelper
	public PacketModel getLastNetConnection(String serverName) throws SQLException {
		QueryBuilder<PacketModel, Integer> queryBuilder = this.packetModels.queryBuilder();
		queryBuilder.where().eq(PacketModel.XML_ATT_HOSTNAME,serverName);
		queryBuilder.orderBy("date",false);

		try {
			PreparedQuery<PacketModel> preparedQuery = queryBuilder.prepare();
			return this.packetModels.queryForFirst(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public OrmLiteDBHelper getDBHelper(Context context){
		return OpenHelperManager.getHelper(context, OrmLiteDBHelper.class);
	}

	//End of user code

}
