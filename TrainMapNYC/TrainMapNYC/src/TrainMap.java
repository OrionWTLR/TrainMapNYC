import java.util.ArrayList;
import java.util.Arrays;

public class TrainMap {

    private static class Station{
        String name;
        ArrayList<Station> adjacencyList = new ArrayList<>();

        Station(){
        }
        Station(String n){
            this.name = n;
        }
        Station(String n, Station prv, Station nxt){
            this.name = n;
            adjacencyList.add(prv); adjacencyList.add(nxt);
        }
        Station(String n, Station[] adj){
            this.name = n;
            adjacencyList.addAll(Arrays.asList(adj));
        }

        public String toString(){return name;}
        //this may be broken if names are supposed to be the same
        public boolean equals(Station station){return name.equals(station.name);}

        public Station next(){return adjacencyList.get(1);}
        public Station prev(){return adjacencyList.get(0);}

        public void setNext(Station station){adjacencyList.set(1, station);}
        public void setPrev(Station station){adjacencyList.set(0, station);}

        public void addAdj(Station station){
            if(station == null) return;

            if(adjacencyList.contains(station)) return;

            for(Station s : adjacencyList){
                if(s != null && s.equals(station)){
                    return;
                }
            }

            adjacencyList.add(station);
        }
        public void addAdj(ArrayList<Station> stations){
            for(Station s : stations){
                addAdj(s);
            }
        }
        public Station getAdj(int i){return adjacencyList.get(i);}

        public boolean isCenter(){return false;}
    }

    private static class TrainLine {

        private Station head;
        private Station tail;
        private int size;
        private final ArrayList<Station> stations = new ArrayList<>();

        TrainLine(String filename){
            extractLine(filename);
        }

        public Station getFirst(){
            return head;
        }

        public void add(String name){
            Station newStop = new Station(name, tail, null);
            stations.add(newStop);
            if(head == null){
                head = newStop;
            }else{
                tail.setNext(newStop);
            }
            tail = newStop;
            size++;
        }

        public void extractLine(String filename){
            Parser p = new Parser(filename);
            ArrayList<String> line = p.extractString();

            for(String stop : line){
                int e = stop.length()-1;
                if(stop.charAt(e) != '!'){
                    String[] half = stop.split(",",2);
                    //add(half[0], half[1]);
                    add(half[0]);
                }else{
                    String half0 = stop.substring(0, stop.length()-2);
                    add(half0);
                }
            }

        }

        public void printForward(){
            Station current = head;
            while (current != null){

                if(current.adjacencyList.size() <= 2){
                    System.out.println(current+": "+current.adjacencyList);
                }else{
                    System.out.print(current+": "+current.adjacencyList +"\n");
                    for(int i = 0; i < current.adjacencyList.size(); i++) {
                        Station cur = current.adjacencyList.get(i);

                        if (cur != null) {
                            if (cur.prev() != null) {
                                while (cur.prev() != null) {
                                    cur = cur.prev();
                                }
                            }

                            if (!cur.name.equals(head.name)) {
                                System.out.print("<");
                                while (cur != null) {

                                    if (current.adjacencyList.contains(cur)) System.out.print("((" + cur + ")) <-> ");
                                    if (!current.adjacencyList.contains(cur)) System.out.print(cur + " <-> ");

                                    cur = cur.next();
                                }
                                System.out.println(">");
                            }
                        }
                    }
                }
                current = current.next();

            }
            System.out.println();
        }


        public int size(){return size;}
        public ArrayList<Station> stations(){return stations;}

    }

    private final ArrayList<Station> VERTICES = new ArrayList<>();
    private final Station NULL_STATION = new Station("null");
    TrainMap(){

    }

