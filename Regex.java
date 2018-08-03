import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

public class Regex {
	static HashSet< Character > alphabet = new HashSet< Character >();
	static HashSet< Character > validOperations = new HashSet< Character >();
	static Stack< NFA > theNFA = new Stack<NFA>();
	private static int stateID = 0;
	static  HashSet<State> set1 = new HashSet<State>();
	static HashSet<State> set2 = new HashSet<State>();
	static BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException {

		char[] alphabets = {'x','y','z'};
		for(char a: alphabets)
			alphabet.add(a);

		char[] validOp = {'+','*','.'};
		for (char e: validOp) 
			validOperations.add(e);

		System.out.println("Available alphabet {x,y,z}\nPress 0 to change");
		int changeAlpha =Integer.parseInt(br1.readLine());
		if(changeAlpha == 0) 
			changeAlphabet();

		for(;;) {	

			BufferedReader br = new BufferedReader(new InputStreamReader (System.in));
			stateID = 0;
			System.out.println("Enter the regex\n'End' to terminate");
			String regex = br.readLine();

			boolean valid = validate(regex);
			if (!valid) {
				System.out.println("Invalid Expression");
				System.out.println("Reenter the regular expression");
				regex = br.readLine();
			}

			if(regex.equals("End")) {
				br.close();
				break;
			}
			NFA generatedNFA = generateNFA(regex);
			DFA generatedDFA = generateDFA(generatedNFA);
			String testString;
			boolean passed;
			DFA minimized;
			System.out.println("-1.Exit\n1.NFA\n2.DFA\n3.Minimisation\n4.Simulation\n5.Minimise and Simulate");

			int choice = Integer.parseInt(br.readLine());

			switch (choice) {

			case -1:
				System.exit(0);

			case 1:
				printAutomata(generatedNFA, choice);
				break;

			case 2:
				printAutomata(generatedDFA, choice);
				break;

			case 3:
				System.out.println("Initial ");
				printAutomata(generatedDFA, choice-1);
				minimized = minimize(generatedDFA);
				System.out.println("After minimisation ");
				printAutomata(minimized, choice-1);
				break;

			case 4:
				System.out.println("Enter the string");
				testString = br.readLine();
				passed = simulate(generatedDFA,testString);
				if(passed) System.out.println("Accepted!");
				else System.out.println("Rejected");
				break;

			case 5:
				System.out.println("Enter the string");
				testString = br.readLine();		
				minimized = minimize(generatedDFA);
				passed = simulate(minimized,testString);
				if(passed) System.out.println("Accepted!");
				else System.out.println("Rejected");
				break;
			}

		}
	}

	private static boolean validate(String regex) {
		//		System.out.println("here");
		for(int i=0; i< regex.length(); i++) {
			Character ch = regex.charAt(i);
			if(!isValid(ch)&& !isOperator(ch) &&!ch.equals('(')&&!ch.equals(')'))
				return false;

		}
		return true;
	}

	private static DFA minimize(DFA generatedDFA) {
		DFA dfa = new DFA();
		LinkedList<LinkedList<State>> partition = new LinkedList<LinkedList<State>>();
		LinkedList<State> internals = new LinkedList<State>();
		LinkedList<State> terminals = new LinkedList<State>();
		for(State e: generatedDFA.getDfa()) {
			if(e.isAcceptState())
				terminals.addLast(e);
			else internals.addLast(e);
		}

		partition.add(internals);
		partition.add(terminals);

		LinkedList<LinkedList<State>> newPartition = new LinkedList<LinkedList<State>>();
		Partition partObj = new Partition();
		newPartition = partObj.newPartitions(alphabet, partition);
		while(!newPartition.equals(partition)) {
			partition = newPartition;
			newPartition = partObj.newPartitions(alphabet, partition);

		}


		Set<State> temp = new HashSet<State>();
		for(int i=0; i< partition.size(); i++) {
			temp.clear();
			for(int j=0; j< partition.get(i).size(); j++) {
				temp.add(partition.get(i).get(j));
				System.out.println(partition.get(i).get(j).stateID);
			}
			LinkedList<State> current = partition.get(i);
			State newState = new State(temp,current.getFirst().stateID );

			for(Character c: alphabet) {
				State nxt = current.getFirst().getAllTransitions(c).get(0);
				State rep = getRepresentative(nxt,partition);
				newState.addTransition(rep, c);

			}
			dfa.getDfa().addLast(newState );
		}


		return dfa;
	}



	private static State getRepresentative(State nxt, LinkedList<LinkedList<State>> partition) {
		for( LinkedList<State> lst : partition) {
			for( State st : lst) {
				if(st.equals(nxt))
					return lst.getFirst();
			}

		}
		return null;
	}

