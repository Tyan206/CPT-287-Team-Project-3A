package binary_tree_infix_exp_parser;

import java.util.Scanner;
import java.util.Stack;

public class Infix_Parser {

	public class ETree {
		// Data fields
		public String data;
		public ETree left, right;
		// Constructors
		public ETree(String value) { data = value; left = right = null; }
		public ETree(String value, ETree leftChild, ETree rightChild) {
			data = value;
			left = leftChild;
			right = rightChild;
		}
	}

	private String infix;
	private boolean invalidExp = false;
	private ETree expression;

	public Infix_Parser() {}
	public Infix_Parser(String input) {
		infix = fixInfix(input);
		if(!isBalanced(input)) {
			invalidExp = true;
		}else {
			expression = infixToETree(infix);
		}
	}

	/**
	 * Convert the infix to the correct infix for evaluting the expression
	 * @param infix: infix expression with no space
	 * @return: infix expression with corrected space
	 */
	private String fixInfix(String input) {
		String result = "", num = "", oper = "";
		for(int i = 0; i <input.length();i++) {
			if(Character.isDigit(input.charAt(i))) {
				if(oper.length() > 0) {
					result +=  oper + " " ;
				}
				num += input.charAt(i);
				oper = "";
			}else {
				if(num.length() > 0) {

					result += num+" " ;
				}
				if(input.charAt(i) == '(' || input.charAt(i) == ')' || 
						input.charAt(i) == '[' || input.charAt(i) == ']' || 
						input.charAt(i) == '{' || input.charAt(i) == '}'){
					if(oper.length() > 0) {
						oper += " "+input.charAt(i);
					}else {
						oper += input.charAt(i);
					}
					result += oper+" ";
					oper = "";
					num = "";
				}else {
					oper += input.charAt(i);
					num = "";
				}
			}
		}

		result += num + oper;
		return result;
	}

	/** Tests whether parentheses are balanced in an expression.
	@param exp: expression to test
    @return: {true} if parentheses are balanced in the expression; {false} otherwise
	 */
	private boolean isBalanced(String exp) {
		Stack<Character> stk = new Stack<>();
		for (int i = 0; i < exp.length(); i++) {
			if (exp.charAt(i) == '(' || exp.charAt(i) == '[' || exp.charAt(i) == '{') { stk.push(exp.charAt(i)); }
			if (exp.charAt(i) == ')' || exp.charAt(i) == ']' || exp.charAt(i) == '}') {
				if (stk.isEmpty()) { return false; }
				if (exp.charAt(i) == ')' && stk.peek() != '(') { return false; }
				if (exp.charAt(i) == ']' && stk.peek() != '[') { return false; }
				if (exp.charAt(i) == '}' && stk.peek() != '{') { return false; }
				stk.pop();
			}
		}
		return stk.isEmpty();
	}


	@SuppressWarnings("null")
	public ETree postfixToETree(String postfix) {
		ETree expression = null, root = null;
		Stack<ETree> stk = new Stack<ETree>();
		Scanner scanner = new Scanner(postfix);

		while(scanner.hasNext()) {
			root = new ETree(scanner.next());
			if(Character.isDigit(root.data.charAt(0))) {
				stk.push(root);
				//System.out.println(root.data);
			}else {
				//System.out.println(root.data);
				ETree rightNode = stk.pop();
				root.right =  rightNode;
				ETree leftNode = stk.pop();
				root.left =  leftNode;
				stk.push(root);
			}
		}

		while(!stk.empty()) {
			root = stk.pop();
		}
		//System.out.println("root: " + root.left.left.data);

		expression = deepCopy(root);
		scanner.close();
		return expression;
	}

	/** Postorder traverses a binary ETree.
    @param root: the root node of a binary ETree
    @param <T>: Object type
	 */
	public void postorderTraversal(ETree root) {
		if (root != null) {
			postorderTraversal(root.left);
			postorderTraversal(root.right);
			System.out.print(root.data.toString() + ' ');
		}
	}

	/** Creates a deep copy of a binary tree.
    @param root: root node of the original binary tree
    @return: root node of the deep copy
	 */
	public ETree deepCopy(ETree root) {
		// Base case
		if (root == null) { return null; }

		// Recurrence relation
		return new ETree(root.data, deepCopy(root.left), deepCopy(root.right));
	}
	
	
	public ETree infixToETree(String input) {

		// Stack to hold operands stN
		Stack<ETree> operands = new Stack<>();
		// Stack to hold operators stC
		Stack<String> operators = new Stack<>();

		ETree root, rightNode, leftNode;

		// Map to priortising the operators

		// Scanner through input
		Scanner scanner = new Scanner(input);

		while(scanner.hasNext()) {
			String nextVal = scanner.next();
			if(nextVal.equals("(")) {
				operators.add(nextVal);
			}else if(Character.isDigit(nextVal.charAt(0))){
				root = new ETree(nextVal);
				operands.add(root);
			}else if(precedence(nextVal) > 0) {
				while(!operators.empty() && !operators.peek().equals("(") && 
						precedence(operators.peek()) >= precedence(nextVal)	 ) {

					root = new ETree(operators.pop());

					rightNode = operands.pop();

					leftNode = operands.pop();

					root.left = leftNode;
					root.right = rightNode;

					operands.add(root);
				}
				operators.add(nextVal);
			}else if (nextVal.equals(")")) {
				while(!operators.isEmpty() && !operators.peek().equals("(")) {
					root = new ETree(operators.pop());

					rightNode = operands.pop();
					leftNode = operands.pop();

					root.left = leftNode;
					root.right = rightNode;

					operands.add(root);
				}
				operators.pop();
			}
		}
		while(operands.size()>1) {
			root = new ETree(operators.pop());

			rightNode = operands.pop();
			leftNode = operands.pop();

			root.left = leftNode;
			root.right = rightNode;

			operands.add(root);

		}
		scanner.close();
		return operands.peek();
	}

