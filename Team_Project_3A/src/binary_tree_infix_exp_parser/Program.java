package binary_tree_infix_exp_parser;

public class Program {
	
	// I will move functions to where it should be later. For now, have the functions in Program for testing
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
	public static void main(String[] args) {
		
	}
}