	private static boolean simulate(DFA dfa, String input) {
		State start =	dfa.getDfa().getFirst();
		if (input.equals("e")) {
			if (start.isAcceptState()) 		
				return true; 	
			else 
				return false;
		}

		for(int i=0; i< input.length(); i++) {
			State next = start.getAllTransitions(input.charAt(i)).get(0);
			start = next;
			if(next == null) break;

		}
		if(start!=null && start.isAcceptState())
			return true;

		return false;
	}

	private static void changeAlphabet() throws NumberFormatException, IOException {

		System.out.println("Enter the number of new alphabet");
		int numAlpha = Integer.parseInt(br1.readLine());
		System.out.println("Enter the alphabet");
		alphabet.clear();
		for (int i=0; i< numAlpha; i++)
			alphabet.add((char)br1.read());


	}

	private static <E> void printAutomata(E Automata, int choice) {
		LinkedList<State> allStates = new LinkedList<State>();

		if(choice == 1) {	
			NFA nfa = (NFA) Automata;
			allStates = nfa.getNfa();
		}
		else {
			DFA dfa = (DFA) Automata;
			allStates = dfa.getDfa();
		}

		Iterator<State> stItr = allStates.iterator();
		while(stItr.hasNext()) {
			State currentState = stItr.next();
			int currentStateId = currentState.stateID;
			System.out.println("StateID:\t\t\t"+"Symbol:\t\t\t"+"Dest:\t\t\t"+"reached final State?");
			Iterator<Character> alphaItr = alphabet.iterator();
			while(alphaItr.hasNext()) {
				char currentSymbol = alphaItr.next();
				Iterator<State> transItr = currentState.getAllTransitions(currentSymbol).iterator();
				while(transItr.hasNext()) {
					State dest = transItr.next();
					if(dest!=null)
						System.out.println(currentStateId+"\t\t\t\t"+currentSymbol+"\t\t\t"+dest.stateID+"\t\t\t"+ dest.isAcceptState());
					else
						System.out.println(currentStateId+"\t\t\t\t"+currentSymbol+"\t\t\t"+"trap"+"\t\t\t"+ "false");

				}

			}
			Iterator<State> epsilonTrans = currentState.getAllTransitions('e').iterator();
			while(epsilonTrans.hasNext()) {
				State dest = epsilonTrans.next();
				System.out.println(currentStateId+"\t\t\t\t"+"null"+"\t\t\t"+dest.stateID+"\t\t\t"+dest.isAcceptState());
			}
		}

	}
	public static DFA generateDFA(NFA nfa) {
		Stack <State> newStates = new Stack<State> ();

		State trap = new State(new HashSet<State>(), 100);
		set1 = new HashSet <State> ();
		set2 = new HashSet <State> ();
		DFA theDFA = new DFA ();
		stateID = 0;

		set1.add(nfa.getNfa().getFirst());
		set2 = getEpsilonTrans (set1);


		State dfaStart = new State (set2, stateID++);
		System.out.println(dfaStart.stateID);		
		theDFA.getDfa().addLast(dfaStart);
		newStates.push(dfaStart);


		while (!newStates.isEmpty()) {
			State state = newStates.pop();

			for (Character symbol : alphabet) {
				set1 = new HashSet<State> ();
				set2 = new HashSet<State> ();

				set1 = getTrans (symbol, state.getStates());
				if(set1 == null) {
					state.addTransition(trap, symbol);
					//					theDFA.getDfa().addLast(trap);
					set1 = new HashSet<State>();
					set1.add(trap);
				}
				else {
					set2 = getEpsilonTrans (set1);
					State required = isPresent(theDFA);

					if (required==null) {
						State p = new State (set2, stateID++);
						newStates.push(p);
						theDFA.getDfa().addLast(p);
						state.addTransition(p, symbol);
					} 
					else 
						state.addTransition(required, symbol);
				}

			}			
		}

		return theDFA;
	}


	private static State isPresent(DFA dfa) {
		Iterator<State> stItr = dfa.getDfa().iterator();
		while(stItr.hasNext()) {
			State s = stItr.next();
			if (s.getStates().containsAll(set2)) {
				return s;	
			}
		}	return null;
	}


	private static HashSet<State> getEpsilonTrans(HashSet<State> states) {
		Stack <State> epsilons = new Stack <State> ();
		HashSet<State> dummy = new HashSet<State>();
		dummy = states;

		for (State st : states) 
			epsilons.push(st);	

		while (!epsilons.isEmpty()) {
			State st = epsilons.pop();
			ArrayList <State> epsilonStates = st.getAllTransitions ('e');
			Iterator<State> Itr = epsilonStates.iterator();
			while(Itr.hasNext()) {
				State p = Itr.next();
				if (!dummy.contains(p)) {
					dummy.add(p);
					epsilons.push(p);
				}				
			}
		}
		return dummy;		
	}


	private static HashSet<State> getTrans(Character c, Set<State> states) {
		HashSet<State> set = new HashSet<State>();
		boolean found = false;
		for (State st : states) {			
			ArrayList<State> allStates = st.getAllTransitions(c);
			for (State p : allStates) {
				set.add(p);	
				found = true;
			}
		}
		if(found) 	return set;
		return null;
	}	


