import java.util.*;
import java.util.concurrent.atomic.AtomicReference; //TODO: learn more about this

public class TrainMap {

    private static class Station{
        private String name;
        private String linkedLines;
        private final ArrayList<Station> adjacencies = new ArrayList<>();
        private final HashSet<String> adjNames = new HashSet<>();
        private boolean visited = false;

        Station(){
        }
        Station(String n){
            this.name = n;
        }

        public String toString(){return ""+name+" "+linkedLines+"";}
        public boolean equals(Station station){

            if(name.equals(station.name)){

                if(linkedLines == null && station.linkedLines == null){
                    return true;
                }else if(linkedLines != null && station.linkedLines != null) {
                    return isAnagram(station);
                }

            }

            return false;
        }

        public boolean isAnagram(Station other){

            if(linkedLines.length() != other.linkedLines.length()) return false;

            boolean[] hasCharI = new boolean[linkedLines.length()];
            for(int i = 0; i < linkedLines.length(); i++){
                boolean hasCharJ = false;
                for(int j = 0; j < other.linkedLines.length(); j++){
                    if(linkedLines.charAt(i) == other.linkedLines.charAt(j)){
                        hasCharJ = true; break;
                    }
                }
                hasCharI[i] = hasCharJ;
            }

            for(boolean b : hasCharI) {
                if(!b) return false;
            }

            return true;
        }

        public void addAdj(Station s){

            if(s != null && adjNames.contains(s.name)){
                for(int i = 0; i < adjacencies.size(); i++){
                    if(adjacencies.get(i) != null && adjacencies.get(i).name.equals(s.name)){
                        adjacencies.set(i, s);
                    }
                }
            }

            if(s != null) adjNames.add(s.name);
            if(s == null) adjNames.add(null);

            if(adjacencies.contains(s)) return;
            adjacencies.add(s);
        }

    }

    private static class TrainLine {
        private final ArrayList<Station> stations = new ArrayList<>();

        TrainLine(){

        }

        TrainLine(ArrayList<String> line){
            for(String stop : line){
                String[] parts = stop.split(",");
                if(parts.length == 2) {
                    add(parts[0], parts[1]);
                }
            }
        }

        TrainLine(String filename){
            extractLine(filename);
        }

        public void add(Station station){
            for(Station s : stations){
                if(s.name.equals(station.name)) return;
            }

            stations.add(station);
        }

