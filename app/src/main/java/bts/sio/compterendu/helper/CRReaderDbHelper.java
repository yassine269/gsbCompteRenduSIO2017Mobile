package bts.sio.compterendu.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import bts.sio.compterendu.model.Account;

/**
 * Created by TI-tygangsta on 18/04/2017.
 */

public class CRReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "CRReader.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CRReaderContract.CREntry.TABLE_NAME + " (" +
                    CRReaderContract.CREntry._ID + " INTEGER PRIMARY KEY," +
                    CRReaderContract.CREntry.COLUMN_USER_USERNAME + " TEXT," +
                    CRReaderContract.CREntry.COLUMN_USER_SALT + " TEXT,"+
                    CRReaderContract.CREntry.COLUMN_USER_CLEARPASS + " TEXT,"+
                    CRReaderContract.CREntry.COLUMN_USER_FONCTION + " TEXT)";
    /**
     *     CRReaderContract.CREntry.COLUMN_USER_FONCTION + " TEXT)";
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CRReaderContract.CREntry.TABLE_NAME;

    public CRReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Account getUser(Context context){
        SQLiteDatabase db = this.getReadableDatabase();
        // Request BDD pour savoir si un compte user existe
        String[] projection={
                CRReaderContract.CREntry._ID,
                CRReaderContract.CREntry.COLUMN_USER_USERNAME,
                CRReaderContract.CREntry.COLUMN_USER_SALT,
                CRReaderContract.CREntry.COLUMN_USER_FONCTION,
                CRReaderContract.CREntry.COLUMN_USER_CLEARPASS,
        };
        Cursor cursor = db.query(
                CRReaderContract.CREntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        String username = null;
        String salt =null;
        String fonction =null;
        String clearPass =null;
        Account user = new Account();
        while(cursor.moveToNext()) {
            int itemId = cursor.getInt(0);
            username=cursor.getString(1);
            salt=cursor.getString(2);
            fonction=cursor.getString(3);
            clearPass=cursor.getString(4);
            user.setId(itemId);
            user.setUsername(username);
            user.setSalt(salt);
            user.setFonction(fonction);
            user.setClearPass(clearPass);
        }
        //Fermeture DB
        cursor.close();
        return user;
    }
}
