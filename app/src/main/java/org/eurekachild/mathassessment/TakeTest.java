package org.eurekachild.mathassessment;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
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
            if (response == correctAnswer)
                return true;
            else
                return false;
        }

        public boolean isCorrectResponse(int response, int rem) {
            if (response == correctAnswer && rem == correctRemainder)
                return true;
            else
                return false;
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
                            Toast.makeText(TakeTest.this, "Enter Remainder", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(TakeTest.this, "Well done!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    text.setText("");
                } else
                    Toast.makeText(TakeTest.this, "Please enter your answer", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void beginTest(int operator, String name) {
        student = new StudentInfo(name);
        assignment = new AssignmentInfo(student);
        me = new MathEngine(operator);
        QuestionAnswer qa = me.getNextQuestion(false);
        if (qa != null) {
            displayQuestion(qa);
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
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_taketest, menu);
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
