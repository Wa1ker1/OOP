package ru.nsu.filippova;

import java.util.Map;

public abstract class Expression {
    public abstract String print();
    public abstract Expression derivative(String variable);
    public abstract int eval(Map<String, Integer> variables);


    public static Expression parse(String s) {
        s = s.trim();
        if (s.matches("-?\\d+")) {
            return new Number(Integer.parseInt(s));
        }
        if (s.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            return new Variable(s);
        }
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1);
            int depth = 0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '(') depth++;
                else if (c == ')') depth--;
                else if ((c == '+' || c == '-' || c == '*' || c == '/') && depth == 0) {
                    String left = s.substring(0, i).trim();
                    String right = s.substring(i + 1).trim();
                    Expression l = parse(left);
                    Expression r = parse(right);
                    switch (c) {
                        case '+': return new Add(l, r);
                        case '-': return new Sub(l, r);
                        case '*': return new Mul(l, r);
                        case '/': return new Div(l, r);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Невозможно распарсить выражение: " + s);
    }

    public int eval(String assignment) {
        String[] parts = assignment.split(";");
        java.util.HashMap<String, Integer> map = new java.util.HashMap<>();
        for (String p : parts) {
            if (p.trim().isEmpty()) continue;
            String[] kv = p.split("=");
            map.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
        }
        return eval(map);
    }

    public abstract Expression simplify();
}
