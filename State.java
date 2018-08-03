import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class State {
	 int stateID;  // name
	 Map<Character, ArrayList<State>> nextState;  // all possible next's from a state
	 Set <State> states; // set of all states
	 boolean acceptState; // if it is a final state

	public State(int i) {
		this.setStateID(i);
		this.setAcceptState(false);
		this.setNextState( new HashMap<Character,ArrayList<State>>());		
		
	}
	public State(Set<State> states, int ID) {
		this.setStates(states);
		this.setStateID(ID);
		this.setNextState(new HashMap <Character, ArrayList<State>> ());
		
		// find if there is final state in this set of states
		for (State p : states) {
			if (p.isAcceptState()) {
				this.setAcceptState(true);
				break;
			}
		}
	}
	
	public Map<Character, ArrayList<State>> getNextState() {
		return nextState;
	}

	public void setNextState(HashMap<Character, ArrayList<State>> hashMap) {
		this.nextState = hashMap;
	}
	
	public int getStateID() {
		return stateID;
	}

	public void setStateID(int stateID) {
		this.stateID = stateID;
	}

	public boolean isAcceptState() {
		return acceptState;
	}

	public void setAcceptState(boolean acceptState) {
		this.acceptState = acceptState;
	}

	public Set <State> getStates() {
		return states;
	}

	public void setStates(Set <State> states) {
		this.states = states;
	}

	public void addTransition(State s1, char symbol) {
     // list of states connected to this state by the symbol
		ArrayList <State> connected = this.nextState.get(symbol);
		
		if (connected == null) {
			connected = new ArrayList<State> ();
			this.nextState.put(symbol, connected);
		}
		
		connected.add(s1);
		
	}
	
	public ArrayList<State> getAllTransitions(char symbol){
		if ( this.nextState.get(symbol)== null)
			return new ArrayList<State>();
		else return this.nextState.get(symbol);
	}

}
