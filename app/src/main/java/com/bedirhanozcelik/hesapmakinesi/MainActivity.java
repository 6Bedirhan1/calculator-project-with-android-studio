package com.bedirhanozcelik.hesapmakinesi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigDecimal;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView textResult;
    private String currentInput = "";
    private String expression = "";
    private boolean isNewInput = true;
    private boolean lastResultUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = findViewById(R.id.textResult);

        setNumberButtonClickListener(R.id.btn0, "0");
        setNumberButtonClickListener(R.id.btn1, "1");
        setNumberButtonClickListener(R.id.btn2, "2");
        setNumberButtonClickListener(R.id.btn3, "3");
        setNumberButtonClickListener(R.id.btn4, "4");
        setNumberButtonClickListener(R.id.btn5, "5");
        setNumberButtonClickListener(R.id.btn6, "6");
        setNumberButtonClickListener(R.id.btn7, "7");
        setNumberButtonClickListener(R.id.btn8, "8");
        setNumberButtonClickListener(R.id.btn9, "9");

        setOperatorButtonClickListener(R.id.btnPlus, "+");
        setOperatorButtonClickListener(R.id.btnMinus, "-");
        setOperatorButtonClickListener(R.id.btnMultiply, "×");
        setOperatorButtonClickListener(R.id.btnDivide, "÷");

        setParenthesisButtonClickListener(R.id.btnOpenParen, "(");
        setParenthesisButtonClickListener(R.id.btnCloseParen, ")");

        Button btnEquals = findViewById(R.id.btnEquals);
        btnEquals.setOnClickListener(v -> calculateResult());

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> clearCalculator());

        Button btnBackspace = findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(v -> backspace());

        Button btnDot = findViewById(R.id.btnDot);
        btnDot.setOnClickListener(v -> {
            if (!currentInput.contains(".")) {
                currentInput += ".";
                textResult.setText(expression + currentInput);
            }
            lastResultUsed = false;
        });
    }

    private void setNumberButtonClickListener(int buttonId, String number) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            if (isNewInput) {
                currentInput = number;
                isNewInput = false;
            } else {
                currentInput += number;
            }
            textResult.setText(expression + currentInput);
            lastResultUsed = false;
        });
    }

    private void setOperatorButtonClickListener(int buttonId, String op) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            // Eğer son hesaplama kullanıldıysa, yeni bir operatör eklenebilir
            if (lastResultUsed) {
                expression = textResult.getText().toString() + op;
                lastResultUsed = false;
            }
            // Eğer mevcut input doluysa veya en son karakter bir kapanan parantezse, operatör eklenebilir
            else if (!currentInput.isEmpty() || expression.endsWith(")")) {
                expression += currentInput + op;
            }
            // Eğer expression boş değilse ve son karakter bir operatörse, eski operatörün yerine yenisini ekle
            else if (!expression.isEmpty() && "+-×÷".contains(expression.substring(expression.length() - 1))) {
                expression = expression.substring(0, expression.length() - 1) + op;
            }
            // Eğer operatör sildikten sonra tekrar operatör eklenmek isteniyorsa izin ver
            else if (!expression.isEmpty()) {
                expression += op;
            }

            // Metni güncelle
            textResult.setText(expression);

            currentInput = "";  // Yeni bir input girişi bekleniyor
            isNewInput = true;
        });
    }

    private void setParenthesisButtonClickListener(int buttonId, String paren) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            if (paren.equals("(")) {
                if (expression.isEmpty() || expression.endsWith("(") || "+-×÷".contains(expression.substring(expression.length() - 1))) {
                    expression += paren;
                }
            } else if (paren.equals(")")) {
                if (!currentInput.isEmpty()) {
                    expression += currentInput;
                    currentInput = "";
                }

                // Parantez sayılarını manuel olarak say
                int openCount = 0, closeCount = 0;
                for (char c : expression.toCharArray()) {
                    if (c == '(') openCount++;
                    if (c == ')') closeCount++;
                }

                if (openCount > closeCount) {
                    expression += paren;
                }
            }
            textResult.setText(expression);
        });
    }

    private void backspace() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else if (!expression.isEmpty()) {
            char lastChar = expression.charAt(expression.length() - 1);
            expression = expression.substring(0, expression.length() - 1);

            // Eğer silinen karakter bir operatörse, tekrar operatör eklenebilmesini sağla
            if ("+-×÷".contains(String.valueOf(lastChar))) {
                isNewInput = false;  // Tekrar operatör ekleyebilmek için false yap
            }
        }

        textResult.setText(expression + currentInput);

        // Eğer ekranda tamamen boş kalırsa "0" göster
        if (expression.isEmpty() && currentInput.isEmpty()) {
            textResult.setText("0");
        }
    }


    private void calculateResult() {
        if (!currentInput.isEmpty()) {
            expression += currentInput;
        }

        try {
            BigDecimal result = evaluateExpression(expression);
            textResult.setText(result.stripTrailingZeros().toPlainString());
            expression = result.stripTrailingZeros().toPlainString();
            currentInput = "";
            lastResultUsed = true;
        } catch (Exception e) {
            textResult.setText("Hata");
            expression = "";
            currentInput = "";
        }
    }

    private BigDecimal evaluateExpression(String expr) {
        // "×" ve "÷" sembollerini "*" ve "/" ile değiştir
        expr = expr.replace("×", "*").replace("÷", "/");

        Expression expression = new ExpressionBuilder(expr).build();
        double result = expression.evaluate();

        return BigDecimal.valueOf(result);
    }

    private void clearCalculator() {
        currentInput = "";
        expression = "";
        isNewInput = true;
        lastResultUsed = false;
        textResult.setText("0");
    }
}