        public void add(String name, String transfers){
            Station station = new Station(name);
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

        public Station get(int i){
            return stations.get(i);
        }

        public Station get(String name){
            for(Station s : stations) if(s.name.equals(name)) return s;
            return null;
        }

        public boolean contains(Station target){
            for(Station s : stations){
                if(s.equals(target)) return true;
            }

            return false;
        }

        public void replaceIfNotVisited(int index, Station replacement){
            if(index < 0) return;

            if(!stations.get(index).visited) stations.set(index, replacement);

        }


        public int indexOfByName(Station target){
            if(contains(target)){
                int i = 0;
                while(i < stations.size()){
                    if(stations.get(i).equals(target)) break;
                    i++;
                }
                return i;
            }
            return -1;
        }

        private void extractLine(String filename){
            Parser p = new Parser(filename);
            ArrayList<String> line = p.extractString();
            for(String stop : line){
                String[] parts = stop.split(",");
                if(parts.length == 2) {
                    add(parts[0], parts[1]);
                }
            }
        }

        public int size(){return stations.size();}

        public void println(){
            int c = 0;
            for (Station station : stations) {

                if(c < stations.size()-1) System.out.print(station+", ");
                if(c == stations.size()-1) System.out.print(station);
                c++;
            }
            System.out.println();
        }

        public void printWithAdj(){
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

    private final HashMap<Character, TrainLine[]> original_lines = new HashMap<>();
    private final ArrayList<TrainLine> united_intermediate = new ArrayList<>();
    private final HashMap<Character, TrainLine> final_lines = new HashMap<>();
    private final ArrayList<Station> VERTICES = new ArrayList<>();
    TrainMap(){
    }

    public void graph(String filename){

        makeMap(filename);

        constructGraph();

    }

    public void constructGraph(){

        original_lines.forEach((K, V)->{
            consolidate(K);
        });

        overwriteAllDuplicates();

        updateAllAdjacencies();

        //Take each TrainLine in the intermediate arraylist and put it into a character keyed hashmap
        AtomicReference<Integer> c = new AtomicReference<>(0);
        original_lines.forEach((key, line) -> {
            final_lines.put(key, united_intermediate.get(c.get()));
            c.getAndSet(c.get() + 1);
        });

        updateAdjWithDifferentNames();

        final_lines.forEach((key, line) -> VERTICES.addAll(line.stations));

    }

    private void updateAdjWithDifferentNames(){
        Parser p = new Parser("Brooklyn Connections");
        ArrayList<String> line = p.extractString();
        for(String connections : line){
            String[] parts = connections.split(",");
            String stationName1 = parts[0], stationName2 = parts[2];
            char link1 = parts[1].charAt(0), link2 = parts[3].charAt(0);

            if(final_lines.get(link1) != null && final_lines.get(link1).get(stationName1) != null &&
                    final_lines.get(link2) != null && final_lines.get(link2).get(stationName2) != null){
                Objects.requireNonNull(final_lines.get(link1).get(stationName1)).adjacencies.add( final_lines.get(link2).get(stationName2));
                Objects.requireNonNull(final_lines.get(link2).get(stationName2)).adjacencies.add( final_lines.get(link1).get(stationName1));
            }
        }
    }

    private void overwriteAllDuplicates(){
        for(int i = 0; i < united_intermediate.size(); i++) {
            for (int j = 0; j < united_intermediate.get(i).size(); j++) {

                Station overwrite = united_intermediate.get(i).get(j);
                overwrite.visited = true;

                for (TrainLine tl : united_intermediate) {
                    if (tl.contains(overwrite)) {
                        int indexByName = tl.indexOfByName(overwrite);
                        tl.replaceIfNotVisited(indexByName, overwrite);
                    }
                }
            }
        }
    }

    private void updateAllAdjacencies(){
        for (TrainLine united_line : united_intermediate) {
            for (int j = 0; j < united_line.size(); j++) {
                Station current = united_line.get(j);

                if (j == 0) {
                    current.addAdj(null);
                    current.addAdj(united_line.get(j + 1));
                } else if (j == united_line.size() - 1) {
                    current.addAdj(null);
                    current.addAdj(united_line.get(j - 1));
                } else {
                    current.addAdj(united_line.get(j - 1));
                    current.addAdj(united_line.get(j + 1));
                }
            }
        }
    }

    private void consolidate(char key){

        TrainLine currentLine = original_lines.get(key)[0];
        TrainLine consolidation = new TrainLine();
        for(Station currentStation : currentLine.stations){

            String currentLinks = currentStation.linkedLines;
            Station brandNew = new Station(currentStation.name);

            for(int i = 0; i < currentLinks.length(); i++){
                char link = currentLinks.charAt(i);

                if(link != '!' && original_lines.get(link) != null) {
                    TrainLine otherLine = original_lines.get(link)[0];

                    for(Station otherStation : otherLine.stations){
                        if(otherStation.name.equals(currentStation.name)){
                            brandNew.linkedLines = currentLinks + key;
                            break;
                        }
                    }

                }else{
                    brandNew.linkedLines = ""+key;
                }
            }

            consolidation.add(brandNew);
        }

        for(int i = 0; i < consolidation.size(); i++){
            Station con = consolidation.get(i);
            if(i == 0){
                con.addAdj(null);
                con.addAdj(consolidation.get(i+1));
            }else if(i == consolidation.size()-1){
                con.addAdj(consolidation.get(i-1));
                con.addAdj(null);
            }else{
                con.addAdj(consolidation.get(i-1));
                con.addAdj(consolidation.get(i+1));
            }

        }

        united_intermediate.add(consolidation);
    }

    private void makeLine(String filename, ArrayList<String> line){
        TrainLine trainLine = new TrainLine(line);
        char key = filename.charAt(0);
        original_lines.put(key, new TrainLine[]{trainLine});
    }

    private void makeMap(String filename){

        Parser p = new Parser(filename);
        ArrayList<String> strings = p.extractString();

        String cap = "<", bot = ">";
        for(int i = 0; i < strings.size(); i++){
            String string = strings.get(i);
            ArrayList<String> line = new ArrayList<>();
            String lineName = "";
            if(string.equals(cap)){
                lineName = strings.get(i-1);
                String current = strings.get(i+1); i+=2;
                while(!current.equals(bot)){
                    line.add(current);
                    current = strings.get(i);
                    i++;
                }
                makeLine(lineName, line);
                i--;
            }

        }
    }

    public void printMapArray(HashMap<Character, TrainLine[]> trainMap){
        trainMap.forEach((K, V) -> {
            if(V.length ==1) {
                System.out.print(K + "|| ");
                for (TrainLine tl : V) {
                    tl.println();
                }
            }else{
                System.out.println(K + "|| ");
                for (TrainLine tl : V) {
                    tl.println();
                }
            }
        });

        System.out.println("--");
    }

    public void printMap(HashMap<Character, TrainLine> trainMap) {
        trainMap.forEach((K, V) -> {
            System.out.print(K + "|| ");
            V.println();
        });
    }

    public void printMap(){
        final_lines.forEach((K, V) -> {
            System.out.print(K + "|| ");
            V.println();
            System.out.println();
        });
    }

    private void printFileReverse(String filename){
        Parser p = new Parser(filename);
        ArrayList<String> line = p.extractString();
        for(int i = line.size()-1; i >= 0; i--){
            System.out.println(line.get(i));
        }
    }

    public static void main(String[] args){
        TrainMap Brooklyn = new TrainMap();
        Brooklyn.graph("Brooklyn Lines");
        Brooklyn.printMap();
    }

}