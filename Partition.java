import java.util.HashSet;
import java.util.LinkedList;

public class Partition {

	LinkedList<LinkedList<State>> myPartition;

	public LinkedList<LinkedList<State>> newPartitions(HashSet<Character> alphabet,LinkedList<LinkedList<State>> partition){
		LinkedList<LinkedList<State>> newPart = new LinkedList<LinkedList<State>>();
		boolean found = true;
		for (LinkedList<State> set : partition) {
			if(set.size() > 1) {
//				System.out.println("size "+set.size());
				for(int i=0; i<set.size()-1; i++) {
					for(int j=0; j< set.size()-i-1; j++) {
						State current = set.get(j);
						State next = set.get(j+1);
//						System.out.println("current "+current.stateID+" next "+next.stateID);
						for(Character c: alphabet) {
							found = true;
							if (current.getAllTransitions(c).get(0) !=next.getAllTransitions(c).get(0) && current.getAllTransitions(c).size()>0 && next.getAllTransitions(c).size()>0) {
//								System.out.println("Distinguishable "+current.stateID+" "+next.stateID);
								LinkedList<State> new1 = new LinkedList<State>();
								new1.add(current);
								LinkedList<State> new2 = new LinkedList<State>();
								new2.add(next);
								int idx = repElement(current,newPart);
								if(idx==-1) 
									newPart.add(new1);
								idx = repElement(next,newPart);
								if(idx==-1)
									newPart.add(new2);
								found = false;
								break;
							}									


						}
						if(found && !current.equals(next)) {
//							System.out.println("InDistinguishable "+ current.stateID+" "+next.stateID);
							LinkedList<State> new3 = new LinkedList<State>();
							int idx = repElement(current,newPart);
							int idx2 = repElement(next,newPart);
							int check = doesExist(current, next, newPart);
//							System.out.println("test "+current.stateID+" "+idx);
							if(check == -1) {
								new3.add(current);
								new3.add(next);
								newPart.add(new3);
							}
							else if (check == 1){
								LinkedList<State> temp = newPart.get(idx);
//								System.out.println(temp.getFirst().stateID);
								newPart.remove(idx);
								temp.add(next);
								newPart.add(temp);
							}
							else if( idx2== 2) {
								LinkedList<State> temp = newPart.get(idx);
//								System.out.println(temp.getFirst().stateID);
								newPart.remove(idx);
								temp.add(current);
								newPart.add(temp);
							}

						}
					}
				}
			}
			else if(set.size()==1){
				newPart.add(set);
			}
		}
		return newPart;

	}

	private int doesExist(State current, State next, LinkedList<LinkedList<State>> newPart) {
	LinkedList<State> required = new LinkedList<State>();
	boolean currentFound = false;
	boolean nextFound = false;
//
//		System.out.println("in does Exists Current Contents" );
//		for ( LinkedList<State> e : newPart) {
//			for(State i: e) {
//				System.out.print(i.stateID+" ");
//			}
//			System.out.println(" ");
//		}
		for( int i=0; i<newPart.size(); i++) {
			LinkedList<State> lst = newPart.get(i);
			for( State st : lst) {
				if(st.equals(current)) {
					currentFound = true;
					required = lst;
					i = newPart.size();
					break;
					
				}
				else if (st.equals(next)) {
					nextFound = true;
					required = lst;
					i = newPart.size();
					break;
			}

		}
		
		
	}
		if (currentFound) {
			for(State e: required) {
				if(e.equals(next))
					return 0;
			}
			return 1;
			
		}
		else if(nextFound) {
			for(State e: required) {
				if(e.equals(current))
					return 0;
			}
			return 2;
		}
		
		return -1;
		
	}

	private int repElement(State current, LinkedList<LinkedList<State>> newPart) {

//		System.out.println("Current Contents" );
//		for ( LinkedList<State> e : newPart) {
//			for(State i: e) {
//				System.out.print(i.stateID+" ");
//			}
//			System.out.println(" ");
//		}
		for( int i=0; i<newPart.size(); i++) {
			LinkedList<State> lst = newPart.get(i);
			for( State st : lst) {
				if(st.equals(current)) {
//					System.out.println("exists");
					return i;
				}
			}

		}
		return -1;
	}

	public void setPartition(LinkedList<LinkedList<State>> partition) {
		myPartition = partition;

	}


}
