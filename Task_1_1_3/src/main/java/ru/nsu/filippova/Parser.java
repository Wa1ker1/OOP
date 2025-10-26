package ru.nsu.filippova;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Позволяет считывать и разбирать арифметические выражения.
 */
public class Parser {

    private final BufferedReader reader;

    /**
     * Создает парсер, читающий выражение из {@link System#in}.
     */
    public Parser() {
        this(System.in);
    }

    /**
     * Создает парсер, читающий выражения из указанного потока.
     *
     * @param inputStream поток, предоставляющий выражения
     */
    public Parser(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    /**
     * Создает парсер, читающий выражения из переданного символьного источника.
     *
     * @param reader источник символов с выражениями
     */
    public Parser(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /**
     * Считывает одно выражение из привязанного источника и возвращает его AST-представление.
     *
     * @return разобранное выражение
     * @throws IOException              если чтение из источника завершилось с ошибкой
     * @throws IllegalArgumentException если поток ввода закончился до передачи выражения
     */
    public Expression readExpression() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("Ввод завершился до получения выражения");
        }
        return parse(line);
    }

    /**
     * Разбирает переданную строку как арифметическое выражение.
     *
     * @param expression строковое представление выражения
     * @return AST-представление выражения
     * @throws IllegalArgumentException если строка не соответствует ожидаемому синтаксису
     */
    public Expression parse(String expression) {
        return parseExpression(expression);
    }

    /**
     * Разбирает выражение, используя синтаксис, поддерживаемый библиотекой.
     *
     * @param expression строковое выражение
     * @return AST-представление выражения
     * @throws IllegalArgumentException если строка не соответствует ожидаемому синтаксису
     */
    public static Expression parseExpression(String expression) {
        String trimmed = expression.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Невозможно распарсить выражение: " + expression);
        }
        if (isFullyWrapped(trimmed)) {
            return parseExpression(trimmed.substring(1, trimmed.length() - 1));
        }
        if (trimmed.matches("-?\\d+")) {
            return new Number(Integer.parseInt(trimmed));
        }
        if (trimmed.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            return new Variable(trimmed);
        }

        int depth = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c == '(') {
                depth++;
                continue;
            }
            if (c == ')') {
                depth--;
                if (depth < 0) {
                    throw new IllegalArgumentException("Невозможно распарсить выражение: " + expression);
                }
                continue;
            }
            if (depth == 0 && isOperator(c)) {
                if (c == '-' && isUnaryMinus(trimmed, i)) {
                    continue;
                }
                String left = trimmed.substring(0, i).trim();
                String right = trimmed.substring(i + 1).trim();
                Expression l = parseExpression(left);
                Expression r = parseExpression(right);
                switch (c) {
                    case '+':
                        return new Add(l, r);
                    case '-':
                        return new Sub(l, r);
                    case '*':
                        return new Mul(l, r);
                    case '/':
                        return new Div(l, r);
                    default:
                        break;
                }
            }
        }
        if (depth != 0) {
            throw new IllegalArgumentException("Невозможно распарсить выражение: " + expression);
        }
        throw new IllegalArgumentException("Невозможно распарсить выражение: " + expression);
    }

    private static boolean isFullyWrapped(String expression) {
        if (expression.length() < 2 || expression.charAt(0) != '(' || expression.charAt(expression.length() - 1) != ')') {
            return false;
        }
        int depth = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            }
            if (depth == 0 && i < expression.length() - 1) {
                return false;
            }
        }
        return depth == 0;
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static boolean isUnaryMinus(String expression, int position) {
        if (expression.charAt(position) != '-') {
            return false;
        }
        if (position == 0) {
            return true;
        }
        char prev = expression.charAt(position - 1);
        return prev == '(' || isOperator(prev);
    }
}
