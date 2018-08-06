/*  */
package fr.inria.diverse.mobileprivacyprofiler.datamodel;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;


//Start of user code additional import for DBHelper
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.packetsniffer.emenegal.packetsniffer.database.OrmLiteDBHelper;
import com.packetsniffer.emenegal.packetsniffer.database.PacketModel;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static android.content.Context.MODE_PRIVATE;
//End of user code
/**
 * Context class used to simplify the access to the different DAOs of the application
 */
public class DBHelper {
	//Start of user code additional variables for DBHelper
	private static final String TAG = DBHelper.class.getSimpleName();
	//End of user code


	public Dao<PacketModel, Integer> packetModels;
	//public RuntimeExceptionDao<PacketModel, Integer> netActivityDao;

	
	public DBHelper(){
	}

	public DBHelper(
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

	public static OrmLiteDBHelper getDBHelper(Context context){
		return OpenHelperManager.getHelper(context, OrmLiteDBHelper.class);
	}

	//End of user code

}
