package org.eurekachild.mathassessment;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by safiq on 18-03-2015.
 */
public class AssignmentContentProvider extends ContentProvider {

    //database helper
    private AssignmentDbHelper dbHelper;

    //used for the UriMatcher
    private static final int STUDENT_INFO = 10;
    private static final int STUDENT_INFO_ID = 20;

    private static final int ASSIGNMENT_LIST = 30;
    private static final int ASSIGNMENT_LIST_ID = 40;

    private static final int ASSIGNMENT_DETAIL = 50;
    private static final int ASSIGNMENT_DETAIL_ID = 60;

    private static final int STUDENT_LIST = 70; //for student and assignment list union query

    private static final String AUTHORITY = "org.eurekachild.mathassessment.provider.assignmentcontentprovider";


    public static final Uri STUDENT_INFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + AssignmentContract.StudentInfoTable.TABLE_NAME);
    public static final Uri ASSIGNMENT_LIST_URI = Uri.parse("content://" + AUTHORITY
            + "/" + AssignmentContract.AssignmentListTable.TABLE_NAME);
    public static final Uri ASSIGNMENT_DETAIL_URI = Uri.parse("content://" + AUTHORITY
            + "/" + AssignmentContract.AssignmentDetailTable.TABLE_NAME);
    public static final Uri STUDENT_LIST_URI = Uri.parse("content://" + AUTHORITY
            + "/" + AssignmentContract.StudentInfoTable.TABLE_NAME + AssignmentContract.AssignmentListTable.TABLE_NAME);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY,
                AssignmentContract.StudentInfoTable.TABLE_NAME, STUDENT_INFO);
        sUriMatcher.addURI(AUTHORITY,
                AssignmentContract.StudentInfoTable.TABLE_NAME + "/#", STUDENT_INFO_ID);
        sUriMatcher.addURI(AUTHORITY,
                AssignmentContract.AssignmentListTable.TABLE_NAME, ASSIGNMENT_LIST);
        sUriMatcher.addURI(AUTHORITY,
                AssignmentContract.AssignmentListTable.TABLE_NAME + "/#", ASSIGNMENT_LIST_ID);
        sUriMatcher.addURI(AUTHORITY,
                AssignmentContract.AssignmentDetailTable.TABLE_NAME, ASSIGNMENT_DETAIL);
        sUriMatcher.addURI(AUTHORITY,
                AssignmentContract.AssignmentDetailTable.TABLE_NAME + "/#", ASSIGNMENT_DETAIL_ID);
        sUriMatcher.addURI(AUTHORITY,
                AssignmentContract.StudentInfoTable.TABLE_NAME
                        + AssignmentContract.AssignmentListTable.TABLE_NAME,
                STUDENT_LIST);
    }

    @Override
    public boolean onCreate() {
        dbHelper = AssignmentDbHelper.getHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);

        String baseDir = "vnd.android.cursor.dir/vnd." + AUTHORITY + ".";
        String baseItem = "vnd.android.cursor.item/vnd." + AUTHORITY + ".";

        switch (uriType) {
            case STUDENT_INFO:
                return baseDir + AssignmentContract.StudentInfoTable.TABLE_NAME;
            case STUDENT_INFO_ID:
                return baseItem + AssignmentContract.StudentInfoTable.TABLE_NAME;
            case ASSIGNMENT_LIST:
                return baseDir + AssignmentContract.AssignmentListTable.TABLE_NAME;
            case ASSIGNMENT_LIST_ID:
                return baseItem + AssignmentContract.AssignmentListTable.TABLE_NAME;
            case ASSIGNMENT_DETAIL:
                return baseDir + AssignmentContract.AssignmentDetailTable.TABLE_NAME;
            case ASSIGNMENT_DETAIL_ID:
                return baseItem + AssignmentContract.AssignmentDetailTable.TABLE_NAME;
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteDatabase db = dbHelper.openDb();
        Cursor cursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // check if the caller has requested a column which does not exists
        //checkColumns(projection);
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case STUDENT_INFO:
                queryBuilder.setTables(AssignmentContract.StudentInfoTable.TABLE_NAME);
                if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "_ID ASC";
                break;
            case STUDENT_INFO_ID:
                queryBuilder.setTables(AssignmentContract.StudentInfoTable.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(AssignmentContract.StudentInfoTable._ID + "="
                        + uri.getLastPathSegment());
                break;
            case ASSIGNMENT_LIST:
                queryBuilder.setTables(AssignmentContract.AssignmentListTable.TABLE_NAME);
                if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "_ID ASC";
                break;
            case ASSIGNMENT_LIST_ID:
                queryBuilder.setTables(AssignmentContract.AssignmentListTable.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(AssignmentContract.AssignmentListTable._ID + "="
                        + uri.getLastPathSegment());
                break;
            case ASSIGNMENT_DETAIL:
                queryBuilder.setTables(AssignmentContract.AssignmentDetailTable.TABLE_NAME);
                if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "_ID ASC";
                break;
            case ASSIGNMENT_DETAIL_ID:
                queryBuilder.setTables(AssignmentContract.AssignmentDetailTable.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(AssignmentContract.AssignmentDetailTable._ID + "="
                        + uri.getLastPathSegment());
                break;
            case STUDENT_LIST:
                final String STUDENT_LIST_QUERY =
                        "SELECT a.*, s."
                                + AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_NAME
                                + " FROM "
                                + AssignmentContract.AssignmentListTable.TABLE_NAME + " a INNER JOIN "
                                + AssignmentContract.StudentInfoTable.TABLE_NAME + " s ON a."
                                + AssignmentContract.AssignmentListTable.COLUMN_NAME_STUDENT_ID + "=s."
                                + AssignmentContract.StudentInfoTable._ID;
                //Log.w(AssignmentContentProvider.class.getName(),STUDENT_LIST_QUERY);
                cursor = db.rawQuery(STUDENT_LIST_QUERY, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (cursor == null)
            cursor = queryBuilder.query(db, projection, selection,
                    selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.openDb();
        long id = 0;

        if (values == null)
            values = new ContentValues();

        switch (uriType) {
            case STUDENT_INFO:
                id = db.insert(AssignmentContract.StudentInfoTable.TABLE_NAME, null, values);
                break;
            case ASSIGNMENT_LIST:
                id = db.insert(AssignmentContract.AssignmentListTable.TABLE_NAME, null, values);
                break;
            case ASSIGNMENT_DETAIL:
                id = db.insert(AssignmentContract.AssignmentDetailTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            //return Uri.parse(BASE_PATH + "/" + id);
            return ContentUris.withAppendedId(STUDENT_INFO_URI, id);
        } else
            throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues values[]) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.openDb();
        long id = 0;

        if (values == null) {
            values = new ContentValues[1];
        }

        int insertCount = 0;

        db.beginTransaction();

        switch (uriType) {
            case STUDENT_INFO:
                for (ContentValues value : values) {
                    id = db.insert(AssignmentContract.StudentInfoTable.TABLE_NAME, null, value);
                    if (id != -1) insertCount++;
                }
                break;
            case ASSIGNMENT_LIST:
                for (ContentValues value : values) {
                    id = db.insert(AssignmentContract.AssignmentListTable.TABLE_NAME, null, value);
                    if (id != -1) insertCount++;
                }
                break;
            case ASSIGNMENT_DETAIL:
                for (ContentValues value : values) {
                    id = db.insert(AssignmentContract.AssignmentDetailTable.TABLE_NAME, null, value);
                    if (id != -1) insertCount++;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);
        //return Uri.parse(BASE_PATH + "/" + id);
        return insertCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.openDb();
        int rowsUpdated = 0;
        String id;
        switch (uriType) {
            case STUDENT_INFO:
                rowsUpdated = db.update(AssignmentContract.StudentInfoTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case STUDENT_INFO_ID:
                id = uri.getLastPathSegment();
                if (selection == null || selection.isEmpty()) {
                    rowsUpdated = db.update(AssignmentContract.StudentInfoTable.TABLE_NAME,
                            values,
                            AssignmentContract.StudentInfoTable._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(AssignmentContract.StudentInfoTable.TABLE_NAME,
                            values,
                            AssignmentContract.StudentInfoTable._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case ASSIGNMENT_LIST:
                rowsUpdated = db.update(AssignmentContract.AssignmentListTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ASSIGNMENT_LIST_ID:
                id = uri.getLastPathSegment();
                if (selection == null || selection.isEmpty()) {
                    rowsUpdated = db.update(AssignmentContract.AssignmentListTable.TABLE_NAME,
                            values,
                            AssignmentContract.AssignmentListTable._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(AssignmentContract.AssignmentListTable.TABLE_NAME,
                            values,
                            AssignmentContract.AssignmentListTable._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case ASSIGNMENT_DETAIL:
                rowsUpdated = db.update(AssignmentContract.AssignmentDetailTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ASSIGNMENT_DETAIL_ID:
                id = uri.getLastPathSegment();
                if (selection == null || selection.isEmpty()) {
                    rowsUpdated = db.update(AssignmentContract.AssignmentDetailTable.TABLE_NAME,
                            values,
                            AssignmentContract.AssignmentDetailTable._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(AssignmentContract.AssignmentDetailTable.TABLE_NAME,
                            values,
                            AssignmentContract.AssignmentDetailTable._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.openDb();

        int rowsDeleted = 0;
        String id;

        switch (uriType) {
            case STUDENT_INFO:
                rowsDeleted = db.delete(AssignmentContract.StudentInfoTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case STUDENT_INFO_ID:
                id = uri.getLastPathSegment();
                if (selection == null || selection.isEmpty()) {
                    rowsDeleted = db.delete(AssignmentContract.StudentInfoTable.TABLE_NAME,
                            AssignmentContract.StudentInfoTable._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(AssignmentContract.StudentInfoTable.TABLE_NAME,
                            AssignmentContract.StudentInfoTable._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case ASSIGNMENT_LIST:
                rowsDeleted = db.delete(AssignmentContract.AssignmentListTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case ASSIGNMENT_LIST_ID:
                id = uri.getLastPathSegment();
                if (selection == null || selection.isEmpty()) {
                    rowsDeleted = db.delete(AssignmentContract.AssignmentListTable.TABLE_NAME,
                            AssignmentContract.AssignmentListTable._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(AssignmentContract.AssignmentListTable.TABLE_NAME,
                            AssignmentContract.AssignmentListTable._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case ASSIGNMENT_DETAIL:
                rowsDeleted = db.delete(AssignmentContract.AssignmentDetailTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case ASSIGNMENT_DETAIL_ID:
                id = uri.getLastPathSegment();
                if (selection == null || selection.isEmpty()) {
                    rowsDeleted = db.delete(AssignmentContract.AssignmentDetailTable.TABLE_NAME,
                            AssignmentContract.AssignmentDetailTable._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(AssignmentContract.AssignmentDetailTable.TABLE_NAME,
                            AssignmentContract.AssignmentDetailTable._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }
}
