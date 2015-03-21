package org.eurekachild.mathassessment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by safiq on 18-03-2015.
 */
public class AssignmentDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Assignment.db";

    private static AssignmentDbHelper instance = null;
    private static SQLiteDatabase db = null;

    public static synchronized AssignmentDbHelper getHelper(Context context) {
        if (instance == null)
            instance = new AssignmentDbHelper(context);

        return instance;
    }

    public AssignmentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase openDb() {
        if (db == null)
            db = instance.getWritableDatabase();
        return db;
    }

    public void onCreate(SQLiteDatabase db) {
        //order of creation is important based on foreign key constraints
        AssignmentContract.StudentInfoTable.onCreate(db);
        AssignmentContract.AssignmentListTable.onCreate(db);
        AssignmentContract.AssignmentDetailTable.onCreate(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        AssignmentContract.AssignmentDetailTable.onUpgrade(db, oldVersion, newVersion);
        AssignmentContract.AssignmentListTable.onUpgrade(db, oldVersion, newVersion);
        AssignmentContract.StudentInfoTable.onUpgrade(db, oldVersion, newVersion);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
