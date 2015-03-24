package org.eurekachild.mathassessment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by safiq on 21-03-2015.
 */
public class AssignmentListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;

    boolean isDualPane;

    // Container Activity must implement this interface
    public interface OnAssignmentSelectedListener {
        public void onAssignmentSelected(long id, String operator);
    }

    OnAssignmentSelectedListener assignmentSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            assignmentSelectedListener = (OnAssignmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_list,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // We have a menu item to show in action bar.
        setHasOptionsMenu(false);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.resultsframe);
        isDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (isDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.assignment_list_row, null,
                new String[]{
                        AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_NAME,
                        AssignmentContract.AssignmentListTable.COLUMN_NAME_OPERATOR},
                new int[]{R.id.tvStudentName, R.id.tvOperator}, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.i(AssignmentListFragment.class.getName(), "Item clicked: " + id);
        //Uri baseUri = AssignmentContentProvider.ASSIGNMENT_DETAIL_URI;
        //assignmentSelectedListener.onAssignmentSelected(ContentUris.withAppendedId(baseUri, id));
        //getListView().setItemChecked(position,true);
        super.onListItemClick(l, v, position, id);

        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);
        int index = c.getColumnIndex(AssignmentContract.AssignmentListTable.COLUMN_NAME_OPERATOR);
        assignmentSelectedListener.onAssignmentSelected(id, c.getString(index));
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
//        String[] projection = {
//                AssignmentContract.AssignmentListTable._ID,
//                AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_NAME,
//                AssignmentContract.AssignmentListTable.COLUMN_NAME_OPERATOR
//        };
        Uri baseUri = AssignmentContentProvider.STUDENT_LIST_URI;


        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        return new CursorLoader(getActivity(), baseUri,
                null, null, null,
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
