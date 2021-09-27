import java.util.*;
import java.util.concurrent.atomic.AtomicReference; //TODO: learn more about this

public class StationGraph {

    private static class Station{
        private String name, linkedLines, exiting, entering;
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

        private boolean isAnagram(Station other){

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

        private void addAdj(Station s){

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

    private static class StationList {
        private final ArrayList<Station> stations = new ArrayList<>();

        StationList(){

        }

        StationList(ArrayList<String> line){
            addAll(line);
        }

        private void add(Station station){
            for(Station s : stations){
                if(s.name.equals(station.name)) return;
            }

            stations.add(station);
        }

        private void add(String name, String transfers){
            add(name, transfers, null, null);
        }

        private void add(String name, String transfers, String exit, String enter){
            Station station = new Station(name);
            if(stations.size() == 0){
                station.adjacencies.add(null);
            }else{
                stations.get(stations.size()-1).adjacencies.set(1, station);
                station.adjacencies.add(0, stations.get(stations.size()-1));
            }

            station.adjacencies.add(null);
            station.linkedLines = transfers;

            station.exiting = exit;
            station.entering = enter;

            stations.add(station);
        }

        private Station get(int i){
            return stations.get(i);
        }

        private Station get(String name){
            for(Station s : stations) if(s.name.equals(name)) return s;
            return null;
        }

        private Station getLast(){return stations.get(stations.size()-1);}
        private Station getFirst(){return stations.get(0);}

        private boolean contains(Station target){
            for(Station s : stations){
                if(s.equals(target)) return true;
            }
            return false;
        }

        private void replaceIfNotVisited(int index, Station replacement){
            if(index < 0) return;

            if(!stations.get(index).visited) stations.set(index, replacement);

        }


        private int indexOfByName(Station target){
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

        private int indexOf(Station target){return stations.indexOf(target);}

        private void addAll(ArrayList<String> line){
            for(String stop : line){

                String[] parts = stop.split(",");
                if(parts.length == 2) {
                    add(parts[0], parts[1]);
                }else if(parts.length == 4){
                    add(parts[0], parts[1], parts[2], parts[3]);
                }

            }
        }

        public String toString(){return stations.toString();}

        private int size(){
            return stations.size();
        }

        private void println(){
            int c = 0;
            for (Station station : stations) {

                if(c < stations.size()-1) System.out.print(station+", ");
                if(c == stations.size()-1) System.out.println(station);

                c++;
            }
        }

        private void printWithAdj(){
            int c = 0;
            for (Station station : stations) {

                if(c < stations.size()-1) System.out.println(station+" "+station.adjacencies+", ");
                if(c == stations.size()-1) System.out.println(station+" "+station.adjacencies);

                c++;
            }
            System.out.println();
        }

        private void printWithExitsAndEntry(){
            int c = 0;
            for (Station station : stations) {
                if(station.exiting != null && station.entering != null) {

                    if (c < stations.size() - 1)
                        System.out.println(station + "(" + station.exiting + " -> " + station.entering + "), ");
                    if (c == stations.size() - 1)
                        System.out.println(station + " (" + station.exiting + " -> " + station.entering+")");

                }

                c++;
            }
            System.out.println();
        }

    }

    private final HashMap<Character, StationList[]> originalMap = new HashMap<>();
    private final ArrayList<StationList> intermediate = new ArrayList<>();
    private final HashMap<Character, StationList> finishedMap = new HashMap<>();
    private final ArrayList<Station> VERTICES = new ArrayList<>();
    private String connectionFileName, filename;
    StationGraph(){
    }

    StationGraph(String boroughLines, String connectionFile){
        connectionFileName = connectionFile;
        filename = boroughLines;

        makeMap();
        constructGraph();
    }

    private void constructGraph(){

        originalMap.forEach((K, V)-> consolidate(K));

        overwriteAllDuplicates();

        updateAllAdjacencies();

        //Take each TrainLine in the intermediate arraylist and put it into a character keyed hashmap
        AtomicReference<Integer> c = new AtomicReference<>(0);
        originalMap.forEach((key, line) -> {
            finishedMap.put(key, intermediate.get(c.get()));
            c.getAndSet(c.get() + 1);
        });

        updateAdjWithDifferentNames();

        finishedMap.forEach((key, line) -> VERTICES.addAll(line.stations));

    }

    private void updateAdjWithDifferentNames(){
        Parser p = new Parser(connectionFileName);
        ArrayList<String> line = p.extractString();
        for(String connections : line){
            String[] parts = connections.split(",");

            String stationName1 = parts[0], stationName2 = parts[2];
            char link1 = parts[1].charAt(0), link2 = parts[3].charAt(0);

            if(finishedMap.get(link1) != null && finishedMap.get(link1).get(stationName1) != null
                    && finishedMap.get(link2) != null && finishedMap.get(link2).get(stationName2) != null){

                Objects.requireNonNull(finishedMap.get(link1).get(stationName1)).adjacencies.add( finishedMap.get(link2).get(stationName2));
                Objects.requireNonNull(finishedMap.get(link2).get(stationName2)).adjacencies.add( finishedMap.get(link1).get(stationName1));

                String save = Objects.requireNonNull(finishedMap.get(link1).get(stationName1)).linkedLines + Objects.requireNonNull(finishedMap.get(link2).get(stationName2)).linkedLines;

                Objects.requireNonNull(finishedMap.get(link1).get(stationName1)).linkedLines = save;
                Objects.requireNonNull(finishedMap.get(link2).get(stationName2)).linkedLines = save;

            }
        }
    }

    private void overwriteAllDuplicates(){
        for(int i = 0; i < intermediate.size(); i++) {
            for (int j = 0; j < intermediate.get(i).size(); j++) {

                Station overwrite = intermediate.get(i).get(j);
                overwrite.visited = true;

                for (StationList tl : intermediate) {
                    if (tl.contains(overwrite)) {
                        int indexByName = tl.indexOfByName(overwrite);
                        tl.replaceIfNotVisited(indexByName, overwrite);
                    }
                }
            }
        }
    }

    private void updateAllAdjacencies(){
        for (StationList united_line : intermediate) {
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

        StationList currentLine = originalMap.get(key)[0];
        StationList consolidation = new StationList();
        for(Station currentStation : currentLine.stations){

            String currentLinks = currentStation.linkedLines;
            Station brandNew = new Station(currentStation.name);
            brandNew.entering = currentStation.entering;
            brandNew.exiting = currentStation.exiting;

            for(int i = 0; i < currentLinks.length(); i++){
                char link = currentLinks.charAt(i);

                if(link != '!' && originalMap.get(link) != null) {
                    StationList otherLine = originalMap.get(link)[0];

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

        intermediate.add(consolidation);
    }

    private void makeLine(String filename, ArrayList<String> line){
        StationList trainLine = new StationList(line);
        char key = filename.charAt(0);
        originalMap.put(key, new StationList[]{trainLine});
    }

    private void makeMap(){

        Parser p = new Parser(filename);
        ArrayList<String> strings = p.extractString();

        String cap = "<", bot = ">";
        for(int i = 0; i < strings.size(); i++){
            String string = strings.get(i);
            ArrayList<String> line = new ArrayList<>();
            String lineName;
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


    public void join(StationGraph otherGraph){

        finishedMap.forEach((K, line) -> {

            if(otherGraph.get(K) != null && otherGraph.getFirstAtKey(K) != null) {



                //put hasExit in Station class

                int f = line.indexOf(getFirstAtKey(K)), l = line.indexOf(getLastAtKey(K));
                int othF = otherGraph.finishedMap.get(K).indexOf(otherGraph.getFirstAtKey(K)), othL = otherGraph.finishedMap.get(K).indexOf(otherGraph.getLastAtKey(K));

                System.out.println(" "+f+" & "+l+", "+othF+" & "+othL);

                connectAt(getFirstAtKey(K), otherGraph.getFirstAtKey(K), get(K), otherGraph.get(K), K);
                connectAt(getFirstAtKey(K), otherGraph.getLastAtKey(K), get(K), otherGraph.get(K), K);
                connectAt(getLastAtKey(K), otherGraph.getFirstAtKey(K), get(K),otherGraph.get(K), K);
                connectAt(getLastAtKey(K), otherGraph.getLastAtKey(K), get(K),otherGraph.get(K), K);

                System.out.println();

            }

        });

        //otherGraph.finishedMap.forEach(finishedMap::putIfAbsent);

        System.out.println();

    }

    public void connectAt(Station s, Station othS, StationList list, StationList othList, char k){
        if(hasExit(s) && hasExit(othS)){
            String ent = s.entering, othExt = othS.exiting;
            String ext = s.exiting, othEnt = othS.entering;

            StationList brandNew = new StationList();
            if(ent.equals(othExt) && ext.equals(othEnt)) {
                System.out.println(s +" & "+othS);



            }

        }
    }

    public boolean hasExit(Station s){
        return s.exiting != null && s.entering != null;
    }

    public StationList get(Character key){return finishedMap.get(key);}

    public Station getFirstAtKey(Character key){return get(key).getFirst();}
    public Station getLastAtKey(Character key){return get(key).getLast();}


    public void printMapArray(HashMap<Character, StationList[]> trainMap){
        trainMap.forEach((K, V) -> {
            if(V.length ==1) {
                System.out.print(K + "|| ");
                for (StationList tl : V) {
                    tl.println();
                }
            }else{
                System.out.println(K + "|| ");
                for (StationList tl : V) {
                    tl.println();
                }
            }
        });

        System.out.println("--");
    }

    public void printMap(HashMap<Character, StationList> trainMap) {
        trainMap.forEach((K, V) -> {
            System.out.print(K + "|| ");
            V.println();
        });
    }

    public void printMap(){
        System.out.println(filename);
        finishedMap.forEach((K, V) -> {
            System.out.print(K + "|| ");
            V.println();
        });
        System.out.println("\n");
    }

    public void printMapWithAdj(){
        finishedMap.forEach((K, V) -> {
            System.out.println(K + "|| ");
            V.printWithAdj();
        });
        System.out.println("\n");
    }

    public void printMapWithExitsAndEntry(){
        finishedMap.forEach((K, V) -> {
            System.out.println(K + "|| ");
            V.printWithExitsAndEntry();
        });
        System.out.println("\n");
    }

    private void printFileReverse(String filename){
        Parser p = new Parser(filename);
        ArrayList<String> line = p.extractString();
        for(int i = line.size()-1; i >= 0; i--){
            System.out.println(line.get(i));
        }
    }

}