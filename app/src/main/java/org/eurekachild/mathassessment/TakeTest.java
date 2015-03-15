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
import java.util.Random;


public class TakeTest extends Activity implements View.OnClickListener {

    private SoundPool soundPool;
    private int soundID[] = {0, 1, 2, 3, 4};
    boolean soundLoaded[] = new boolean[5];

    StudentInfo student;
    AssignmentInfo assignment;
    MathEngine me;
    QuestionAnswer displayedQuestion;

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
    }

    public static class QuestionResponse {
        QuestionAnswer question;
        int response;
        boolean isCorrect;

        public boolean isCorrect() {
            return isCorrect;
        }

        QuestionResponse(QuestionAnswer qn, int studResponse) {
            question = qn;
            response = studResponse;
            isCorrect = qn.isCorrectResponse(response);
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

    public static class MathEngine {
        int currentLevel;
        int currentType;
        int currentOperator;
        boolean isInit;

        Random r = new Random();

        MathEngine() {
            currentOperator = MainActivity.ADD;
            currentLevel = 0;
            currentType = 0;
            isInit = true;
        }

        MathEngine(int operator) {
            currentOperator = operator;
            currentLevel = 0;
            currentType = 0;
            isInit = true;
        }

        public void initialize(int operator) {
            currentOperator = operator;
            currentLevel = 0;
            currentType = 0;
            isInit = true;
        }

        public boolean hasNextQuestion(boolean isPrevRespCorrect) {
            if (!(isPrevRespCorrect || isInit))
                return false;
            return true;
        }

        //returns null if no Next Question
        public QuestionAnswer getNextQuestion(boolean isPrevRespCorrect) {
            switch (currentOperator) {
                case MainActivity.ADD:
                    return getNextAddQuestion(isPrevRespCorrect);
                case MainActivity.SUB:
                    return getNextSubQuestion(isPrevRespCorrect);
                case MainActivity.MUL:
                    return getNextMulQuestion(isPrevRespCorrect);
                case MainActivity.DIV:
                    break;
            }
            return null;
        }

        QuestionAnswer getNextAddQuestion(boolean isPrevRespCorrect) {
            QuestionAnswer newQA = null;
            if (isInit) {
                isInit = false;
                currentType = 1;
                newQA = generateAddQn(currentType);
            } else {
                switch (currentType) {
                    case 1://if Type 1, same level-> generate type 2
                    case 3: //type 3, same level->generate type 4
                    case 4: //type 4, same level->generate type 5
                        currentType++;
                        newQA = generateAddQn(currentType);
                        break;
                    case 2://type 2 has to be correct to generate type 3 + level up
                    case 5://type5 has to be correct to generate type 6 + level up
                    case 6://type6 has to be correct to generate type 7 + level up
                        if (isPrevRespCorrect) {
                            currentType++;
                            currentLevel++;
                            newQA = generateAddQn(currentType);
                        }
                        break;
                }
            }
            return newQA;
        }

        private QuestionAnswer generateAddQn(int type) {
            int op1;
            int op2;
            int digit;
            QuestionAnswer newQA = null;

            switch (type) {
                case 1://1D + 1D
                    op1 = getRandomNumber(1, 9);
                    op2 = getRandomNumber(1, 9);
                    newQA = new QuestionAnswer(op1, op2, MainActivity.ADD);
                    break;
                case 2://2d + 2d w/o carry
                    op1 = getRandomNumber(11, 88);
                    while (op1 % 10 == 0) {
                        op1 = getRandomNumber(11, 88);
                    }
                    op2 = getRandomNumberSum1D(op1 % 10);//Units digit
                    digit = getRandomNumberSum1D(op1 / 10);//Tens digit
                    op2 = digit * 10 + op2;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.ADD);
                    break;
                case 3://2D + 1D w/ or w/o carry
                    op1 = getRandomNumber(10, 99);
                    op2 = getRandomNumber(1, 9);
                    newQA = new QuestionAnswer(op1, op2, MainActivity.ADD);
                    break;
                case 4://2d + 2d with zero ending
                    op1 = getRandomNumber(10, 99);
                    if (op1 % 10 == 0) {//generate non zero ending
                        op2 = getRandomNumber(11, 99);
                        while (op2 % 10 == 0) {
                            op2 = getRandomNumber(11, 99);
                        }
                    } else {//generate zero ending
                        op2 = getRandomNumber(1, 9);
                        op2 = op2 * 10;
                    }
                    newQA = new QuestionAnswer(op1, op2, MainActivity.ADD);
                    break;
                case 5://2d + 2d w carry
                    op1 = getRandomNumber(11, 99);
                    op2 = getRandomNumberSum2D(op1 % 10);//units digit
                    digit = getRandomNumber(1, 9);
                    op2 = digit * 10 + op2;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.ADD);
                    break;
                case 6:
                    digit = getRandomNumber(2, 5);
                    newQA = generateAddQn(digit);
                    break;
                case 7:
                    op1 = getRandomNumber(1, 9);
                    op2 = getRandomNumberSum2D(op1);
                    digit = getRandomNumber(1, 9);
                    op1 = digit * 10 + op1;
                    op2 = getRandomNumberSum2D(digit) * 10 + op2;
                    digit = getRandomNumber(1, 9);
                    op1 = digit * 100 + op1;
                    op2 = getRandomNumberSum1D(digit) * 100 + op2;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.ADD);
                    break;
            }
            return newQA;
        }//generateAddQn

        private int getRandomNumber(int low, int high) {
            return (r.nextInt(high - low + 1) + low);
        }

        //Given a 1D +ve integer, generate a 1D random number such that the sum is also 1D
        private int getRandomNumberSum1D(int given) {
            if (given > 8 || given < 0) return 0;
            return (r.nextInt(9 - given) + 1);
        }

        //Given a 1D +ve integer, generate a 1D random number such that the sum is 2D always
        private int getRandomNumberSum2D(int given) {
            if (given > 9 || given < 0) return 0;
            int min = 10 - given;
            return getRandomNumber(min, 9);
        }

        QuestionAnswer getNextSubQuestion(boolean isPrevRespCorrect) {
            QuestionAnswer newQA = null;
            if (isInit) {
                isInit = false;
                currentType = 1;
                newQA = generateSubQn(currentType);
            } else {
                switch (currentType) {

                    case 3: //type 3, same level->generate type 4
                    case 4: //type 4, same level->generate type 5
                        currentType++;
                        newQA = generateSubQn(currentType);
                        break;
                    case 1://type 1 has to be correct to generate type 2 + level up
                    case 2://type 2 has to be correct to generate type 3 + level up
                    case 5://type5 has to be correct to generate type 6 + level up
                    case 6://type6 has to be correct to generate type 7 + level up
                        if (isPrevRespCorrect) {
                            currentType++;
                            currentLevel++;
                            newQA = generateSubQn(currentType);
                        }
                        break;
                }
            }
            return newQA;
        }//getNextSubQuestion

        private QuestionAnswer generateSubQn(int type) {
            int op1;
            int op2;
            int digit;
            QuestionAnswer newQA = null;

            switch (type) {
                case 1://1D - 1D
                    op1 = getRandomNumber(1, 9);
                    op2 = getRandomNumber(1, 9);
                    while (op1 == op2) {
                        op2 = getRandomNumber(1, 9);
                    }
                    if (op1 > op2)
                        newQA = new QuestionAnswer(op1, op2, MainActivity.SUB);
                    else
                        newQA = new QuestionAnswer(op2, op1, MainActivity.SUB);
                    break;
                case 2://2d-1d = 1d
                    op1 = getRandomNumber(10, 18);
                    digit = op1 - 10 + 1;
                    op2 = getRandomNumber(digit, 9);
                    newQA = new QuestionAnswer(op1, op2, MainActivity.SUB);
                    break;
                case 3://2d-2d w/o borrow
                    while (true) {
                        op1 = getRandomNumber(11, 99);
                        while (op1 % 10 == 0)
                            op1 = getRandomNumber(11, 99);
                        op2 = getRandomNumber(1, op1 % 10);
                        digit = getRandomNumber(1, op1 / 10);
                        op2 = digit * 10 + op2;
                        if (op1 != op2) break;
                    }
                    newQA = new QuestionAnswer(op1, op2, MainActivity.SUB);
                    break;
                case 4://2d-2d zero ending
                    op1 = getRandomNumber(11, 99);
                    while (op1 % 10 == 0)
                        op1 = getRandomNumber(11, 99);
                    op2 = getRandomNumber(1, op1 / 10);
                    op2 *= 10;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.SUB);
                    break;
                case 5://2d-2d w borrow
                    op1 = getRandomNumber(21, 88);
                    op2 = getRandomNumber((op1 % 10) + 1, 9);
                    digit = getRandomNumber(1, (op1 / 10) - 1);
                    op2 = digit * 10 + op2;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.SUB);
                    break;
                case 6://3,4,5 or any 2d-1d
                    digit = getRandomNumber(3, 6);
                    if (digit == 6) {
                        op1 = getRandomNumber(10, 99);
                        op2 = getRandomNumber(1, 9);
                        newQA = new QuestionAnswer(op1, op2, MainActivity.SUB);
                    } else {
                        newQA = generateSubQn(digit);
                    }
                    break;
                case 7://3d-3d with two levels borrow
                    op1 = getRandomNumber(220, 998);
                    op2 = getRandomNumber((op1 % 10) + 1, 9);
                    digit = getRandomNumber(((op1 % 100) / 10), 9);
                    op2 = digit * 10 + op2;
                    digit = getRandomNumber(1, (op1 / 100) - 1);
                    op2 = digit * 100 + op2;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.SUB);
                    break;
            }
            return newQA;
        }//generateSubQn

        QuestionAnswer getNextMulQuestion(boolean isPrevRespCorrect) {
            QuestionAnswer newQA = null;
            if (isInit) {
                isInit = false;
                currentType = 1;
                newQA = generateMulQn(currentType);
            } else {
                switch (currentType) {

                    case 2://type 2, same level->generate type 3
                    case 4: //type 4, same level->generate type 5
                        currentType++;
                        newQA = generateMulQn(currentType);
                        break;
                    case 1://type 1 has to be correct to generate type 2 + level up
                    case 3: //type 3 has to be correct to generate type 4 + level up
                    case 5://type 5 has to be correct to generate type 6 + level up
                    case 6://type 6 has to be correct to generate type 7 + level up
                        if (isPrevRespCorrect) {
                            currentType++;
                            currentLevel++;
                            newQA = generateMulQn(currentType);
                        }
                        break;
                }
            }
            return newQA;
        }//getNextMulQuestion

        private QuestionAnswer generateMulQn(int type) {
            int op1;
            int op2;
            int digit;
            QuestionAnswer newQA = null;

            switch (type) {
                case 1://1D x 1D <= 5
                    op1 = getRandomNumber(1, 5);
                    op2 = getRandomNumber(1, 5);
                    newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                    break;
                case 2://1d x 1d
                    op1 = getRandomNumber(1, 9);
                    op2 = getRandomNumber(1, 9);
                    newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                    break;
                case 3://2d x 1d w/o carry
                    op1 = getRandomNumber(1, 5);
                    op2 = getRandomMul1D(op1);
                    op1 = getRandomNumber(1, 9) * 10 + op1;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                    break;
                case 4://2d x 1d with carry
                    op1 = getRandomNumber(2, 9);
                    op2 = getRandomMul2D(op1);
                    op1 = getRandomNumber(1, 9) * 10 + op1;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                    break;
                case 5://2dx2d or 2dx1d w zero ending
                    op1 = getRandomNumber(1, 9);
                    op1 = op1 * 10;
                    op2 = getRandomNumber(1, 99);
                    newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                    break;
                case 6://2d x 2d w carry
                    op1 = getRandomNumber(2, 9);
                    op2 = getRandomMul2D(op1);
                    digit = getRandomNumber(2, 9);
                    op1 = digit * 10 + op1;
                    digit = getRandomMul2D(digit);
                    op2 = digit * 10 + op2;
                    newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                    break;
                case 7://random from 4,5,6
                    digit = getRandomNumber(4, 6);
                    newQA = generateMulQn(digit);
                    break;
            }
            return newQA;
        }//generateMulQn

        //Given a 1D +ve integer, generate a 1D random number such that the mul is also 1D
        int getRandomMul1D(int digit) {
            int num = 0;
            switch (digit) {
                case 1:
                    num = getRandomNumber(1, 9);
                    break;
                case 2:
                    num = getRandomNumber(1, 4);
                    break;
                case 3:
                    num = getRandomNumber(1, 3);
                    break;
                case 4:
                    num = getRandomNumber(1, 2);
                    break;
                default:
                    if (digit < 10)
                        num = 1;
                    else
                        num = 0;//error condition
                    break;
            }
            return num;
        }

        //Given a 1D +ve integer, generate a 1D random number such that the mul is always 2D
        int getRandomMul2D(int digit) {
            int num = 0;
            switch (digit) {
                case 1:
                    num = 0;//error condition
                    break;
                case 2:
                    num = getRandomNumber(5, 9);
                    break;
                case 3:
                    num = getRandomNumber(4, 9);
                    break;
                case 4:
                    num = getRandomNumber(3, 9);
                    break;
                default:
                    if (digit < 10)
                        num = getRandomNumber(2, 9);
                    else
                        num = 0;//error condition
                    break;
            }
            return num;
        }
    }//MathEngine

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

                if (!ans.isEmpty()) {
                    QuestionResponse qr = new QuestionResponse(displayedQuestion, Integer.parseInt(ans));
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
