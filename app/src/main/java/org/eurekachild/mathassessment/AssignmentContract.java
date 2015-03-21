package org.eurekachild.mathassessment;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by safiq on 17-03-2015.
 */
public final class AssignmentContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    AssignmentContract() {
    }

    /* Inner classes that defines the table contents */

    public static class StudentInfoTable implements BaseColumns {
        public static final String TABLE_NAME = "studentinfo";
        public static final String COLUMN_NAME_STUDENT_ID = "studentid";
        public static final String COLUMN_NAME_STUDENT_NAME = "studentname";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_NAME
                + "("
                + _ID + " integer primary key autoincrement, "
                + COLUMN_NAME_STUDENT_ID + " integer not null, "
                + COLUMN_NAME_STUDENT_NAME + " text not null"
                + ");";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(StudentInfoTable.DATABASE_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            Log.w(StudentInfoTable.class.getName(), "Upgrading database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + StudentInfoTable.TABLE_NAME);
            //onCreate(database);
        }
    }

    public static class AssignmentListTable implements BaseColumns {
        public static final String TABLE_NAME = "assignmentlist";
        public static final String COLUMN_NAME_ASSIGN_ID = "assignmentid";
        public static final String COLUMN_NAME_STUDENT_ID = "studentid";
        public static final String COLUMN_NAME_OPERATOR = "operator";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_NAME
                + "("
                + _ID + " integer primary key autoincrement, "
                + COLUMN_NAME_ASSIGN_ID + " integer not null, "
                + COLUMN_NAME_STUDENT_ID + " integer not null, "
                + COLUMN_NAME_OPERATOR + " text not null, "
                + "FOREIGN KEY(" + COLUMN_NAME_STUDENT_ID + ") REFERENCES "
                + StudentInfoTable.TABLE_NAME + "(" + StudentInfoTable._ID + ")"
                + ");";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(AssignmentListTable.DATABASE_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            Log.w(AssignmentListTable.class.getName(), "Upgrading database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + AssignmentListTable.TABLE_NAME);
            //onCreate(database);
        }
    }

    public static class AssignmentDetailTable implements BaseColumns {
        public static final String TABLE_NAME = "assignmentdetail";
        public static final String COLUMN_NAME_ASSIGN_ID = "assignmentid";
        public static final String COLUMN_NAME_OP1 = "operand1";
        public static final String COLUMN_NAME_OPERATOR = "operator";
        public static final String COLUMN_NAME_OP2 = "operand2";
        public static final String COLUMN_NAME_RESPONSE = "response";
        public static final String COLUMN_NAME_REMAINDER = "remainder";
        public static final String COLUMN_NAME_CORRECT_RESPONSE = "corresponse";
        public static final String COLUMN_NAME_CORRECT_REMAINDER = "corremainder";
        public static final String COLUMN_NAME_ISCORRECT = "iscorrect";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_NAME
                + "("
                + _ID + " integer primary key autoincrement, "
                + COLUMN_NAME_ASSIGN_ID + " integer not null, "
                + COLUMN_NAME_OP1 + " integer not null, "
                + COLUMN_NAME_OPERATOR + " text not null, "
                + COLUMN_NAME_OP2 + " integer not null, "
                + COLUMN_NAME_RESPONSE + " integer not null, "
                + COLUMN_NAME_REMAINDER + " integer not null, "
                + COLUMN_NAME_CORRECT_RESPONSE + " integer not null, "
                + COLUMN_NAME_CORRECT_REMAINDER + " integer not null, "
                + COLUMN_NAME_ISCORRECT + " integer not null, "
                + "FOREIGN KEY(" + COLUMN_NAME_ASSIGN_ID + ") REFERENCES "
                + AssignmentListTable.TABLE_NAME + "(" + AssignmentListTable._ID + ")"
                + ");";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(AssignmentDetailTable.DATABASE_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            Log.w(AssignmentDetailTable.class.getName(), "Upgrading database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + AssignmentDetailTable.TABLE_NAME);
            //onCreate(database);
        }
    }
}
