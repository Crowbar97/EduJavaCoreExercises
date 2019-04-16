package edu.JavaCore.Expression;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Launcher {
    public static void main(String[] args) {
        Expression exp = new Expression();
        if (args.length > 0)
            try {
                System.out.println(exp.evaluate(args[0]));
            } catch (Expression.ExpressionValidationException e) {
                System.out.println(e);
            }
        else {
            System.out.println("Welcome to calculator!");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                try {
                    System.out.println(exp.evaluate(scanner.nextLine()));
                } catch (Expression.ExpressionValidationException e) {
                    System.out.println(e);
                } catch (NoSuchElementException e) {
                    System.out.println("See you later!");
                    break;
                }
            }
        }
    }
}
