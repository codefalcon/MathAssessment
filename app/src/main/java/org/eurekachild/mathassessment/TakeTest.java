package org.eurekachild.mathassessment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TakeTest extends Activity implements View.OnClickListener {

    private SoundPool soundPool;
    private int soundID[] = {0, 1, 2, 3, 4};
    boolean soundLoaded[] = new boolean[5];

    StudentInfo student;
    AssignmentInfo assignment;
    MathEngine me;
    QuestionAnswer displayedQuestion;

    boolean isQuotient;
    int quotient;

    AssignmentDbHelper dbHelper;

    public static class QuestionAnswer {
        private static int id = 7001;
        int questionID;
        int operand1;
        int operand2;
        int operator;
        int correctAnswer;
        int correctRemainder;

        QuestionAnswer(int op1, int op2, int oper) {
            questionID = id++;
            operand1 = op1;
            operand2 = op2;
            operator = oper;
            correctRemainder = 0;

            switch (operator) {
                case MainActivity.ADD:
                    correctAnswer = op1 + op2;
                    break;
                case MainActivity.SUB:
                    correctAnswer = op1 - op2;
                    break;
                case MainActivity.MUL:
                    correctAnswer = op1 * op2;
                    break;
                case MainActivity.DIV:
                    correctAnswer = op1 / op2;
                    correctRemainder = op1 % op2;
                    break;
            }
        }

        public boolean isCorrectResponse(int response) {
            return (response == correctAnswer);
        }

        public boolean isCorrectResponse(int response, int rem) {
            return (response == correctAnswer && rem == correctRemainder);
        }
    }

    public static class QuestionResponse {
        QuestionAnswer question;
        int response;
        int remainder = 0;
        boolean isCorrect;

        public boolean isCorrect() {
            return isCorrect;
        }

        QuestionResponse(QuestionAnswer qn, int studResponse) {
            question = qn;
            response = studResponse;
            remainder = 0;
            isCorrect = qn.isCorrectResponse(response);
        }

        QuestionResponse(QuestionAnswer qn, int quotient, int rem) {
            question = qn;
            response = quotient;
            remainder = rem;
            isCorrect = qn.isCorrectResponse(response, remainder);
        }
    }

    public static class StudentInfo {
        private static int id = 5001;
        int studentID;
        String studentName;

        StudentInfo(String name) {
            studentID = id++;
            studentName = name;
        }
    }

    public static class AssignmentInfo {
        private static int id = 10001;
        int assignmentID;
        StudentInfo student;
        ArrayList<QuestionResponse> studentResponse;
        int numAnswered;
        int numCorrect;

        AssignmentInfo(StudentInfo stud) {
            assignmentID = id++;
            student = stud;
            studentResponse = new ArrayList<QuestionResponse>();
            numAnswered = 0;
            numCorrect = 0;
        }

        public void addStudentResponse(QuestionResponse response) {
            studentResponse.add(response);
            if (response.isCorrect())
                numCorrect++;
            numAnswered++;
        }
    }

    @Override
    public void onClick(View v) {
        TextView text = (TextView) findViewById(R.id.tvAnswer);

        switch (v.getId()) {
            case R.id.button0:
                text.setText("0" + text.getText());
                break;
            case R.id.button1:
                text.setText("1" + text.getText());
                break;
            case R.id.button2:
                text.setText("2" + text.getText());
                break;
            case R.id.button3:
                text.setText("3" + text.getText());
                break;
            case R.id.button4:
                text.setText("4" + text.getText());
                break;
            case R.id.button5:
                text.setText("5" + text.getText());
                break;
            case R.id.button6:
                text.setText("6" + text.getText());
                break;
            case R.id.button7:
                text.setText("7" + text.getText());
                break;
            case R.id.button8:
                text.setText("8" + text.getText());
                break;
            case R.id.button9:
                text.setText("9" + text.getText());
                break;
            case R.id.buttonBkspc:
                String answer = (String) text.getText();
                if (!answer.isEmpty())
                    text.setText(answer.substring(1, answer.length()));
                break;
            case R.id.buttonSubmit:
                String ans = (String) text.getText();
                QuestionResponse qr;
                if (!ans.isEmpty()) {
                    if (displayedQuestion.operator == MainActivity.DIV) {
                        if (isQuotient) {
                            isQuotient = false;
                            TextView t1 = (TextView) findViewById(R.id.textview123);
                            t1.setText("← Remainder");
                            t1 = (TextView) findViewById(R.id.textview124);
                            t1.setText("Remainder →");
                            text.setText("");
                            quotient = Integer.parseInt(ans);
                            Toast.makeText(TakeTest.this, "Enter Remainder", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            qr = new QuestionResponse(displayedQuestion, quotient, Integer.parseInt(ans));
                            TextView t1 = (TextView) findViewById(R.id.textview123);
                            t1.setText("← Quotient");
                            t1 = (TextView) findViewById(R.id.textview124);
                            t1.setText("Quotient →");
                            quotient = Integer.parseInt(ans);
                            isQuotient = true;
                        }
                    } else
                        qr = new QuestionResponse(displayedQuestion, Integer.parseInt(ans));
                    assignment.addStudentResponse(qr);
                    QuestionAnswer qa;
                    if (qr.isCorrect) {
                        Toast.makeText(TakeTest.this, "Correct", Toast.LENGTH_SHORT).show();
                        playSound(1);
                        qa = me.getNextQuestion(true);
                    } else {
                        Toast.makeText(TakeTest.this, "Wrong", Toast.LENGTH_SHORT).show();
                        playSound(0);
                        qa = me.getNextQuestion(false);
                    }
                    if (qa != null) {
                        displayQuestion(qa);
                    } else {
                        Toast.makeText(TakeTest.this, "Well done!", Toast.LENGTH_SHORT).show();
                        // Start lengthy operation in a background thread
                        new Thread(new Runnable() {
                            public void run() {
                                writeAssignmentToDb(assignment, displayedQuestion.operator);
                                deleteAssignmentAboveLimit();
//                                readStudentInfoFromDb();
//                                while (mProgressStatus < 100) {
//                                    mProgressStatus = doWork();
//
//                                    // Update the progress bar
//                                    mHandler.post(new Runnable() {
//                                        public void run() {
//                                            mProgress.setProgress(mProgressStatus);
//                                        }
//                                    });
//                                }
                            }
                        }).start();
                        finish();
                    }
                    text.setText("");
                } else
                    Toast.makeText(TakeTest.this, "Please enter your answer", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void displayQuestion(QuestionAnswer qa) {
        displayedQuestion = qa;
        TextView textOp1 = (TextView) findViewById(R.id.tvOperand1);
        TextView textOp2 = (TextView) findViewById(R.id.tvOperand2);
        TextView textOper = (TextView) findViewById(R.id.tvOperator);

        textOp1.setText(Integer.toString(qa.operand1));
        textOp2.setText(Integer.toString(qa.operand2));
        String operator;
        switch (qa.operator) {
            case MainActivity.ADD:
                operator = "+ ";
                break;
            case MainActivity.SUB:
                operator = "- ";
                break;
            case MainActivity.MUL:
                operator = "x ";
                break;
            case MainActivity.DIV:
                operator = "÷ ";
                break;
            default:
                operator = " ";
                break;
        }
        textOper.setText(operator);
    }

    void playSound(int soundIndex) {
        // Getting the user sound settings
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;

        if (soundLoaded[soundIndex])
            soundPool.play(soundID[soundIndex], volume, volume, 1, 0, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        int operator = intent.getIntExtra(MainActivity.EXTRA_OPERATOR, 0);

        setContentView(R.layout.activity_taketest);

        //getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView text = (TextView) findViewById(R.id.textview1);
        switch (operator) {
            case MainActivity.ADD:
                text.setText("Addition - " + name);
                break;
            case MainActivity.SUB:
                text.setText("Subtraction - " + name);
                break;
            case MainActivity.MUL:
                text.setText("Multiplication - " + name);
                break;
            case MainActivity.DIV:
                text.setText("Division - " + name);
                TextView t1 = (TextView) findViewById(R.id.textview123);
                t1.setText("← Quotient");
                t1 = (TextView) findViewById(R.id.textview124);
                t1.setText("Quotient →");
                isQuotient = true;
                break;
        }

        setupListeners();
        setupSound();

        dbHelper = AssignmentDbHelper.getHelper(getApplicationContext());
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        //dbHelper.onDowngrade(db,1,2);

        beginTest(operator, name);
    }

    void setupListeners() {
        Button but = (Button) findViewById(R.id.button0);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button1);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button2);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button3);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button4);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button5);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button6);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button7);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button8);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.button9);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.buttonBkspc);
        but.setOnClickListener(this);
        but = (Button) findViewById(R.id.buttonSubmit);
        but.setOnClickListener(this);
    }

    void setupSound() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        } else {
            SoundPool.Builder sb = new SoundPool.Builder();
            sb.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            sb.setMaxStreams(2);
            soundPool = sb.build();
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                if (status == 0) {
                    for (int i = 0; i < 5; i++) {
                        if (sampleId == soundID[i]) {
                            soundLoaded[i] = true;
                            break;
                        }
                    }
                }
            }
        });
        soundID[0] = soundPool.load(this, R.raw.ting1sec, 1);
        soundID[1] = soundPool.load(this, R.raw.clap2sec, 1);
        soundID[2] = soundPool.load(this, R.raw.clapcheer2sec, 1);
        soundID[3] = soundPool.load(this, R.raw.claphighcheer2sec, 1);
        soundID[4] = soundPool.load(this, R.raw.fireworks3sec, 1);
    }

    private void beginTest(int operator, String name) {
        student = new StudentInfo(name);
        assignment = new AssignmentInfo(student);
        me = new MathEngine(operator);

        //writeStudentInfoToDb(student);

        QuestionAnswer qa = me.getNextQuestion(false);
        if (qa != null) {
            displayQuestion(qa);
        }
    }

    boolean writeAssignmentToDb(AssignmentInfo assignment, int operator) {
        Uri uri, returnUri;
        int id;

        //Write student info
        uri = AssignmentContentProvider.STUDENT_INFO_URI;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_ID, assignment.student.studentID);
        values.put(AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_NAME, assignment.student.studentName);

        returnUri = getContentResolver().insert(uri, values);
        id = Integer.parseInt(returnUri.getLastPathSegment());

        //Write assignment overview
        uri = AssignmentContentProvider.ASSIGNMENT_LIST_URI;

        // Create a new map of values, where column names are the keys
        ContentValues listvalues = new ContentValues();
        listvalues.put(AssignmentContract.AssignmentListTable.COLUMN_NAME_ASSIGN_ID, assignment.assignmentID);
        listvalues.put(AssignmentContract.AssignmentListTable.COLUMN_NAME_STUDENT_ID, id);
        listvalues.put(AssignmentContract.AssignmentListTable.COLUMN_NAME_OPERATOR, getOperatorName(operator));

        returnUri = getContentResolver().insert(uri, listvalues);

        id = Integer.parseInt(returnUri.getLastPathSegment());

        //Write assignment detail
        uri = AssignmentContentProvider.ASSIGNMENT_DETAIL_URI;
        int numEntries = assignment.studentResponse.size();
        ContentValues detailValues[] = new ContentValues[numEntries];

        // Create a new map of values, where column names are the keys
        QuestionResponse qr;
        for (int i = 0; i < numEntries; i++) {
            qr = assignment.studentResponse.get(i);
            detailValues[i] = new ContentValues();
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_ASSIGN_ID, id);
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_OP1, qr.question.operand1);
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_OPERATOR, getOperatorName(qr.question.operator));
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_OP2, qr.question.operand2);
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_RESPONSE, qr.response);
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_REMAINDER, qr.remainder);
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_CORRECT_RESPONSE, qr.question.correctAnswer);
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_CORRECT_REMAINDER, qr.question.correctRemainder);
            detailValues[i].put(AssignmentContract.AssignmentDetailTable.COLUMN_NAME_ISCORRECT, qr.isCorrect() ? 1 : 0);
        }
        getContentResolver().bulkInsert(uri, detailValues);

        return false;
    }

    String getOperatorName(int operator) {
        switch (operator) {
            case MainActivity.ADD:
                return "+";
            case MainActivity.SUB:
                return "-";
            case MainActivity.MUL:
                return "x";
            case MainActivity.DIV:
                return "÷";
        }
        return "";
    }

    boolean deleteAssignmentAboveLimit() {
        int LIMIT = 10;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                AssignmentContract.AssignmentListTable._ID,
                AssignmentContract.AssignmentListTable.COLUMN_NAME_STUDENT_ID
        };
        // How you want the results sorted in the resulting Cursor
        String sortOrder = AssignmentContract.AssignmentListTable._ID + " ASC";
        Cursor c = getContentResolver().query(AssignmentContentProvider.ASSIGNMENT_LIST_URI, projection, null, null, sortOrder);
        c.moveToFirst();
        int assignmentid, studentid;
        int count = c.getCount();
        //if number of entries is above LIMIT
        while (count-- > LIMIT && !c.isAfterLast()) {
            assignmentid = c.getInt(
                    c.getColumnIndexOrThrow(AssignmentContract.StudentInfoTable._ID)
            );
            studentid = c.getInt(
                    c.getColumnIndexOrThrow(AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_ID)
            );

            getContentResolver().delete(AssignmentContentProvider.ASSIGNMENT_DETAIL_URI,
                    AssignmentContract.AssignmentDetailTable.COLUMN_NAME_ASSIGN_ID + " = " + Integer.toString(assignmentid),
                    null
            );

            Uri assignmentUri = Uri.parse(AssignmentContentProvider.ASSIGNMENT_LIST_URI + "/" + Integer.toString(assignmentid));
            getContentResolver().delete(assignmentUri, null, null);

            Uri studUri = Uri.parse(AssignmentContentProvider.STUDENT_INFO_URI + "/" + Integer.toString(studentid));
            getContentResolver().delete(studUri, null, null);
            c.moveToNext();
        }
        c.close();
        return true;
    }

    void writeStudentInfoToDb(StudentInfo s) {

        SQLiteDatabase db = dbHelper.openDb();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_ID, s.studentID);
        values.put(AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_NAME, s.studentName);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                AssignmentContract.StudentInfoTable.TABLE_NAME,
                null,
                values);
    }

    void readStudentInfoFromDb() {
        SQLiteDatabase db = dbHelper.openDb();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                AssignmentContract.StudentInfoTable._ID,
                AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_ID,
                AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_NAME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                AssignmentContract.StudentInfoTable._ID + " DESC";
        Cursor c = db.query(
                AssignmentContract.StudentInfoTable.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        Log.w(TakeTest.class.getName(), "after query");
        c.moveToFirst();
        int id;
//        int studId;
        String name;
        while (!c.isAfterLast()) {
            id = c.getInt(
                    c.getColumnIndexOrThrow(AssignmentContract.StudentInfoTable._ID)
            );
//            studId = c.getInt(
//                    c.getColumnIndexOrThrow(AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_ID)
//            );
            name = c.getString(
                    c.getColumnIndexOrThrow(AssignmentContract.StudentInfoTable.COLUMN_NAME_STUDENT_NAME)
            );
            c.moveToNext();
            Log.w(TakeTest.class.getName(), id + ":" + name);
        }
        c.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_taketest, menu);
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
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
