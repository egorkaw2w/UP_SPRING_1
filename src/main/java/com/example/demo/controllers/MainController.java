package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/calculator")
    public String showCalculator(Model model) {
        model.addAttribute("expression", "");
        return "calculator";
    }

    @PostMapping("/calculator")
    public String calculate(
            @RequestParam("expression") String expression,
            @RequestParam("action") String action,
            Model model) {

        String updatedExpr = expression == null ? "" : expression;
        String result = "";

        if ("clear".equals(action)) {
            updatedExpr = "";
        } else if ("backspace".equals(action)) {
            if (!updatedExpr.isEmpty()) {
                updatedExpr = updatedExpr.substring(0, updatedExpr.length() - 1);
            }
        } else if ("equals".equals(action)) {
            try {
                double eval = evaluateExpression(updatedExpr);
                updatedExpr = formatResult(eval);
            } catch (ArithmeticException e) {
                updatedExpr = "Деление на 0!";
            } catch (Exception e) {
                updatedExpr = "Ошибка";
            }
        } else {
            updatedExpr += action;
        }

        model.addAttribute("expression", updatedExpr);
        model.addAttribute("result", result);
        return "calculator";
    }

    private double evaluateExpression(String expr) {
        double result = 0.0;
        char operator = '+';
        StringBuilder number = new StringBuilder();

        for (int i = 0; i <= expr.length(); i++) {
            char c = i < expr.length() ? expr.charAt(i) : '\0';

            if ((c >= '0' && c <= '9') || c == '.') {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    double num = Double.parseDouble(number.toString());
                    number.setLength(0);
                    switch (operator) {
                        case '+': result += num; break;
                        case '-': result -= num; break;
                        case '*': result *= num; break;
                        case '/':
                            if (num == 0) throw new ArithmeticException("Деление на ноль");
                            result /= num;
                            break;
                    }
                }
                operator = c;
            }
        }
        return result;
    }

    private String formatResult(double val) {
        if (val == (long) val) {
            return String.valueOf((long) val);
        }
        return String.valueOf(val);
    }

    @GetMapping("/converter")
    public String converter(
            @RequestParam(value = "amount1", required = false) String amount1,
            @RequestParam(value = "currency1", required = false) String currency1,
            @RequestParam(value = "currency2", required = false) String currency2,
            Model model
    ) {
        if (currency1 == null) currency1 = "rub";
        if (currency2 == null) currency2 = "usd";
        model.addAttribute("amount1", amount1);
        model.addAttribute("currency1", currency1);
        model.addAttribute("currency2", currency2);
        model.addAttribute("amount2", "");
        return "converter";
    }

    @GetMapping("/convert")
    public String convert(
            @RequestParam(value = "amount1", required = false) String amount1,
            @RequestParam(value = "currency1") String currency1,
            @RequestParam(value = "currency2") String currency2,
            Model model
    ) {
        double value;
        try {
            value = Double.parseDouble(amount1);
        } catch (Exception e) {
            value = 0.0;
        }
        double result = convertCurrency(value, currency1, currency2);
        model.addAttribute("amount1", amount1);
        model.addAttribute("currency1", currency1);
        model.addAttribute("currency2", currency2);
        model.addAttribute("amount2", result);
        return "converter";
    }

    private double convertCurrency(double value, String from, String to) {
        if (from.equals(to)) return value;
        if (from.equals("rub") && to.equals("usd")) return value / 81.56;
        if (from.equals("usd") && to.equals("rub")) return value * 81.56;
        if (from.equals("rub") && to.equals("CNY")) return value / 11.45;
        if (from.equals("CNY") && to.equals("rub")) return value * 11.45;
        if (from.equals("usd") && to.equals("CNY")) return value * 7.13;
        if (from.equals("CNY") && to.equals("usd")) return value / 7.13;
        return value;
    }
}
