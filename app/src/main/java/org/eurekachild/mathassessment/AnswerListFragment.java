package org.eurekachild.mathassessment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by safiq on 21-03-2015.
 */
public class AnswerListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private class AnswerCursorAdapter extends SimpleCursorAdapter {
        private String operator;

        AnswerCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, String operator) {
            super(context, layout, c, from, to, flags);
            this.operator = operator;
        }

        public void setOperator(String oper) {
            operator = oper;
        }

        @Override
        public void setViewImage(@NonNull ImageView v, String value) {

            if (value.equals("1")) {
                Drawable tick = getResources().getDrawable(R.drawable.tick);
                v.setImageDrawable(tick);
            } else {
                Drawable cross = getResources().getDrawable(R.drawable.cross2);
                v.setImageDrawable(cross);
            }
        }

        @Override
        public void setViewText(@NonNull TextView v, String text) {
            switch (v.getId()) {
                case R.id.tvAnsRem:
                    if (operator.equals("÷")) {
                        v.setVisibility(View.VISIBLE);
                        super.setViewText(v, "(" + text + ")");
                    } else {
                        v.setVisibility(View.INVISIBLE);
                        super.setViewText(v, "");
                    }
                    break;
                default:
                    super.setViewText(v, text);
                    break;
            }
        }
    }

    // This is the Adapter being used to display the list's data.
    AnswerCursorAdapter mAdapter;
    String operator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer_list,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(false);

        Bundle arguments = getArguments();
        operator = arguments.getString("operator");

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new AnswerCursorAdapter(getActivity(),
                R.layout.answer_list_row, null,
                new String[]{
                        AssignmentContract.AssignmentDetailTable.COLUMN_NAME_OP1,
                        AssignmentContract.AssignmentDetailTable.COLUMN_NAME_OPERATOR,
                        AssignmentContract.AssignmentDetailTable.COLUMN_NAME_OP2,
                        AssignmentContract.AssignmentDetailTable.COLUMN_NAME_RESPONSE,
                        AssignmentContract.AssignmentDetailTable.COLUMN_NAME_REMAINDER,
                        AssignmentContract.AssignmentDetailTable.COLUMN_NAME_ISCORRECT
                },
                new int[]{R.id.tvAnsOp1, R.id.tvAnsOper, R.id.tvAnsOp2,
                        R.id.tvAnsResp, R.id.tvAnsRem,
                        R.id.tvAnsIcon}, 0, operator);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, arguments, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        super.onListItemClick(l, v, position, id);
        Log.i(AnswerListFragment.class.getName(), "Item clicked: " + id);
        setCorrectAnswer(position);
    }

    public void setCorrectAnswer(int position) {

        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        int index1 = c.getColumnIndex(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_CORRECT_RESPONSE);
        int index2 = c.getColumnIndex(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_CORRECT_REMAINDER);

        StringBuilder ans = new StringBuilder();
        ans.append("Correct Answer: ").append(c.getInt(index1));
        if (operator.compareTo("÷") == 0)
            ans.append("(").append(c.getInt(index2)).append(")");
        ans.append("\n");
        ans.append(getAnswerType(operator, position));

        try {
            TextView text = (TextView) getView().findViewById(R.id.tvCorrectAnswer);
            //text.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            text.setText(ans);
        } catch (NullPointerException e) {

        }
    }

    public String getAnswerType(String operator, int position) {
        String addType[] = {"1D + 1D", "2D + 2D w/o carry", "2D+1D with or w/o carry",
                "2D+2D (with zero ending )", "2D+2D with carry", "Random 2D problem", "3D + 3D with 2 carry overs"};
        String subType[] = {"1D-1D", "Special 2D-1D (with answer in 1D)", "2D-2D w/o borrow",
                "2D-2D with zero ending", "2D-2D with borrow", "Random 2D problem", "3D-3D with two levels borrow"};
        String mulType[] = {"1Dx1D (<=5)", "Any 1Dx1D", "2Dx1D w/o carry", "2Dx1D with carry",
                "2Dx1D or 2Dx2D with zero ending", " 2Dx2D problem with carry", "Random 2D x 2D (or 2Dx1D)"};
        String divType[] = {"2D/1D no remainder", "2D/1D with remainder", "3D/1D without remainder", "3D/1D with remainder",
                "3D/1D with zero in quotient at the end", "3D/1D with zero in quotient in the middle",
                "3D/1D with zero in quotient and dividend "};

        switch (operator) {
            case "+":
                return addType[position];
            case "-":
                return subType[position];
            case "x":
                return mulType[position];
            case "÷":
                return divType[position];
            default:
                return "";
        }
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
        String selection = AssignmentContract.AssignmentDetailTable.COLUMN_NAME_ASSIGN_ID + "=?";
        Uri uri = AssignmentContentProvider.ASSIGNMENT_DETAIL_URI;

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        return new CursorLoader(getActivity(), uri,
                null, selection, new String[]{Long.toString(args.getLong("assignmentId", 0))},
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        try {
            TextView text = (TextView) getView().findViewById(R.id.tvCorrectAnswer);
            //text.setAlpha(0.54f);
            //text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            text.setText(R.string.select_answer);
        } catch (NullPointerException e) {

        }
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    public void onAssignmentChange(Bundle args) {
        operator = args.getString("operator");
        mAdapter.setOperator(operator);
        getLoaderManager().restartLoader(0, args, this);
    }
}
