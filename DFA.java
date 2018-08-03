import java.util.LinkedList;

public class DFA {
   private LinkedList<State> dfa;
    DFA(){
    	this.setDFA(new LinkedList<State>());
    	this.getDfa().clear();
        }
	private void setDFA(LinkedList<State> list) {
		this.dfa = list;
	}
   
	LinkedList<State> getDfa() {
		return this.dfa;
	}
}
