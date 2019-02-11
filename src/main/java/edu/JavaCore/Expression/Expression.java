package edu.JavaCore.Expression;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * expression evaluation class
 */
public class Expression {
    /**
     * special exception for expression validation
     */
    public class ExpressionValidationException extends Exception {
        private String exp;
        private int errorPos;
        private String errorMsg;

        private ExpressionValidationException(String exp, int errorPos, String errorMsg) {
            this.exp = exp;
            this.errorPos = errorPos;
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return "Expression validation error:\n" + exp + "\n" + " ".repeat(errorPos) + "^\n" + errorMsg + ".";
        }
    }
    /**
     * just a tuple
     * @param <First> type of first element
     * @param <Second> type of second element
     */
    private class Tuple <First, Second> {
        private First first;
        private Second second;

        private Tuple(First first, Second second) {
            this.first = first;
            this.second = second;
        }
    }

    /**
     * @param str string for matching
     * @param regex pattern for matching
     * @return left match position of pattern and string
     */
    private int getMatchPos(String str, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if (!matcher.find())
            return -1;
        return matcher.start();
    }
    /**
     * checks bracket balance
     * @param exp the expression to be checked
     * @throws ExpressionValidationException if brackets are not balanced
     */
    private void checkBalance(String exp) throws ExpressionValidationException {
        Deque<Integer> pos = new ArrayDeque<>();
        for (int i = 0; i < exp.length(); i++) {
            switch (exp.charAt(i)) {
                case '(':
                    pos.add(i);
                    break;
                case ')':
                    if (pos.isEmpty())
                        throw new ExpressionValidationException(exp, i, "'(' expected");
                    else
                        pos.pollLast();
                    break;
                default:
                    break;
            }
        }
        if (!pos.isEmpty())
            throw new ExpressionValidationException(exp, pos.peekLast(), "')' expected");
    }
    /**
     * checks for characters are in set of 0-9, (, ), *, /, + or -
     * @param exp the expression to be checked
     * @throws ExpressionValidationException if expression has illegal characters
     */
    private void checkIllegal(String exp) throws ExpressionValidationException {
        int matchPos = getMatchPos(exp, "[^\\d()*/+-]");
        if (matchPos != -1)
            throw new ExpressionValidationException(exp, matchPos, "Illegal character was found");
    }
    /**
     * checks next extra symbol situations:
     * <ul>
     *     <li>pair of brackets with no content</li>
     *     <li>pair of operators</li>
     *     <li>pair of bracket and operator</li>
     *     <li>pair of operator and bracket</li>
     *     <li>operator in the beginning</li>
     *     <li>operator in the end</li>
     * </ul>
     * @param exp the expression to be checked
     * @throws ExpressionValidationException if expression has extra symbols
     */
    private void checkExtra(String exp) throws ExpressionValidationException {
        int matchPos = getMatchPos(exp, "\\(\\)|[*/+-]{2}|\\([*/]");
        if (matchPos != -1)
            throw new ExpressionValidationException(exp, matchPos + 1, "Extra symbol was found");

        matchPos = getMatchPos(exp, "[*/+-]\\)|^[*/]|[*/+-]$");
        if (matchPos != -1)
            throw new ExpressionValidationException(exp, matchPos, "Extra symbol was found");
    }
    /**
     * checks next missed symbols situations:
     * <ul>
     *     <li>pair of adjoining brackets</li>
     *     <li>pair of adjoining bracket and digit</li>
     *     <li>pair of adjoining digit and bracket</li>
     * </ul>
     * @param exp the expression to be checked
     * @throws ExpressionValidationException if expression has missed symbols
     */
    private void checkMissed(String exp) throws ExpressionValidationException {
        int matchPos = getMatchPos(exp, "\\)\\(|\\)\\d|\\d\\(");
        if (matchPos != -1)
            throw new ExpressionValidationException(exp, matchPos + 1, "Operator is missing");
    }
    /**
     * validates given expression
     * @param exp the expression to be validated
     * @throws ExpressionValidationException if expression is not valid
     */
    private void validate(String exp) throws ExpressionValidationException {
        checkBalance(exp);
        checkIllegal(exp);
        checkExtra(exp);
        checkMissed(exp);
    }

    /**
     * expression node
     */
    private class Node {
        /**
         * left operand
         */
        private Node left;
        /**
         * right operand
         */
        private Node right;
        /**
         * operator
         */
        private char op;
        /**
         * the result of operator
         */
        private Double value;

        /**
         * constructs new not evaluated node by given parameters
         * @param op operator
         * @param left left operand
         * @param right right operand
         */
        private Node(char op, Node left, Node right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }
        /**
         * constructs new evaluated node by given result
         * @param value the result of the operator application
         */
        private Node(Double value) {
            this.value = value;
        }
        /**
         * constructs empty node for further filling
         */
        private Node() {}
    }

