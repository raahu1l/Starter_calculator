package com.example.mycalc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    private TextView resultView;
    private final StringBuilder input = new StringBuilder();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = findViewById(R.id.textResult);

        // Numbers
        int[] numberIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9
        };

        View.OnClickListener numClick = v -> {
            Button b = (Button) v;
            input.append(b.getText());
            resultView.setText(input.toString());
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(numClick);
        }

        // Operators
        findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            if (appendOperator('+')) {
                resultView.setText(input.toString());
            }
        });
        findViewById(R.id.buttonSub).setOnClickListener(v -> {
            if (appendOperator('-')) {
                resultView.setText(input.toString());
            }
        });
        findViewById(R.id.buttonMul).setOnClickListener(v -> {
            if (appendOperator('*')) {
                resultView.setText(input.toString());
            }
        });
        findViewById(R.id.buttonDiv).setOnClickListener(v -> {
            if (appendOperator('/')) {
                resultView.setText(input.toString());
            }
        });

        // Percentage button
        findViewById(R.id.buttonper).setOnClickListener(v -> {
            if (input.length() > 0 &&
                    Character.isDigit(input.charAt(input.length() - 1)) &&
                    input.indexOf("%") == -1) {
                input.append("%");
                resultView.setText(input.toString());
            }
        });

        // Decimal point
        findViewById(R.id.buttonDot).setOnClickListener(v -> {
            int lastOperatorIdx = Math.max(
                    Math.max(input.lastIndexOf("+"), input.lastIndexOf("-")),
                    Math.max(input.lastIndexOf("*"), input.lastIndexOf("/"))
            );
            lastOperatorIdx = Math.max(lastOperatorIdx, input.lastIndexOf("%"));
            String lastNum = input.substring(lastOperatorIdx + 1);
            if (!lastNum.contains(".")) {
                input.append(".");
                resultView.setText(input.toString());
            }
        });

        // Equals
        findViewById(R.id.buttonEqual).setOnClickListener(v -> {
            String expr = input.toString();
            try {
                double result = evalExpression(expr);
                if (result == (long) result) {
                    resultView.setText(String.valueOf((long) result));
                    input.setLength(0);
                    input.append((long) result);
                } else {
                    resultView.setText(String.valueOf(result));
                    input.setLength(0);
                    input.append(result);
                }
            } catch (Exception e) {
                resultView.setText(getString(R.string.error));
                input.setLength(0);
            }
        });

        // Clear All
        findViewById(R.id.buttonAC).setOnClickListener(v -> {
            input.setLength(0);
            resultView.setText(getString(R.string.default_zero));
        });

        // Backspace
        findViewById(R.id.buttonC).setOnClickListener(v -> {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
                resultView.setText(
                        input.length() > 0 ? input.toString() : getString(R.string.default_zero)
                );
            }
        });
    }

    private boolean appendOperator(char operator) {
        if (input.length() == 0) return false;
        char lastChar = input.charAt(input.length() - 1);
        if ("+-*/%".indexOf(lastChar) >= 0) {
            input.setCharAt(input.length() - 1, operator);
        } else {
            input.append(operator);
        }
        return true;
    }

    private double evalExpression(String expr) throws Exception {
        // Percentage
        if (expr.contains("%")) {
            int idx = expr.indexOf('%');
            double percentNum = Double.parseDouble(expr.substring(0, idx));
            double ofNum = Double.parseDouble(expr.substring(idx + 1));
            return percentNum / 100.0 * ofNum;
        }

        char[] ops = {'+', '-', '*', '/'};
        int opPos = -1;
        char op = ' ';
        for (char c : ops) {
            int i = expr.indexOf(c, 1);
            if (i != -1) {
                opPos = i;
                op = c;
                break;
            }
        }
        if (opPos == -1) {
            return Double.parseDouble(expr);
        }

        double left = Double.parseDouble(expr.substring(0, opPos));
        double right = Double.parseDouble(expr.substring(opPos + 1));
        switch (op) {
            case '+': return left + right;
            case '-': return left - right;
            case '*': return left * right;
            case '/': return right == 0 ? 0 : left / right;
        }
        throw new Exception("Invalid operator");
    }
}
