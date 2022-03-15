package binary_tree_infix_exp_parser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;




public class Program {


	@SuppressWarnings("null")
	public static Tree postfixToTree(String postfix) {
		Tree expression = null, root = null;
		Stack<Tree> stk = new Stack<Tree>();
		Scanner scanner = new Scanner(postfix);

		while(scanner.hasNext()) {
			root = new Tree(scanner.next());
			if(Character.isDigit(root.data.charAt(0))) {
				stk.push(root);
				//System.out.println(root.data);
			}else {
				//System.out.println(root.data);
				Tree rightNode = stk.pop();
				root.right =  rightNode;
				Tree leftNode = stk.pop();
				root.left =  leftNode;
				stk.push(root);
			}
		}
		//System.out.println("root: " + root.data);
	//	System.out.println("root left: " + root.left.data);
	//	System.out.println("root right: " + root.right.data);
	//	System.out.println("root: " + root.left.left.data);

		while(!stk.empty()) {
			root = stk.pop();
		}
		//System.out.println("root: " + root.left.left.data);

		expression = root;
		return expression;
	}

	/** Postorder traverses a binary tree.
    @param root: the root node of a binary tree
    @param <T>: Object type
	 */
	public static void postorderTraversal(Tree root) {
		if (root != null) {
			postorderTraversal(root.left);
			postorderTraversal(root.right);
			System.out.print(root.data.toString() + ' ');
		}
	}

	public static Tree infixToTree(String input) {

		// Stack to hold operands stN
		Stack<Tree> operands = new Stack<>();
		// Stack to hold operators stC
		Stack<String> operators = new Stack<>();

		Tree root, rightNode, leftNode;

		// Map to priortising the operators

		// Scanner through input
		Scanner scanner = new Scanner(input);

		while(scanner.hasNext()) {
			String nextVal = scanner.next();
			if(nextVal.equals("(")) {
			//	System.out.println("First if: "+nextVal);
				operators.add(nextVal);
			}else if(Character.isDigit(nextVal.charAt(0))){
			//	System.out.println("Second if: "+nextVal);
				root = new Tree(nextVal);
				operands.add(root);
			}else if(precedence(nextVal) > 0) {
			//	System.out.println("Third if: "+nextVal);
				// If an operator with lower or same associativity appears
				while(!operators.empty() && !operators.peek().equals("(") && 
						precedence(operators.peek()) >= precedence(nextVal)	 ) {
					
					/*
					 
					 ((!nextVal.equals("^") && precedence(operators.peek()) >= precedence(nextVal)) 
								|| (nextVal.equals("^") && precedence(operators.peek()) > precedence(nextVal))) 
					 
					 
				//	 */
				//	System.out.println("Third if and loop: "+nextVal);
					root = new Tree(operators.pop());

					rightNode = operands.pop();
					
					leftNode = operands.pop();
					
					root.left = leftNode;
					root.right = rightNode;
					
					operands.add(root);
				}
				operators.add(nextVal);
			}else if (nextVal.equals(")")) {
				//System.out.println("Fourth if: "+nextVal);
			//	System.out.println(operators.isEmpty());
				while(!operators.isEmpty() && !operators.peek().equals("(")) {
				//	System.out.println("Fourth if and loop: "+nextVal);
					root = new Tree(operators.pop());
					
					rightNode = operands.pop();
					leftNode = operands.pop();
					
					root.left = leftNode;
					root.right = rightNode;
					
					operands.add(root);
				}
				operators.pop();
			}
		}
		root = new Tree("NULL");
		while(operands.size()>1) {
			root = new Tree(operators.pop());
			
			rightNode = operands.pop();
			leftNode = operands.pop();
			
			root.left = leftNode;
			root.right = rightNode;
			
			operands.add(root);
			
		}
		return operands.peek();
	}

	/** Returns the precedence of an operator.
    @param oper: operator to find its precedence
    @return: precedence of the operator
    @throws IllegalArgumentException: operator is not supported.
	 */
	public static int precedence(String oper) {
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
	public static String infixToPostfix(String infixExp) {
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
	
/** Evaluates an expression using a expression tree. 
	 * "^", "*", "/", "%", "+", "-", ">", ">=", "<", "<=", "==", "!=", "&&", "||" 14 totals
	    @param root: tree to evaluate
	    @return: evaluation result
	*/
	public static int evaluate(Tree root){
		if(root == null) return 0;
		
		if(root.left == null && root.right == null) return Integer.valueOf(root.data);
		
		//Evaluate left subtree
		int evalLeft = evaluate(root.left);
		
		//Evaluate right subtree
		int evalRight = evaluate(root.right);
		
		//Apply operators
		if(root.data.equals("+")) return evalLeft + evalRight;
		if(root.data.equals("-")) return evalLeft - evalRight;
		if(root.data.equals("*")) return evalLeft * evalRight;
		
		if(root.data.equals("%")) return evalLeft % evalRight;
		if(root.data.equals("^")) return evalLeft ^ evalRight;
		if(root.data.equals(">")) {
			if(evalLeft > evalRight) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("<")) {
			if(evalLeft < evalRight) {
				return 1;
			}else return 0;
		}
		if(root.data.equals(">=")) {
			if(evalLeft >= evalRight) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("<=")) {
			if(evalLeft <= evalRight) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("==")) {
			if(evalLeft == evalRight) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("!=")) {
			if(evalLeft != evalRight) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("&&")) {
			if(evalLeft == 1 && evalRight == 1) {
				return 1;
			}else return 0;
		}
		if(root.data.equals("||")) {
			if(evalLeft == 1 || evalRight == 1) {
				return 1;
			}else return 0;
		}
		
		return evalLeft / evalRight;
	}
	
	
	public static void main(String[] args) {
		String second = "( 6 + ( 8 / 2 ) ) * ( 3 - ( 1 - 3 ) )";
		String infix = " ( 1 ^ 2 ^ ( 3 / 4 / 5 - 6 ) ^ ( 7 * 8 - 9 * 10 ) ) ";
		String third = "( 4 >= 4 ) && 0";
		String postfix = infixToPostfix(second);

		Tree test = postfixToTree(postfix);
		Tree test1 = infixToTree(second);
		//System.out.println(test.left.left.data);
		//System.out.println(infix);
		System.out.println(postfix);
		System.out.println();
		postorderTraversal(test);
		System.out.println();
		//System.out.println(test1.data);
		postorderTraversal(test1);
		
		System.out.println();
		
		System.out.println(eval(test1));
		


	}
}
