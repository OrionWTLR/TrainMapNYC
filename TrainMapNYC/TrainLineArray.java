import java.util.ArrayList;
import java.util.HashMap;

public class TrainLineArray{
    private ArrayList<Station> stations = new ArrayList<>();
    private HashMap<Station, Integer> stationIntMap = new HashMap<>();
    private String lineName;

    TrainLineArray(String filename){
        lineName = filename.charAt(filename.length()-1)+"";
        extractLine(filename);
    }

    public void extractLine(String filename){
        Parser p = new Parser(filename);
        ArrayList<String> line = p.extractString();

        int i = 0;
        for(String stop : line){
            int e = stop.length()-1;
            if(stop.charAt(e) != '!'){
                String[] half = stop.split(",",2);
                Station withTransfers = new Station(half[0], half[1]);
                stations.add(withTransfers);
                stationIntMap.put(withTransfers, i);
            }else{
                String half0 = stop.substring(0, stop.length()-2);
                Station soloStation = new Station(half0);
                stations.add(soloStation);
                stationIntMap.put(soloStation, i);
            }
            i++;
        }
    }

    public void printLine(){
        System.out.print(lineName+" : ");
        for(Station s : stations){
            System.out.print(s.name+", "+s.lineReferences +" -> ");
        }
    }

    public ArrayList<Station> stations(){return stations;}
    public HashMap<Station, Integer> stationIntegerHashMap(){return stationIntMap;}
    public String lineName(){return lineName;}


}
