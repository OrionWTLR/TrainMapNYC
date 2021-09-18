import java.util.ArrayList;
import java.util.Arrays;

public class Station{
        String name;
        Station prev;
        Station next;
        String transfers;
        ArrayList<Station> adjacencyList = new ArrayList<>();

        Station(){
        }
        Station(String n){
            this.name = n;
        }
        Station(String n, Station prv, Station nxt){
            this.name = n;
            this.prev = prv;
            this.next = nxt;
        }
        Station(String n, Station[] adj){
            this.name = n;
            adjacencyList.addAll(Arrays.asList(adj));
        }

        public String toString(){return name;}

    }