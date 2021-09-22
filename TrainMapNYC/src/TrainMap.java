import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
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


        public int size(){return stations.size();}

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

        public void print(){
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

    private void printFileReverse(String filename){
        Parser p = new Parser(filename);
        ArrayList<String> line = p.extractString();
        for(int i = line.size()-1; i >= 0; i--){
            System.out.println(line.get(i));
        }
    }

    public void makeBrooklyn(){

        makeLine("A Far Rockaway");
        makeLine("C Euclid Av");

        makeLine("J Cypress Hills");
        makeLine("Z Cypress Hills");
        makeLine("M Metropolitan Av");

        makeLine("L Canarsie");

        makeLine("F Coney Island");
        makeLine("G Greenpoint");

        makeLine("R Bay Ridge");
        makeLine("Q Coney Island");
        makeLine("B Brighton Beach");
        makeLine("D Coney Island");
        makeLine("N Coney Island");

        makeLine("2 Flatbush Av");
        makeLine("3 New Lots Av");
        makeLine("4 Crown Heights");
        makeLine("5 Flatbush Av");

        printMapArray(original_lines);

        original_lines.forEach((K, V)->{
            TrainLine consolidation = consolidate(K);
            unsortedLinkMerge(consolidation);
        });

        overwriteAllDuplicates();

        updateAllAdj();

        AtomicReference<Integer> c = new AtomicReference<>(0);
        original_lines.forEach((key, line) -> {
            final_lines.put(key, united_intermediate.get(c.get()));
            c.getAndSet(c.get() + 1);
        });


        Parser p = new Parser("0 Connections");
        ArrayList<String> line = p.extractString();
        for(String connections : line){
            String[] parts = connections.split(",");
            String stationName1 = parts[0], stationName2 = parts[2];
            char link1 = parts[2].charAt(0), link2 = parts[3].charAt(0);




        }

        System.out.println();
        for(TrainLine tl : united_intermediate){
            tl.printWithAdj();
        }
        System.out.println();

        printMap(final_lines);
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

    private void updateAllAdj(){
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

    private TrainLine consolidate(char key){

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

        return consolidation;
    }

    private void unsortedLinkMerge(TrainLine consolidation){

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

    private void makeLine(String filename){
        TrainLine line = new TrainLine(filename);
        char key = filename.charAt(0);

        if(original_lines.get(key) != null){
            TrainLine[] old = original_lines.get(key);
            TrainLine[] array = new TrainLine[old.length+1];

            System.arraycopy(old, 0, array, 0, old.length);
            array[array.length-1] = line;

            original_lines.replace(key, array);
        }else{
            original_lines.put(key, new TrainLine[]{line});
        }
    }


    public void printMapArray(HashMap<Character, TrainLine[]> trainMap){
        trainMap.forEach((K, V) -> {
            if(V.length ==1) {
                System.out.print(K + "|| ");
                for (TrainLine tl : V) {
                    tl.print();
                }
            }else{
                System.out.println(K + "|| ");
                for (TrainLine tl : V) {
                    tl.print();
                }
            }
        });

        System.out.println("--");
    }

    public void printMap(HashMap<Character, TrainLine> trainMap) {
        trainMap.forEach((K, V) -> {
            System.out.print(K + "|| ");
            V.print();
        });
    }

    public static void main(String[] args){
        TrainMap Brooklyn = new TrainMap();
        Brooklyn.makeBrooklyn();
    }

}
