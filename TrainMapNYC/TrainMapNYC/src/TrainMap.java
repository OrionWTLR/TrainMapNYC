import java.util.ArrayList;
import java.util.HashMap;

public class TrainMap {

    private static class Station{
        private String name;
        private char onLine;
        private String linkedLines;
        private ArrayList<Station> adjacencyList = new ArrayList<>();
        private HashMap<Character, Station[]> sortedAdjacencies = new HashMap<>();

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
        Station(String n, char ol){
            this.name = n;
            this.onLine = ol;
        }

        public String toString(){return name+" "+linkedLines;}
        public void print(){System.out.print(toString()+", ");}
        //this may be broken if names are supposed to be the same
        public boolean equals(Station station){return name.equals(station.name);}

        public Station next(){return adjacencyList.get(1);}
        public Station prev(){return adjacencyList.get(0);}

        public void setNext(Station station){adjacencyList.set(1, station);}
        public void setPrev(Station station){adjacencyList.set(0, station);}

        public void addAdj(Station station){
            if(station == null) {adjacencyList.add(null); return;}

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
    }

    private static class TrainLine {

        private final String FILE_NAME;
        private final ArrayList<Station> stations = new ArrayList<>();
        private final ArrayList<String> transfers = new ArrayList<>();

        TrainLine(String filename){
            FILE_NAME = filename;
            extractLine(filename);
        }

        public void add(String name, String xfer){
            Station station = new Station(name, FILE_NAME.charAt(0));
            if(stations.size() == 0){
                station.adjacencyList.add(null);
            }else{
                stations.get(stations.size()-1).adjacencyList.set(1, station);
                station.adjacencyList.add(0, stations.get(stations.size()-1));
            }

            station.adjacencyList.add(null);

            station.linkedLines = xfer;
            stations.add(station);
            transfers.add(xfer);
        }

        public void extractLine(String filename){
            Parser p = new Parser(filename);
            ArrayList<String> line = p.extractString();

            for(String stop : line){
                String[] halves = stop.split(",", 2);
                add(halves[0], halves[1]);
            }

        }

        public void printForward(){
            for (Station station : stations) {
                //System.out.print(station + " "+station.linkedLines+": "+ station.adjacencyList + " <-> ");
                System.out.print(station+", ");
            }
            System.out.println();
        }


        public ArrayList<Station> stations(){
            return stations;
        }

        public ArrayList<String> transfers(){return transfers;}
    }

    private final ArrayList<Station> VERTICES = new ArrayList<>();
    private final HashMap<Character, TrainLine[]> keyedLines = new HashMap<>();
    TrainMap(){
    }

    public void makeBrooklyn(){
        makeLine("A Ozone Park");
        makeLine("A Rockaway Beach");
        makeLine("A Far Rockaway");
        makeLine("C Euclid Av");

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

        consolidate('A');
        printMap();

        keyedLines.forEach((K, V) -> {
            replaceDuplicates(K);
        });

        printMap();

        /*keyedLines.forEach((K, V) -> {
            for(Station v : V[0].stations){
                System.out.print(v+": ");
                v.sortedAdjacencies.forEach((Q, W) ->{
                    System.out.print("("+Q+": "+W[0]+" <-> "+W[1]+"), ");
                });
            }
            System.out.println("\n");
        });*/

    }

    public TrainLine makeLine(String filename){
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

        return line;
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

    public ArrayList<Station> consolidate(char letter){
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
        return splitLine;
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
                        Station[] pair = new Station[]{m.adjacencyList.get(0), m.adjacencyList.get(1)};
                        singularity.adjacencyList.add(pair[0]); singularity.adjacencyList.add(pair[1]);
                        links.append(m.onLine);
                        singularity.sortedAdjacencies.put(m.onLine, pair);
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

    public void printWithBranches(ArrayList<Station> line){
        Station current = line.get(0);
        while(current != null){
            System.out.println(current);

            if(current.adjacencyList.size() > 2){
                Station cur = current.adjacencyList.get(2);
                while(cur != null){
                    System.out.print(cur+", ");
                    cur = cur.adjacencyList.get(1);
                }
                System.out.println();
            }

            current = current.adjacencyList.get(1);
        }
        System.out.println();
    }

    public void printWithAdj(ArrayList<Station> line){
        for(Station s : line){
            System.out.println(s+", "+s.adjacencyList);
        }
        System.out.println();
    }

    public static void main(String[] args){
        TrainMap Brooklyn = new TrainMap();
        Brooklyn.makeBrooklyn();
    }

}