	private static boolean isOperator(char current) {
		if (validOperations.contains(current))
			return true;
		return false;
	}

	private static boolean isValid(char charAt) {
		if (alphabet.contains(charAt))
			return true;
		return false;
	}

	private static String AddConcatenation(String regex) {
		String newRegex = new String ("");
		for (int i = 0; i < regex.length() - 1; i++) {
			char current = regex.charAt(i);
			char next = regex.charAt(i+1);

			if ( isValid(current)  && isValid(next) ) {
				newRegex+= current + ".";

			} else if ( isValid(current) && next == '(' ) {
				newRegex += current + ".";

			} else if ( current == ')' && isValid(next) ) {
				newRegex += current + ".";

			} else if (current == '*'  && isValid(next) ) {
				newRegex += current + ".";

			} else if (current == '*' && next == '(' ) {
				newRegex += current + ".";

			} else if (current == ')' && next == '(') {
				newRegex += current + ".";			

			} else {
				newRegex += current;
			}
		}
		newRegex += regex.charAt(regex.length() - 1);
		return newRegex;
	}

	public static NFA generateNFA (String given_regex) {	
		Stack< Character > operators = new Stack< Character >();		
		theNFA.clear();
		operators.clear();

		given_regex = AddConcatenation (given_regex);
		for (int i=0; i< given_regex.length(); i++) {
			char current = given_regex.charAt(i);
			if (isValid(current))
				pushtoNFA(current);

			else if (isOperator(current)) {
				if(operators.isEmpty())
					operators.push(current);
				else { 
					while (!operators.isEmpty() && isPrior(current, operators.get(operators.size() - 1)) ){
						doOperation (operators);

					}
					operators.push(current);
				}
			}				

			else if (current == '(') 
				operators.push(current);

			else if (current== ')') {
				while (operators.get(operators.size()-1) != '(') {
					doOperation(operators);
				}	
				operators.pop();
			}

		}

		while (!operators.isEmpty()) 	
			doOperation(operators); 

		NFA completeNfa = theNFA.pop();
		completeNfa.getNfa().get(completeNfa.getNfa().size() - 1).setAcceptState(true);
		return completeNfa;
	}

	private static void pushtoNFA(char symbol) {
		State s0 = new State (stateID++);
		State s1 = new State (stateID++);

		s0.addTransition(s1, symbol);
		NFA nfa = new NFA ();

		nfa.getNfa().addLast(s0);
		nfa.getNfa().addLast(s1);		

		theNFA.push(nfa);
	}

	private static boolean isPrior(char currentOp, Character highestPrior) {

		if(currentOp == highestPrior || highestPrior == '*' ||highestPrior == '.') 
			return true;	
		if(currentOp == '*'|| currentOp == '.'||currentOp == '+') 	
			return false;	
		return true;	


	}

	private static void doOperation (Stack<Character> operators) {
		if (operators.size() > 0) {
			char charAt = operators.pop();

			switch (charAt) {
			case ('+'):
				union ();
			break;

			case ('.'):
				concatenation ();
			break;

			case ('*'):
				kleen ();
			break;

			default :
				System.out.println(" Unkown Symbol !");
				break;			
			}
		}
	}


	private static void kleen() {

		NFA nfa = theNFA.pop();

		State start = new State (stateID++);
		State end	= new State (stateID++);

		start.addTransition(end, 'e');
		start.addTransition(nfa.getNfa().getFirst(), 'e');

		nfa.getNfa().getLast().addTransition(end, 'e');
		nfa.getNfa().getLast().addTransition(nfa.getNfa().getFirst(), 'e');

		nfa.getNfa().addFirst(start);
		nfa.getNfa().addLast(end);


		theNFA.push(nfa);
	}

	private static void concatenation() {


		NFA nfa2 = theNFA.pop();
		NFA nfa1 = theNFA.pop();

		nfa1.getNfa().getLast().addTransition(nfa2.getNfa().getFirst(), 'e');

		for (State s : nfa2.getNfa()) {	
			nfa1.getNfa().addLast(s); 
		}

		theNFA.push (nfa1);

	}


	private static void union() {

		NFA nfa2 = theNFA.pop();
		NFA nfa1 = theNFA.pop();


		State start = new State (stateID++);
		State end	= new State (stateID++);

		start.addTransition(nfa1.getNfa().getFirst(), 'e');
		start.addTransition(nfa2.getNfa().getFirst(), 'e');


		nfa1.getNfa().getLast().addTransition(end, 'e');
		nfa2.getNfa().getLast().addTransition(end, 'e');


		nfa1.getNfa().addFirst(start);
		nfa2.getNfa().addLast(end); 


		for (State s : nfa2.getNfa()) {
			nfa1.getNfa().addLast(s);
		}

		theNFA.push(nfa1);		
	}
}
