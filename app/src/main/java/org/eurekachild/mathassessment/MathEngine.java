package org.eurekachild.mathassessment;

import android.util.Log;

import java.util.Random;

import static org.eurekachild.mathassessment.TakeTest.QuestionAnswer;

/**
 * Created by safiq on 15-03-2015.
 */
public class MathEngine {
    int currentLevel;
    int currentType;
    int currentOperator;
    boolean isInit;
    boolean isLevelPass;

    Random r = new Random();

    MathEngine(int operator) {
        currentOperator = operator;
        currentLevel = 0;
        currentType = 0;
        isInit = true;
        isLevelPass = true;
    }

    public void initialize(int operator) {
        currentOperator = operator;
        currentLevel = 0;
        currentType = 0;
        isInit = true;
        isLevelPass = true;
    }

    public boolean hasNextQuestion(boolean isPrevRespCorrect) {
        return (isPrevRespCorrect || isInit);
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
                return getNextDivQuestion(isPrevRespCorrect);
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
                    if (!isPrevRespCorrect) isLevelPass = false;
                    currentType++;
                    newQA = generateAddQn(currentType);
                    break;
                case 2://type 2 has to be correct to generate type 3 + level up
                case 5://type5 has to be correct to generate type 6 + level up
                case 6://type6 has to be correct to generate type 7 + level up
                    if (isPrevRespCorrect && isLevelPass) {
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
        try {
            return (r.nextInt(high - low + 1) + low);
        } catch (IllegalArgumentException e) {
            Log.w(MathEngine.class.getName(), Integer.toString(low) + ":" + Integer.toString(high));
        }
        return 0;
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
                    if (!isPrevRespCorrect) isLevelPass = false;
                    currentType++;
                    newQA = generateSubQn(currentType);
                    break;
                case 1://type 1 has to be correct to generate type 2 + level up
                case 2://type 2 has to be correct to generate type 3 + level up
                case 5://type5 has to be correct to generate type 6 + level up
                case 6://type6 has to be correct to generate type 7 + level up
                    if (isPrevRespCorrect && isLevelPass) {
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
                    if (!isPrevRespCorrect) isLevelPass = false;
                    currentType++;
                    newQA = generateMulQn(currentType);
                    break;
                case 1://type 1 has to be correct to generate type 2 + level up
                case 3: //type 3 has to be correct to generate type 4 + level up
                case 5://type 5 has to be correct to generate type 6 + level up
                case 6://type 6 has to be correct to generate type 7 + level up
                    if (isPrevRespCorrect && isLevelPass) {
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
                op1 = getRandomNumber(2, 5);
                op2 = getRandomNumber(2, 5);
                newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                break;
            case 2://1d x 1d
                op1 = getRandomNumber(2, 9);
                op2 = getRandomNumber(2, 9);
                newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                break;
            case 3://2d x 1d w/o carry
                op1 = getRandomNumber(1, 4);
                op2 = getRandomMul1D(op1);
                op1 = getRandomNumber(1, 9) * 10 + op1;
                newQA = new QuestionAnswer(op1, op2, MainActivity.MUL);
                break;
            case 4://2d x 1d with carry
                op1 = getRandomNumber(2, 9);
                op2 = getRandomMul2D(op1, false);
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
                op2 = getRandomMul2D(op1, false);
                digit = getRandomNumber(2, 9);
                op1 = digit * 10 + op1;
                digit = getRandomMul2D(digit, false);
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
                num = getRandomNumber(2, 9);
                break;
            case 2:
                num = getRandomNumber(2, 4);
                break;
            case 3:
                num = getRandomNumber(2, 3);
                break;
            case 4:
                num = getRandomNumber(2, 2);
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
    int getRandomMul2D(int digit, boolean isNoZeroProduct) {
        int num = 0;
        switch (digit) {
            case 1:
                num = 0;//error condition
                break;
            case 2:
                if (isNoZeroProduct)
                    num = getRandomNumber(6, 9);
                else
                    num = getRandomNumber(5, 9);
                break;
            case 3:
                num = getRandomNumber(4, 9);
                break;
            case 4:
                num = getRandomNumber(3, 9);
                break;
            case 5:
                if (isNoZeroProduct) {
                    num = getRandomNumber(1, 4);
                    num = num * 2 + 1;
                } else
                    num = getRandomNumber(2, 9);
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

    QuestionAnswer getNextDivQuestion(boolean isPrevRespCorrect) {
        QuestionAnswer newQA = null;
        if (isInit) {
            isInit = false;
            currentType = 1;
            newQA = generateDivQn(currentType);
        } else {
            switch (currentType) {

                case 3: //type 3, same level->generate type 4
                case 5: //type 5, same level->generate type 6
                    if (!isPrevRespCorrect) isLevelPass = false;
                    currentType++;
                    newQA = generateDivQn(currentType);
                    break;
                case 1://type 1 has to be correct to generate type 2 + level up
                case 2://type 2 has to be correct to generate type 3 + level up
                case 4://type 4 has to be correct to generate type 5 + level up
                case 6://type 6 has to be correct to generate type 7 + level up
                    if (isPrevRespCorrect && isLevelPass) {
                        currentType++;
                        currentLevel++;
                        newQA = generateDivQn(currentType);
                    }
                    break;
            }
        }
        return newQA;
    }//getNextDivQuestion

    private QuestionAnswer generateDivQn(int type) {
        int op1;
        int op2;
        QuestionAnswer newQA = null;

        switch (type) {
            case 1://2D ÷ 1D - no remainder
                op1 = getRandomNumber(10, 99);
                op2 = getRandomNumber(2, 9);
                while (op1 % op2 != 0) {
                    op2 = getRandomNumber(2, 9);
                }
                newQA = new QuestionAnswer(op1, op2, MainActivity.DIV);
                break;
            case 2://2D ÷ 1D - w remainder
                op1 = getRandomNumber(10, 99);
                op2 = getRandomNumber(2, 9);
                while (op1 % op2 == 0) {
                    op2 = getRandomNumber(2, 9);
                }
                newQA = new QuestionAnswer(op1, op2, MainActivity.DIV);
                break;
            case 3://3D ÷ 1D - no remainder
                newQA = generateType3n4Div(false);
                break;
            case 4://3D ÷ 1D - w remainder
                newQA = generateType3n4Div(true);
                break;
            case 5://3D ÷ 1D - with zero in quotient at end (332/3) w remainder
                newQA = generateType5n7aDiv(true);
                break;
            case 6://3D ÷ 1D - with zero in quotient in middle (322/3) w remainder
                newQA = generateType6n7bDiv(true);
                break;
            case 7://3D ÷ 1D - with zero in quotient and dividend (332/3)
                if (getRandomNumber(1, 2) == 1)
                    newQA = generateType5n7aDiv(false);
                else
                    newQA = generateType6n7bDiv(false);
                break;
        }
        return newQA;
    }//generateDivQn

    private QuestionAnswer generateType3n4Div(boolean isHasReminder) {
        int divisor = 0;
        int quotient = 0;
        int dividend = 0;
        int remainder = 0;
        boolean isDone = false;

        divisor = getRandomNumber(2, 9);
        while (!isDone) {

            switch (divisor) {
                case 2:
                    quotient = getRandomNumber(51, 499);
                    break;
                case 3:
                    quotient = getRandomNumber(34, 333);
                    break;
                case 4:
                    quotient = getRandomNumber(25, 249);
                    break;
                case 5:
                    quotient = getRandomNumber(21, 199);
                    break;
                case 6:
                    quotient = getRandomNumber(17, 166);
                    break;
                case 7:
                    quotient = getRandomNumber(21, 142);
                    break;
                case 8:
                    quotient = getRandomNumber(21, 124);
                    break;
                case 9:
                    quotient = getRandomNumber(21, 111);
                    break;
            }
            if (quotient % 10 != 0 && (quotient / 10) % 10 != 0)
                isDone = true;
        }
        if (isHasReminder)
            remainder = getRandomNumber(1, divisor - 1);
        dividend = quotient * divisor + remainder;
        return new QuestionAnswer(dividend, divisor, MainActivity.DIV);
    }//generateType3n4Div

    private QuestionAnswer generateType5n7aDiv(boolean isNoZeroDividend) {
        int divisor = 0;
        int quotient = 0;
        int dividend = 0;
        int remainder = 0;

        divisor = getRandomNumber(2, 9);

        while (true) {
            switch (divisor) {
                case 2:
                    quotient = getRandomNumber(5, 49);
                    if (isNoZeroDividend) {
                        while (quotient % 5 == 0)
                            quotient = getRandomNumber(6, 49);
                    }
                    break;
                case 3:
                    quotient = getRandomNumber(4, 33);
                    break;
                case 4:
                    quotient = getRandomNumber(3, 24);
                    break;
                case 5:
                    if (isNoZeroDividend) {
                        quotient = getRandomNumber(1, 9);
                        quotient = quotient * 2 + 1;
                    } else
                        quotient = getRandomNumber(2, 19);
                    break;
                case 6:
                    quotient = getRandomNumber(2, 16);
                    break;
                case 7:
                    quotient = getRandomNumber(2, 14);
                    break;
                case 8:
                    quotient = getRandomNumber(2, 12);
                    break;
                case 9:
                    quotient = getRandomNumber(2, 11);
                    break;
            }
            if (quotient % 10 != 0) break;
        }
        quotient = quotient * 10;
        if (isNoZeroDividend)
            remainder = getRandomNumber(1, divisor - 1);
        dividend = quotient * divisor + remainder;
        return new QuestionAnswer(dividend, divisor, MainActivity.DIV);
    }//generateType5Div

    private QuestionAnswer generateType6n7bDiv(boolean isNoZeroDividend) {
        int divisor = 0;
        int quotient = 0;
        int dividend = 0;
        int remainder = 0;

        divisor = getRandomNumber(2, 9);

        switch (divisor) {
            case 2:
                quotient = getRandomNumber(1, 4);
                break;
            case 3:
                quotient = getRandomNumber(1, 3);
                break;
            case 4:
                quotient = getRandomNumber(1, 2);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                quotient = 1;
                break;
        }
        quotient *= 100;
        if (isNoZeroDividend) {
            quotient += getRandomMul2D(divisor, isNoZeroDividend);
            // remainder = getRandomNumber(1,divisor-1);
        } else
            quotient += getRandomMul1D(divisor);

        dividend = quotient * divisor + remainder;
        return new QuestionAnswer(dividend, divisor, MainActivity.DIV);
    }//generateType6Div
}//MathEngine
