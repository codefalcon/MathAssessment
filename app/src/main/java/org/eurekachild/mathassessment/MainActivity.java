package org.eurekachild.mathassessment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {

    public static final int ADD = 1001;
    public static final int SUB = 1002;
    public static final int MUL = 1003;
    public static final int DIV = 1004;

    public static final String EXTRA_OPERATOR = "org.eurekachild.mathassessment.OPERATOR";
    public static final String EXTRA_NAME = "org.eurekachild.mathassessment.NAME";

    public void onClick(View v) {
        // Perform action on click
        EditText name = (EditText) findViewById(R.id.etName);
        boolean isNameEmpty = false;

        switch (v.getId()) {
            case R.id.butAdd:
                if (name.getText().toString().isEmpty()) {
                    isNameEmpty = true;
                    break;
                }
                launchTakeTestActivity(name.getText().toString(), ADD);
                break;
            case R.id.butSub:
                if (name.getText().toString().isEmpty()) {
                    isNameEmpty = true;
                    break;
                }
                launchTakeTestActivity(name.getText().toString(), SUB);
                break;
            case R.id.butMul:
                if (name.getText().toString().isEmpty()) {
                    isNameEmpty = true;
                    break;
                }
                launchTakeTestActivity(name.getText().toString(), MUL);
                break;
            case R.id.butDiv:
                if (name.getText().toString().isEmpty()) {
                    isNameEmpty = true;
                    break;
                }
                launchTakeTestActivity(name.getText().toString(), DIV);
                break;
            case R.id.butSummary:
                Intent intentSummary = new Intent(this, Summary.class);
                startActivity(intentSummary);
                break;
            case R.id.butExit:
                finish();
                break;
        }
        if (isNameEmpty) {
            Toast.makeText(MainActivity.this, "Please enter student name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchTakeTestActivity(String name, int operator) {
        Intent intentTakeTest = new Intent(this, TakeTest.class);
        intentTakeTest.putExtra(EXTRA_OPERATOR, operator);
        intentTakeTest.putExtra(EXTRA_NAME, name);
        startActivity(intentTakeTest);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add = (Button) findViewById(R.id.butAdd);
        Button sub = (Button) findViewById(R.id.butSub);
        Button mul = (Button) findViewById(R.id.butMul);
        Button div = (Button) findViewById(R.id.butDiv);
        Button summary = (Button) findViewById(R.id.butSummary);
        Button exit = (Button) findViewById(R.id.butExit);

        add.setOnClickListener(this);
        sub.setOnClickListener(this);
        mul.setOnClickListener(this);
        div.setOnClickListener(this);
        summary.setOnClickListener(this);
        exit.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