    public void Brooklyn(){
        TrainLine base_lineN = new TrainLine("Coney Island N");
        TrainLine bayRidge = new TrainLine("Bay Ridge");
        TrainLine _53rdStreet = new TrainLine("53rd Street");
        TrainLine coneyIsland_D = new TrainLine("Coney Island D");
        TrainLine prospectAv = new TrainLine("Prospect Ave");
        TrainLine coneyIsland_F = new TrainLine("Coney Island F");
        TrainLine coneyIsland_Q = new TrainLine("Coney Island Q");
        TrainLine base_line3 = new TrainLine("New Lots Av 3");
        TrainLine flatBushAve_2 = new TrainLine("FlatBush Av 2");
        TrainLine easternPkwy = new TrainLine("Eastern Pkwy");


        mergeTops(base_lineN, bayRidge);
        appendUp(base_lineN, _53rdStreet);
        mergeTops(base_lineN, coneyIsland_D);
        appendUp(base_lineN, prospectAv);
        joinAt(base_lineN, "4 Av- 9 St", coneyIsland_F, "4 Av");
        joinAt(coneyIsland_F,"West 8 St NY Aquarium", coneyIsland_Q, "West 8 St NY Aquarium");


        mergeTops(base_line3, flatBushAve_2);
        appendUp(base_line3, easternPkwy);


        Station AtlanticAvBarclaysCtr = new Station("Atlantic Av- Barclays Ctr", new Station[]{
                base_lineN.head, base_line3.head, coneyIsland_Q.head});
        base_lineN.head.addAdj(AtlanticAvBarclaysCtr);
        base_line3.head.addAdj(AtlanticAvBarclaysCtr);
        coneyIsland_Q.head.addAdj(AtlanticAvBarclaysCtr);
        addToVertices(AtlanticAvBarclaysCtr);


/*
        base_lineN.printForward();
        //coneyIsland_F.printForward();
        coneyIsland_Q.printForward();
        base_line3.printForward();*/
        printVertices();

    }

    private void mergeTops(TrainLine line1, TrainLine line2){
        line1.head.addAdj(line2.head);
        line2.head.addAdj(line1.head);
        addToVertices(line1, line2);
    }
    private void appendUp(TrainLine lower, TrainLine upper){
        upper.tail.setNext(lower.head);
        lower.head.setPrev(upper.tail);
        lower.head = upper.head;
        addToVertices(lower, upper);
    }
    private void joinAt(TrainLine t1, String n1, TrainLine t2, String n2){
        Station current1 = search(t1, n1);
        if(current1 == null) return;

        Station current2 = search(t2, n2);
        if(current2 == null) return;

        shareAdjacencies(current1, current2);
    }
    private void shareAdjacencies(Station station1, Station station2){
        station1.addAdj(new ArrayList<>(Arrays.asList(station2.prev(), station2.next())));
        station2.addAdj(new ArrayList<>(Arrays.asList(station1.prev(), station1.next())));
    }
    private void shareAdjacencies(Station one, ArrayList<Station> many){
        for(Station m : many){shareAdjacencies(one, m);}
    }

    private Station search(TrainLine trainLine, String name){
        Station current = trainLine.head;
        while(current != null){
            if(current.name.equals(name)) break;
            current = current.next();
        }
        return current;
    }


    private void addToVertices(Station station){
        if(VERTICES.contains(station)) return;
        VERTICES.add(station);
    }
    private void addToVertices(ArrayList<Station> stations){
        for(Station s : stations){
            addToVertices(s);
        }
    }
    private void addToVertices(ArrayList<Station> stations1, ArrayList<Station> stations2){
        addToVertices(stations1);
        addToVertices(stations2);
    }
    private void addToVertices(TrainLine trainLine){
        addToVertices(trainLine.stations);
    }
    private void addToVertices(TrainLine trainLine1, TrainLine trainLine2){
        addToVertices(trainLine1.stations, trainLine2.stations);
    }

    private void printVertices(){
        for(Station s : VERTICES){
            System.out.println(s+":"+s.adjacencyList);
        }
    }

    public static void main(String[] args){
        TrainMap map = new TrainMap();
        map.Brooklyn();
    }

}