	/** Returns the precedence of an operator.
    @param oper: operator to find its precedence
    @return: precedence of the operator
    @throws IllegalArgumentException: operator is not supported.
	 */
	private int precedence(String oper) {
		if(oper.equals(")")) { return 0;}
		if (oper.equals("||")){ return 1; }
		if (oper.equals("&&")){ return 2; }
		if (oper.equals("==") || (oper.equals("!="))){ return 3; }
		if (oper.equals(">") || (oper.equals(">=")) || (oper.equals("<")) || (oper.equals("<="))){ return 4; }
		if (oper.equals("+") || (oper.equals("-"))){ return 5; }
		if (oper.equals("*") || (oper.equals("/")) || (oper.equals("%"))){ return 6; }
		if (oper.equals("^")){ return 7; }
		throw new IllegalArgumentException("Operator not supported");
	}

	/** Converts an infix expression to postfix expression.
    @param infixExp: infix expression to convert
    @return: result postfix expression
	 */
	public String infixToPostfix(String infixExp) {
		Stack<String> stk = new Stack<>();
		StringBuilder postfix = new StringBuilder();
		Scanner scanner = new Scanner(infixExp);
		while (scanner.hasNext()) {
			String token = scanner.next();
			if (Character.isDigit(token.charAt(0))) { postfix.append(token).append(' '); }
			else if (token.equals("(")) { stk.push(token); }
			else if (token.equals(")")) {
				while (!stk.peek().equals("(")) { postfix.append(stk.pop()).append(' '); }
				stk.pop();
			} else {
				while (!stk.isEmpty() && !stk.peek().equals("(") && precedence(token) <= precedence(stk.peek())) {
					postfix.append(stk.pop()).append(' ');
				}
				stk.push(token);
			}
		}
		while (!stk.isEmpty()) { postfix.append(stk.pop()).append(' '); }
		scanner.close();
		return postfix.toString();
	}

	/** Evaluates an expression using a expression ETree. 
	 * "^", "*", "/", "%", "+", "-", ">", ">=", "<", "<=", "==", "!=", "&&", "||" 14 totals
	    @param root: ETree to evaluate
	    @return: evaluation result
	 */
	public int evaluate(ETree root){
		if(root == null || invalidExp) { return 0;}

		if(root.left == null && root.right == null) { return Integer.valueOf(root.data);}

		//Evaluate left subETree
		int evalLeft = evaluate(root.left);

		//Evaluate right subETree
		int evalRight = evaluate(root.right);

		//Apply operators
		if(root.data.equals("+")) { return evalLeft + evalRight;}
		if(root.data.equals("-")) { return evalLeft - evalRight;}
		if(root.data.equals("*")) { return evalLeft * evalRight;}

		if(root.data.equals("%")) { return evalLeft % evalRight;}
		if(root.data.equals("^")) { return (int) Math.pow(evalLeft, evalRight);}
		if(root.data.equals(">")) {
			if(evalLeft > evalRight) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("<")) {
			if(evalLeft < evalRight) {
				return 1;
			}else { return 0;}
		}
		if(root.data.equals(">=")) {
			if(evalLeft >= evalRight) {
				return 1;
			}else { return 0;}
		}
		if(root.data.equals("<=")) {
			if(evalLeft <= evalRight) {
				return 1;
			}else {return 0;}
		}
		if(root.data.equals("==")) {
			if(evalLeft == evalRight) {
				return 1;
			}else {return 0;}
		}
		if(root.data.equals("!=")) {
			if(evalLeft != evalRight) {
				return 1;
			}else {return 0;}
		}
		if(root.data.equals("&&")) {
			if(evalLeft == 1 && evalRight == 1) {
				return 1;
			}else {return 0;}
		}
		if(root.data.equals("||")) {
			if(evalLeft == 1 || evalRight == 1) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("/")) {
			if(evalRight == 0 ) {
				invalidExp = true;
				throw new ArithmeticException("Dividing by zero");
			}else {
				return evalLeft / evalRight;
			}
		}
		return 0;
	}
	
	@Override
	public String toString() {
		
		if(!invalidExp) {
			return evaluate(expression)+"";
		}else {
			System.out.println("The infix expression is invalid");
			return "";
		}
		
	}

}

