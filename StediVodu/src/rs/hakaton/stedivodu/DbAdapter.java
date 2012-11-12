package rs.hakaton.stedivodu;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

	private static final String DB_NAME = "vodiracunaovodi";
	private static final String DB_TABLE_GRAD = "grad";
	private static final String DB_TABLE_PITANJE = "pitanje";
	private static final String DB_TABLE_ODGOVOR = "odgovor";
	public static final String ROWID = "_id";
	public static final String G_GRAD = "grad";
	public static final String P_PITANJE = "pitanje";
	public static final String O_ODGOVOR = "odgovor";
	public static final String O_ID_PITANJE = "id_pitanje";
	private static final int DATABASE_VERSION = 1;

	private static final boolean D = true;
	private static final String TAG = "DbAdapter class";
	private Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		Context context;

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
//			db.execSQL(getSqlFromAssets("create.sql", context));
			execBatchSqlFromAssets("create.sql", context, db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (D)
				Log.w(TAG, "Upgrading database from version " + oldVersion
						+ " to " + newVersion
						+ ", which will destroy all old data");
//			db.execSQL(getSqlFromAssets("drop.sql", context));
			execBatchSqlFromAssets("drop.sql", context, db);
			onCreate(db);
		}
	}

	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public DbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void truncateGrad() {
		mDb.execSQL("");
	}

	public long insertRow(String userid, String title, String description,
			String latitude, String longitude) {
		ContentValues initialValues = new ContentValues();
		// initialValues.put(KEY_USERID, userid);
		// initialValues.put(KEY_TITLE, title);
		// initialValues.put(KEY_DESCRIPTION, description);
		// initialValues.put(KEY_LATITUDE, latitude);
		// initialValues.put(KEY_LONGITUDE, longitude);

		return mDb.insert(DB_TABLE_GRAD, null, initialValues);
	}

	// public Cursor getAllMarkers() {
	// return mDb.query(DB_TABLE_GRAD, new String[]
	// {KEY_ROWID, KEY_LATITUDE, KEY_LONGITUDE, KEY_TITLE, KEY_DESCRIPTION,
	// KEY_SNIPPET, KEY_IMAGEURL, KEY_IMAGEASSET},
	// null, null, null, null, null);
	// }

	// public Cursor fetchRow(long rowId) throws SQLException {
	// Cursor mCursor =
	// mDb.query(true, DB_TABLE_GRAD, new String[] {KEY_ROWID, KEY_LATITUDE,
	// KEY_LONGITUDE,
	// KEY_TITLE, KEY_DESCRIPTION, KEY_SNIPPET, KEY_ADDRESS, KEY_IMAGEURL,
	// KEY_IMAGEASSET, KEY_PAGEASSET}, KEY_ROWID + "=" + rowId, null,
	// null, null, null, null);
	// if (mCursor != null) {
	// mCursor.moveToFirst();
	// }
	// return mCursor;
	// }

	private static void execBatchSqlFromAssets(String asset, Context c,
			SQLiteDatabase db) {
		InputStream input;
		String sql = "";
		try {
			input = c.getAssets().open(asset);
			// myData.txt can't be more than 2 gigs.
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			// byte buffer into a string
			sql = new String(buffer);

			Log.d(TAG, "Sadrzaj sql: " + sql);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] queries = sql.split(";");
		for (String query : queries) {
			db.execSQL(query);
		}
	}

}
