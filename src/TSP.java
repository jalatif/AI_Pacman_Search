import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by manshu on 2/20/15.
 */
public class TSP {

    private int num_cities;
    private String[] cities;
    private HashMap<String, Integer> index_map;
    private double[][] cost_city;
    
    private void readFile(String file_name) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file_name));
        num_cities = Integer.parseInt(br.readLine());
        cost_city = new double[num_cities][num_cities];
        index_map = new HashMap<String, Integer>();

        cities = br.readLine().split(" ");
        int city_num = 0;
        for (String city : cities) {
            index_map.put(city, city_num++);
            System.out.print(city + " ");
        }
        System.out.println();
        for (int i = 0; i < num_cities; i++) {
            String line = br.readLine();
            String[] costs = line.split(" ");
            int col = 0;
            for (String s : costs)
                cost_city[i][col++] = (double) Integer.parseInt(s);
        }

        System.out.println(num_cities);
        for (int i = 0; i < num_cities; i++){
            for (int j = 0; j < num_cities; j++) {
                System.out.print(cost_city[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public TSP(String file_name) throws IOException{
        readFile(file_name);
    }
    
    private class Coordinate {
        Double x;
        Double y;
        String name;
        Coordinate(String s, double x, double y) {name = s; this.x = x; this.y = y;}
    }
    
    public double distFrom(Coordinate c1, Coordinate c2) {
        double lat1 = c1.x, lng1 = c1.y;
        double lat2 = c2.x, lng2 = c2.y;
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (earthRadius * c) / 1000; //km

        return dist;
    }
    private void readFile2(String file_name) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file_name));
        String line = "";
        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
        while ((line = br.readLine()) != null) {
            String words[] = line.split(" ");
            coords.add(new Coordinate(words[0], Double.parseDouble(words[1]), Double.parseDouble(words[2])));
        }
        num_cities = coords.size();
        cost_city = new double[num_cities][num_cities];
        index_map = new HashMap<String, Integer>();
        cities = new String[num_cities];
        
        int city_num = 0;
        for (Coordinate c : coords) {
            cities[city_num] = c.name;
            index_map.put(c.name, city_num++);
        }
        
        for (int i = 0; i < coords.size(); i++) {
            for (int j = 0; j < coords.size(); j++) {
                if (i == j) cost_city[i][j] = 0;
                else {
                    cost_city[i][j] = distFrom(coords.get(i), coords.get(j));
                            //Math.sqrt((coords.get(i).x - coords.get(j).x) * (coords.get(i).x - coords.get(j).x) + (coords.get(i).y - coords.get(j).y) * (coords.get(i).y - coords.get(j).y));
                }
            }
            
        }
    }
    
    public TSP(String file_name, Boolean b) throws IOException{
        readFile2(file_name);
    }
    private class State implements Comparable<State>{
        String city;
        Double edge_cost;
        Double heuristic_cost;
        Double cost;
        List<String> parent;
//        State(String s, String p, int i){
//            city = s;
//            parent = new LinkedList<String>();
//            parent.add(p);
//            cost = i;
//        }
        State(String s, List<String> p, double i){
            city = s;
            parent = p;
            edge_cost = i;
            cost = i;
        }

        @Override
        public int compareTo(State o)
        {
            return this.cost.compareTo(o.cost);
        }

        @Override
        public boolean equals(Object obj) {
            State other = (State) obj;
            if (other.parent.size() != this.parent.size()) return false;
            
            if (!this.city.equals(other.city) || this.cost == other.cost)
                return false;
            Iterator iterator1 = parent.iterator();
            Iterator iterator2 = other.parent.iterator();
            while(iterator1.hasNext()) {
                if (!iterator1.next().equals(iterator2.next()))
                    return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hashcode = 0;
            if (parent != null)
                for (String s : parent)
                    hashcode = s.hashCode() + 31 * hashcode;
            return 31 * 31 * hashcode + 31 * city.hashCode() + cost.hashCode();
        }
        
        @Override
        public String toString(){
            StringBuilder s = new StringBuilder();
            s.append(city).append(" ").append(cost);
            if (parent == null) return s.toString();
            for (String prt : parent)
                s.append(" ").append(prt);
            return s.toString();
        }
    }
    
    private double getHeuristic(State state) {
        long t1 = System.currentTimeMillis();

        ArrayList<String> goals = new ArrayList<String>();
        for (String city : cities) {
            if (state.parent == null || (!state.parent.contains(city) && !state.city.equals(city)))
                goals.add(city);
        }
        String start = state.city;
        if (state.parent != null) start = state.parent.get(0);
        
        double closest_dist_current = (double) Integer.MAX_VALUE;
        double closest_dist_start = (double) Integer.MAX_VALUE;
        for (String goal : goals) {
            if (goal.equals(state.city)) continue;
            double dist = cost_city[index_map.get(state.city)][index_map.get(goal)];
            if (dist < closest_dist_current)
                closest_dist_current = dist;
            dist = cost_city[index_map.get(start)][index_map.get(goal)];
            if (dist < closest_dist_start)
                closest_dist_start = dist;
        }
        if (closest_dist_current == (double) Integer.MAX_VALUE) closest_dist_current = 0.0;
        if (closest_dist_start == (double) Integer.MAX_VALUE) closest_dist_start = 0.0;

        PriorityQueue<Edge> queue = new PriorityQueue<Edge>();
        for (int i = 0; i < goals.size() - 1; i++)
            for (int j = i + 1; j < goals.size(); j++) {
                queue.add(new Edge(goals.get(i), goals.get(j), cost_city[index_map.get(goals.get(i))][index_map.get(goals.get(j))]));
            }
        DisjointSet<String> disjointSet = new DisjointSet<String>(goals.size());
        for (String goal : goals)
            disjointSet.makeSet(goal);

        HashSet<String> verticesInMST = new HashSet<String>();
        
        double spanning_cost = 0;
        int num_edges = 0;
        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            if (!verticesInMST.contains(edge.src)) {
                verticesInMST.add(edge.src);
            }
            if (!verticesInMST.contains(edge.dest)) {
                verticesInMST.add(edge.dest);
            }
            if (disjointSet.inSameSet(edge.src, edge.dest)) {
                //System.out.println("\nCycle");
                continue;
            }
            disjointSet.union(edge.src, edge.dest);
            num_edges++;
            spanning_cost += edge.weight;
            if (num_edges == (goals.size() - 1)) break;
        }
        System.out.println("Parents = " + state.parent + " City = " + state.city);
        System.out.println("Spanning Cost = " + spanning_cost + " Closest Dist current = " + closest_dist_current + " Closest Distance start = " + closest_dist_start);
        System.out.println("HN = " + (closest_dist_current + spanning_cost + closest_dist_start));
        
        long t2 = System.currentTimeMillis();
        System.out.println("Time taken for heuristic = " + (t2 - t1));
        return closest_dist_current + spanning_cost + closest_dist_start;
    }
    
    public ArrayList<String> tsp_search() {
        String start = cities[0];
        HashSet<State> explored_state = new HashSet<State>();
        
        PriorityQueue<State> queue = new PriorityQueue<State>();
        State start_state = new State(start, null, 0.0);
        start_state.heuristic_cost = getHeuristic(start_state);
        start_state.cost = start_state.heuristic_cost + start_state.edge_cost;
        
        queue.add(start_state);
        

        State current = null;
        while (!queue.isEmpty()) {
            long t1 = System.currentTimeMillis();
            current = queue.poll();
            System.out.println(current);
            if (current.parent != null && (current.parent.size() == cities.length) && (start.equals(current.city))) break;
            if (explored_state.contains(current)) continue;
            explored_state.add(current);

            List<String> parents = current.parent;
            for (String city : cities) {
                //if (explored_city.contains(city)) continue;
                if (city.equals(current.city)) continue;
                if (parents != null && parents.contains(city)) {
                    if (current.parent.size() == (cities.length - 1) && city.equals(start))
                        ;
                    else 
                        continue;
                }
                List<String> new_parents = new LinkedList<String>();
                if (parents != null)
                    for (String parent : parents)
                        new_parents.add(parent);
                new_parents.add(current.city);
                double gn = current.edge_cost + cost_city[index_map.get(current.city)][index_map.get(city)];
                State new_state = new State(city, new_parents, gn);
                new_state.heuristic_cost = getHeuristic(new_state);
                
                new_state.cost = new_state.heuristic_cost + new_state.edge_cost;
                queue.add(new_state);
            }
            long t2 = System.currentTimeMillis();
            System.out.println("Time taken for each iteration " + (t2 - t1));
        }
        double cost = current.edge_cost;
        current.parent.add(current.city);
        System.out.println("Cost = " + cost);
        return new ArrayList<String>(current.parent);
    }
    public static void main(String[] args) throws IOException {
        String file_name = "TSPsample.txt";
        String file_name_2 = "berlin52.tsp";

        TSP problem = new TSP(file_name);
        ArrayList<String> cities = problem.tsp_search();
        for (String city : cities) {
            System.out.println(city);
        }

    }
}
