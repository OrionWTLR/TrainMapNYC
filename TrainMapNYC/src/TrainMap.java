import java.util.ArrayList;
import java.util.HashMap;

public class TrainMap {

    private static class    Station{
        private String name;
        private char onLine;
        private String linkedLines;
        private ArrayList<Station> adjacencies = new ArrayList<>();
        private final HashMap<Character, Station[]> keyedAdjacencies = new HashMap<>();

        Station(){
        }
        Station(String n){
            this.name = n;
        }
        Station(String n, Station prv, Station nxt){
            this.name = n;
            adjacencies.add(prv); adjacencies.add(nxt);
        }
        Station(String n, Station[] adj){
            this.name = n;

            ArrayList<Station> set = new ArrayList<>();
            for(Station a : adj){
                if(!set.contains(a)){
                    set.add(a);
                }
            }

            adjacencies.addAll(set);
        }
        Station(String n, ArrayList<Station> adj){
            this.name = n;
            this.adjacencies = adj;
        }
        Station(String n, char ol){
            this.name = n;
            this.onLine = ol;
        }

        public String toString(){return ""+name+" "+linkedLines+"";}
        public void print(){System.out.print(toString()+", ");}
        //this may be broken if names are supposed to be the same
        public boolean equals(Station station){return name.equals(station.name);}

        public Station next(){return adjacencies.get(1);}
        public Station prev(){return adjacencies.get(0);}

        public void setNext(Station station){
            adjacencies.set(1, station);}
        public void setPrev(Station station){
            adjacencies.set(0, station);}

        public void addAdj(Station station){
            if(station == null) {
                adjacencies.add(null); return;}

            if(adjacencies.contains(station)) return;

            for(Station s : adjacencies){
                if(s != null && s.equals(station)){
                    return;
                }
            }
            adjacencies.add(station);
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
    }

    private static class TrainLine {
        private final String FILE_NAME;
        private final ArrayList<Station> stations = new ArrayList<>();

        TrainLine(String filename){
            FILE_NAME = filename;
            extractLine(filename);
        }

        public void add(String name, String transfers){
            Station station = new Station(name, FILE_NAME.charAt(0));
            if(stations.size() == 0){
                station.adjacencies.add(null);
            }else{
                stations.get(stations.size()-1).adjacencies.set(1, station);
                station.adjacencies.add(0, stations.get(stations.size()-1));
            }

            station.adjacencies.add(null);
            station.linkedLines = transfers;
            stations.add(station);
        }

        public void extractLine(String filename){
            Parser p = new Parser(filename);
            ArrayList<String> line = p.extractString();
            for(String stop : line){
                String[] parts = stop.split(",");
                if(parts.length == 2) {
                    add(parts[0], parts[1]);
                }
            }
        }

        public void printForward(){
            int c = 0;
            for (Station station : stations) {
                if(c < stations.size()-1) System.out.print(station+" "+station.adjacencies+", ");
                if(c == stations.size()-1) System.out.print(station+" "+station.adjacencies);
                c++;
            }
            System.out.println();
        }


        public ArrayList<Station> stations(){
            return stations;
        }

    }

    private final HashMap<Character, TrainLine[]> keyedLines = new HashMap<>();
    TrainMap(){
    }

    public void makeBrooklyn(){
        makeLine("A Ozone Park");
        makeLine("A Rockaway Beach");
        makeLine("A Far Rockaway");
        makeLine("C Euclid Av");

        makeLine("T AirTrain");

        makeLine("J Cypress Hills");
        makeLine("Z Cypress Hills");
        makeLine("M Metropolitan Av");

        makeLine("L Canarsie");

        makeLine("F Coney Island");
        makeLine("G Greenpoint");

        makeLine("Q Coney Island");
        makeLine("B Brighton Beach");

        makeLine("R Bay Ridge");
        makeLine("D Coney Island");
        makeLine("N Coney Island");

        makeLine("2 Flatbush Av");
        makeLine("3 New Lots Av");
        makeLine("4 Crown Heights");
        makeLine("5 Flatbush Av");


        printMap();

        consolidate('A');
        keyedLines.forEach((K, V) -> {
            replaceDuplicates(K);
            updateAllStationMaps(V);
        });


        //next is to link all stations that connect to each other but have different names
        wrapUp("0 Connections");

        printMap();

    }

    public void updateAllStationMaps(TrainLine[] V){
        //Although the HashMap now has the new stations each station still points to an old and incomplete version of
        //that station.
        //The following will make sure each station is adjacent to the correct
        for(int i = 0; i < V[0].stations.size(); i++){
            if(i == 0){
                updateStationMap(V[0].stations.get(i), null, V[0].stations.get(i+1));
            }else if(i == V[0].stations.size()-1){
                updateStationMap(V[0].stations.get(i), V[0].stations.get(i-1), null);
            }else{
                updateStationMap(V[0].stations.get(i), V[0].stations.get(i-1), V[0].stations.get(i+1));
            }
        }
    }

    public void updateStationMap(Station station, Station w0, Station w1){
        station.keyedAdjacencies.forEach((Q, W)-> {
            W[0] = w0;
            W[1] = w1;
        });
    }

