import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by manshu on 2/7/15.
 */
public class State<T> implements Comparable<State<T>> {
    T item;
    State<T> parent;
    int compared_cost;
    int current_cost;
    int heuristic_cost;
    int num_goals_found;
    Set<T> goals_done;

    //Stack<State<T>> prt = new Stack<State<T>>();


    State(T item, State<T> parent) {
        this.item = item;
        this.parent = parent;
        //prt.push(parent);
        current_cost = 0;
        heuristic_cost = 0;
        compared_cost = 0;
        goals_done = new HashSet<T>();
        num_goals_found = 0;
    }

    public void addGoal(T item) {this.goals_done.add(item); this.num_goals_found++;}

//    public State<T> getParent() {
//        if (prt.empty()) return null;
//        return prt.pop();
//    }
//
//    public void addParent(State<T> parent) {
//        prt.push(parent);
//    }

    @Override
    public boolean equals(Object obj) {
        State<T> state2 = (State<T>) obj;
        return this.item.equals(state2.item);// && (parent == node2.parent);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public int compareTo(State<T> o) {
        if (compared_cost > o.compared_cost)
            return 1;
        else if (compared_cost < o.compared_cost)
            return -1;
        else
            return 0;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(item).append(" ");
        stringBuilder.append(String.valueOf(compared_cost));

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        State<String> A1 = new State<String>("A", null);
        State<String> A2 = new State<String>("A", null);

        State<String> B1 = new State<String>("B", A1);
        State<String> B2 = new State<String>("B", A1);

        System.out.println(B1 == B2);



    }
}