    /**
     * special class for node description
     */
    private class NodeTuple {
        /**
         * node
         */
        private Node node;
        /**
         * string representation of left operand for further parsing
         */
        private String left;
        /**
         * string representation of right operand for further parsing
         */
        private String second;

        /**
         * constructs new node description
         * @param node new node
         * @param left left operand description
         * @param right right operand description
         */
        private NodeTuple(Node node, String left, String right) {
            this.node = node;
            this.left = left;
            this.second = right;
        }
    }

    /**
     * trims extra side brackets<br/>
     * works only with correct expressions
     * @param exp expression to be trimmed
     * @return trimmed expression
     */
    private String trim(String exp) {
        Deque<Integer> pos = new ArrayDeque<>();
        for (int i = 0; i < exp.length(); i++) {
            switch (exp.charAt(i)) {
                case '(':
                    pos.add(i);
                    break;
                case ')':
                    if(pos.pollLast() == 0 && i == exp.length() - 1)
                        return trim(exp.substring(1, exp.length() - 1));
                    break;
                default:
                    break;
            }
        }
        return exp;
    }
    /**
     * finds the last operation of the given expression<br/>
     * works only with correct expression
     * @param exp expression to be analyzed
     * @return found operand and it's position
     */
    private Tuple<Character, Integer> getNextOp(String exp) {
        int count = 0;
        for (int i = exp.length() - 1; i >= 0; i--) {
            switch (exp.charAt(i)) {
                case '(':
                    count++;
                    break;
                case ')':
                    count--;
                    break;
                default:
                    if (count == 0)
                        if (exp.charAt(i) == '+' || exp.charAt(i) == '-')
                            return new Tuple<>(exp.charAt(i), i);
                    break;
            }
        }
        for (int i = exp.length() - 1; i >= 0; i--) {
            switch (exp.charAt(i)) {
                case '(':
                    count++;
                    break;
                case ')':
                    count--;
                    break;
                default:
                    if (count == 0)
                        if (exp.charAt(i) == '*' || exp.charAt(i) == '/')
                            return new Tuple<>(exp.charAt(i), i);
                    break;
            }
        }
        return new Tuple<>(null, -1);
    }
    /**
     * builds and returns node structure by given expression
     * @param exp expression to be analyzed
     * @return tuple of node and two representations of it's left and right operands
     * @see NodeTuple
     */
    private NodeTuple getNode(String exp) {
        exp = trim(exp);

        Tuple<Character, Integer> op = getNextOp(exp);

        if (op.second != -1)
            return new NodeTuple(new Node(op.first, new Node(), new Node()),
                    exp.substring(0, op.second), exp.substring(op.second + 1));

        return new NodeTuple(new Node(Double.parseDouble(exp)), null, null);
    }
    /**
     * parses given expression to expression tree
     * @param node root of the expression tree
     * @param exp expression to be parsed
     */
    private void parse(Node node, String exp) {
        NodeTuple tuple = getNode(exp);
        node.left = tuple.node.left;
        node.right = tuple.node.right;
        node.op = tuple.node.op;
        node.value = tuple.node.value;
        if (node.left != null) {
            parse(node.left, tuple.left);
            parse(node.right, tuple.second);
        }
    }

    /**
     * evaluates binary operation value
     * @param op operation
     * @param first left operand
     * @param second right operand
     * @return operation value
     */
    private Double evaluate(char op, Double first, Double second) {
        switch (op) {
            case '+':
                return first + second;
            case '-':
                return first - second;
            case '*':
                return first * second;
            case '/':
                return first / second;
            default:
                System.out.println("Operator error!");
                return 0.0;
        }
    }
    /**
     * puts expression result in the root of the expression tree
     * @param p root of the expression tree
     */
    private void evaluate(Node p) {
        if (p != null) {
            evaluate(p.left);
            evaluate(p.right);
            if (p.left != null && p.left.value != null && p.right != null && p.right.value != null)
                p.value = evaluate(p.op, p.left.value, p.right.value);
        }
    }
    /**
     * evaluates given expression
     * @param exp expression to be evaluated
     * @return value of the expression
     */
    public double evaluate(String exp) throws ExpressionValidationException {
        // spaces removing (+ digit merging)
        exp = exp.replaceAll("\\s", "");

        // expression validation
        validate(exp);

        // completing leading minuses with left zero
        exp = exp.replaceAll("^-", "0-");
        exp = exp.replaceAll("\\(-", "(0-");

        // expression tree creation and evaluation
        Node node = new Node();
        parse(node, exp);
        evaluate(node);

        // expression result returning
        return node.value;
    }
}

