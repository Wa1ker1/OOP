package ru.nsu.filippova;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовый класс для всех арифметических выражений.
 */
public abstract class Expression {

    /**
     * Формирует строковое представление текущего выражения.
     *
     * @return выражение в виде строки
     */
    public abstract String print();

    /**
     * Вычисляет производную выражения по указанной переменной.
     *
     * @param variable имя переменной дифференцирования
     * @return новое выражение, представляющее производную
     */
    public abstract Expression derivative(String variable);

    /**
     * Вычисляет значение выражения при заданных значениях переменных.
     *
     * @param variables отображение имен переменных в их значения
     * @return целочисленный результат вычисления
     */
    public abstract int eval(Map<String, Integer> variables);

    /**
     * Разбирает строку, используя {@link Parser}, и возвращает построенное выражение.
     *
     * @param expression строковое представление выражения
     * @return построенное выражение
     */
    public static Expression parse(String expression) {
        return Parser.parseExpression(expression);
    }

    /**
     * Выполняет вычисление выражения, разбирая список присваиваний вида {@code x = 10; y = 2}.
     *
     * @param assignment строковый список присваиваний
     * @return значение выражения
     */
    public int eval(String assignment) {
        String[] parts = assignment.split(";");
        HashMap<String, Integer> map = new HashMap<>();
        for (String p : parts) {
            if (p.trim().isEmpty()) {
                continue;
            }
            String[] kv = p.split("=", 2);
            if (kv.length != 2) {
                throw new IllegalArgumentException("Некорректное присваивание: " + p);
            }
            String name = kv[0].trim();
            String value = kv[1].trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Некорректное имя переменной в присваивании: " + p);
            }
            map.put(name, Integer.parseInt(value));
        }
        return eval(map);
    }

    /**
     * Упрощает выражение, выполняя возможные вычисления на уровне констант.
     *
     * @return упрощенное выражение
     */
    public abstract Expression simplify();
}
