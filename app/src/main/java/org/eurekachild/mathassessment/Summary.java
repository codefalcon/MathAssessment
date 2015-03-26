package org.eurekachild.mathassessment;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;


public class Summary extends Activity implements AssignmentListFragment.OnAssignmentSelectedListener {

    // Create a new Fragment to be placed in the activity layout
    AnswerListFragment answerListFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
//        ActionBar ab = getActionBar();
//        if (ab != null)
//            ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.home) {
            //navigateUpFromSameTask(this);
            finish();
            //  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAssignmentSelected(long id, String operator) {

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putLong("assignmentId", id);
        args.putString("operator", operator);

        if (answerListFragment == null) {
            //first time
            answerListFragment = new AnswerListFragment();

            answerListFragment.setArguments(args);

            if (findViewById(R.id.resultsframe) != null) {
                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .replace(R.id.resultsframe, answerListFragment).commit();
            }
        } else {
            //answerListFragment.setArguments(args);
            answerListFragment.onAssignmentChange(args);
        }
    }

}
