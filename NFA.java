import java.util.LinkedList;

public class NFA {
private LinkedList<State> nfa;

   public NFA () {
	  this.setNFA(new LinkedList<State>());
	  this.getNfa().clear();
	   }	

	private void setNFA(LinkedList<State> newList) {
	  this.nfa = newList;
	
}

	public LinkedList<State> getNfa() {		
		return this.nfa;
	}

	

}