    public void makeLine(String filename){
        TrainLine line = new TrainLine(filename);
        char key = filename.charAt(0);

        if(keyedLines.get(key) != null){
            TrainLine[] old = keyedLines.get(key);
            TrainLine[] array = new TrainLine[old.length+1];

            System.arraycopy(old, 0, array, 0, old.length);
            array[array.length-1] = line;

            keyedLines.replace(key, array);
        }else{
            keyedLines.put(key, new TrainLine[]{line});
        }


    }

    public void consolidate(char letter){
        ArrayList<Station> splitLine = new ArrayList<>();
        TrainLine[] lines = keyedLines.get(letter);
        if(lines.length > 1){
            int start = 0;
            int end = 0;
            for(TrainLine line : lines){
                if(end < line.stations.size()) end = line.stations.size();
            }

            for(int i = 0; i < end; i++) {
                boolean rolling = true;

                for(int j = start; j < lines.length; j++){

                    if(j < lines.length-1 && !lines[j].stations.get(i).equals(lines[j+1].stations.get(i))){
                        rolling = false;
                    }

                    if(j == lines.length-1 && rolling) {
                        splitLine.add(lines[j].stations.get(i));
                    }

                    if(!rolling){
                        splitLine.get(splitLine.size()-1).addAdj(lines[j].stations.get(i));
                        break;
                    }

                }

                if(!rolling) {
                    i--;
                    start++;
                }

            }

        }

        TrainLine[] trainLines = keyedLines.get(letter);
        TrainLine trainline = trainLines[trainLines.length-1];
        TrainLine[] replacement = new TrainLine[]{trainline};
        keyedLines.replace(letter, replacement);

        //return splitLine;
    }

    public void replaceDuplicates(char letter){

        TrainLine trainline = keyedLines.get(letter)[0];
        for(Station s : trainline.stations){
            if(!s.linkedLines.equals("!")){

                Station[] merge = new Station[(s.linkedLines.length()+1)];
                merge[0] = s;
                int j = 1;

                for(int i = 0; i < s.linkedLines.length(); i++){
                    char c = s.linkedLines.charAt(i);
                    TrainLine[] tmp = keyedLines.get(c);

                    if(tmp == null) break;

                    TrainLine tl = tmp[0];

                    for (Station x : tl.stations) {
                        if(s.name.equals(x.name)) {
                            merge[j] = x;
                            j++;
                        }
                    }
                }

                Station singularity = new Station();
                StringBuilder links = new StringBuilder();

                for(Station m : merge){
                    if(m != null) {
                        singularity.name = m.name;
                        Station[] pair = new Station[]{m.adjacencies.get(0), m.adjacencies.get(1)};
                        singularity.adjacencies.add(pair[0]); singularity.adjacencies.add(pair[1]);
                        links.append(m.onLine);
                        singularity.keyedAdjacencies.put(m.onLine, pair);

                    }
                }
                singularity.linkedLines = links.toString();


                for(Station m : merge){
                    if(m != null && keyedLines.get(m.onLine) != null) {
                        int indexM = keyedLines.get(m.onLine)[0].stations.indexOf(m);
                        keyedLines.get(m.onLine)[0].stations.set(indexM, singularity);
                    }
                }

            }
        }

    }

    public void wrapUp(String filename){
        Parser p = new Parser(filename);
        ArrayList<String> lines = p.extractString();
        for(String line : lines){
            String[] array = line.split(",");
            char key1 = array[1].charAt(0), key2 = array[3].charAt(0);

            if(keyedLines.get(key1) != null && keyedLines.get(key2) != null) {
                Station s1 = null;
                for (Station s : keyedLines.get(key1)[0].stations) {
                    if(s.name.equals(array[0])){
                        s1 = s;
                        break;
                    }
                }

                Station s2 = null;
                for (Station s : keyedLines.get(key2)[0].stations) {
                    if(s.name.equals(array[2])) {
                        s2 = s;
                        break;
                    }
                }

                if(s1 != null || s2 != null){
                    assert s1 != null;
                    assert s2 != null;

                    s1.adjacencies.add(s2);
                    s2.adjacencies.add(s1);

                    s1.keyedAdjacencies.put(key2, new Station[]{s2});
                    s2.keyedAdjacencies.put(key1, new Station[]{s1});

                }

            }
            System.out.println();

        }

    }

    public void printWithBranches(ArrayList<Station> line){
        Station current = line.get(0);
        while(current != null){
            System.out.println(current);

            if(current.adjacencies.size() > 2){
                Station cur = current.adjacencies.get(2);
                while(cur != null){
                    System.out.print(cur+", ");
                    cur = cur.adjacencies.get(1);
                }
                System.out.println();
            }

            current = current.adjacencies.get(1);
        }
        System.out.println();
    }

    public void printWithAdj(ArrayList<Station> line){
        for(Station s : line){
            System.out.println(s+", "+s.adjacencies);
        }
        System.out.println();
    }

    public void printMap(){
        keyedLines.forEach((K, V) -> {
            if(V.length ==1) {
                System.out.print(K + "|| ");
                for (TrainLine tl : V) {
                    tl.printForward();
                }
            }else{
                System.out.println(K + "|| ");
                for (TrainLine tl : V) {
                    tl.printForward();
                }
            }

        });

        System.out.println("--");
    }

    public static void main(String[] args){
        TrainMap Brooklyn = new TrainMap();
        Brooklyn.makeBrooklyn();
    }

}
