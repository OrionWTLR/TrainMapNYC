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

            ArrayList<Station> set = new ArrayList<>();
            for(Station a : adj){
                if(!set.contains(a)){
                    set.add(a);
                }
            }

            adjacencyList.addAll(set);
        }
        Station(String n, ArrayList<Station> adj){
            this.name = n;
            this.adjacencyList = adj;
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
        public void addAdj(Station[] stations){
            for(Station s : stations){
                addAdj(s);
            }
        }

        public void print(){
            System.out.print(name+": ");
            for(Station s : adjacencyList){
                System.out.println(s+": "+s.adjacencyList);
            }
            System.out.println();
        }
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
        public void add(Station newStop){
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
                    for(Station cur : current.adjacencyList) {

                        if (cur != null) {
                            if (!cur.name.equals(head.name)) {
                                System.out.print("<");
                                while (cur != null) {

                                    System.out.print(cur + " <-> ");

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

        public void printReverse(){
            Station current = tail;
            while (current != null){

                if(current.adjacencyList.size() <= 2){
                    System.out.println(current+": "+current.adjacencyList);
                }else{
                    System.out.print(current+": "+current.adjacencyList +"\n");
                    for(Station cur : current.adjacencyList) {

                        if (cur != null) {
                            if (!cur.name.equals(head.name)) {
                                System.out.print("<");
                                while (cur != null) {

                                    System.out.print(cur + " <-> ");

                                    cur = cur.prev();
                                }
                                System.out.println(">");
                            }
                        }
                    }
                }

                current = current.prev();
            }
            System.out.println();
        }

        public ArrayList<Station> stations(){
            size = stations.size();
            return stations;
        }
        public int size(){return size;}

    }

    private final ArrayList<Station> VERTICES = new ArrayList<>();
    private final Station NULL_STATION = new Station("NULL_STATION", VERTICES);
    TrainMap(){
    }

    public void makeBrooklyn(){
        TrainLine coneyIsland_N = new TrainLine("Root Coney Island N");
        TrainLine bayRidge = new TrainLine("Root Bay Ridge");
        TrainLine _53rdStreet = new TrainLine("Segment 53rd Street");
        TrainLine coneyIsland_D = new TrainLine("Root Coney Island D");
        TrainLine prospectAv = new TrainLine("Segment Prospect Ave");
        TrainLine coneyIsland_F = new TrainLine("Root Coney Island F");
        TrainLine coneyIsland_Q = new TrainLine("Root Coney Island Q");
        TrainLine newLots_3 = new TrainLine("Root New Lots Av");
        TrainLine flatBushAve_2 = new TrainLine("Root FlatBush Av");
        TrainLine easternPkwy = new TrainLine("Segment Eastern Pkwy");
        TrainLine nevinsSt = new TrainLine("Segment Nevins Street");
        TrainLine FarRockaway = new TrainLine("Segment Far Rockaway");
        TrainLine RockawayPark = new TrainLine("Segment Rockaway Park");
        TrainLine OzonePark = new TrainLine("Segment Ozone Park");
        TrainLine RockawayBlvd = new TrainLine("Root Rockaway Blvd");
        TrainLine CypressHills = new TrainLine("Root Cypress Hills");
        TrainLine CanarsiePkwy = new TrainLine("Root Canarsie Rockaway Pkwy");
        TrainLine RockawayAv = new TrainLine("Segment Rockaway Av");
        TrainLine ChaunceySt = new TrainLine("Segment Chauncey Street");
        TrainLine BushwickAv = new TrainLine("Segment Bushwick Av");
        TrainLine JayMetroR = new TrainLine("Segment JayMetroR");
        TrainLine GreenpointAv = new TrainLine("Root Greenpoint Av");


        mergeHeads(coneyIsland_N, bayRidge);
        appendUp(coneyIsland_N, _53rdStreet);
        mergeHeads(coneyIsland_N, coneyIsland_D);
        appendUp(coneyIsland_N, prospectAv);
        joinAt(coneyIsland_N, "4 Av- 9 St", coneyIsland_F, "4 Av");
        joinAt(coneyIsland_F,"West 8 St NY Aquarium", coneyIsland_Q, "West 8 St NY Aquarium");


        mergeHeads(newLots_3, flatBushAve_2);
        appendUp(newLots_3, easternPkwy);


        Station AtlanticAvBarclaysCtr  = updateHeads("Atlantic Av- Barclays Ctr", new TrainLine[]{coneyIsland_N, newLots_3, coneyIsland_Q});
        Station DeKalbAv = updateHeads("DeKalb Av", new TrainLine[]{coneyIsland_N, coneyIsland_Q});

        linkTail(nevinsSt, AtlanticAvBarclaysCtr);

        DeKalbAv.addAdj(JayMetroR.tail);
        JayMetroR.tail.setNext(DeKalbAv);


        mergeHeadAt(FarRockaway, "Broad Channel", RockawayPark);
        mergeTailToHead(RockawayBlvd, OzonePark);
        Station rock = search(RockawayBlvd, "Rockaway Blvd");
        Station aqueduct = search(FarRockaway, "Aqueduct Racetrack");
        rock.addAdj(aqueduct);
        aqueduct.setPrev(rock);

        Station BroadwayJunction = updateHeads("Broadway Junction", new TrainLine[]{RockawayBlvd, CypressHills, CanarsiePkwy});
        BroadwayJunction.setPrev(RockawayAv.tail);



        BroadwayJunction.addAdj(new Station[]{ChaunceySt.tail, BushwickAv.tail});
        RockawayAv.tail.setNext(BroadwayJunction);
        ChaunceySt.tail.setNext(BroadwayJunction);
        BushwickAv.tail.setNext(BroadwayJunction);

        joinAt(RockawayAv, "Hoyt Schermerhorn", GreenpointAv, "Hoyt Schermerhorn");

        mergeTailAt(coneyIsland_F, "Bergen St", GreenpointAv);


        Station CourStR = search(JayMetroR, "Court St");
        Station BoroughHall2 = search(nevinsSt, "Borough Hall");
        CourStR.addAdj(BoroughHall2);
        BoroughHall2.addAdj(CourStR);


        linkStations(ChaunceySt, "Lorimer St", GreenpointAv, "Metropolitan Av");
        TrainLine MyrtleWyckoffAvs = new TrainLine("Segment Myrtle");

        linkWithSegment(BushwickAv, "Myrtle Wyckoff Avs", ChaunceySt, "Myrtle Av", MyrtleWyckoffAvs);


        Station OceanPkwy = coneyIsland_Q.stations.get(coneyIsland_Q.stations.size()-1);
        coneyIsland_F.tail.addAdj(OceanPkwy);
        OceanPkwy.setNext(coneyIsland_F.tail);


        coneyIsland_Q.stations.add(coneyIsland_F.tail);
        coneyIsland_Q.tail = coneyIsland_F.tail;


        Station ConeyIslandStillwellAv = new Station("Coney Island Stillwell Av");
        ConeyIslandStillwellAv.addAdj(new Station[]{coneyIsland_F.tail, coneyIsland_N.tail, coneyIsland_D.tail});
        ConeyIslandStillwellAv.adjacencyList.add(1, null);
        updateTails(ConeyIslandStillwellAv, new TrainLine[]{coneyIsland_F, coneyIsland_N, coneyIsland_D});

        addToVertices(ConeyIslandStillwellAv);

    }


    public Station updateHeads(String name, TrainLine[] trainLines){
        Station[] heads = new Station[trainLines.length+1];
        heads[0] = null;
        for(int i = 1; i < heads.length; i++){
            heads[i] = trainLines[i-1].head;
            addToVertices(trainLines[i-1]);
        }
        Station bigStation = new Station(name, heads);

        for(TrainLine tl : trainLines){
            tl.head.setPrev((bigStation));
            tl.head = bigStation;
        }

        addToVertices(bigStation);

        return bigStation;
    }

    public void updateTails(Station station, TrainLine[] lines){

        for(TrainLine t : lines){
            t.tail.setNext(station);
            t.tail = station;
        }
    }

    private void linkTail(TrainLine t, Station s){
        t.tail.setNext(s);
        s.addAdj(t.tail);
        addToVertices(t); addToVertices(s);
    }
    private void mergeHeads(TrainLine line1, TrainLine line2){
        line1.head.addAdj(line2.head);
        line2.head.setPrev(line1.head);

        addToVertices(line1, line2);
    }
    private void mergeTailToHead(TrainLine line1, TrainLine line2){
        line1.tail.setNext(line2.head);
        line2.head.setPrev(line1.tail);

        addToVertices(line1, line2);
    }

    private void appendUp(TrainLine lower, TrainLine upper){
        lower.head.addAdj(upper.tail);
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

        addToVertices(t1, t2);
    }
    private void shareAdjacencies(Station station1, Station station2){
        station1.addAdj(new ArrayList<>(Arrays.asList(station2.prev(), station2.next())));
        station2.addAdj(new ArrayList<>(Arrays.asList(station1.prev(), station1.next())));
    }
    private void mergeHeadAt(TrainLine exists, String name, TrainLine merger){
        Station station = search(exists, name);
        station.addAdj(merger.head);
        merger.head.setPrev(station);

        addToVertices(exists, merger);
    }
    private void mergeTailAt(TrainLine exists, String name, TrainLine merger){
        Station station = search(exists, name);
        station.addAdj(merger.tail);
        merger.tail.setNext(station);

        addToVertices(exists, merger);
    }
    private void linkWithSegment(TrainLine t1, String n1, TrainLine t2, String n2, TrainLine segment){
        Station s1 = search(t1, n1);
        Station s2 = search(t2, n2);

        s1.addAdj(segment.tail.prev());
        s2.addAdj(segment.head.next());

    }
    private void linkStations(TrainLine t1, String n1, TrainLine t2, String n2){
        Station s1 = search(t1, n1);
        Station s2 = search(t2, n2);
        s1.addAdj(s2);
        s2.addAdj(s1);
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
        System.out.println();

    }

    private void printTails(){
        for(Station s : VERTICES){
            if(s.adjacencyList.get(0) == null) System.out.println(s+":"+s.adjacencyList);
        }
        System.out.println();
    }

    private void printHeads(){
        for(Station s : VERTICES){
            if(s.adjacencyList.get(1) == null) System.out.println(s+":"+s.adjacencyList);
        }
        System.out.println();
    }

    public static void main(String[] args){
        TrainMap Brooklyn = new TrainMap();
        Brooklyn.makeBrooklyn();
    }

}
