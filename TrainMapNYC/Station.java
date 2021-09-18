import java.util.ArrayList;

public class Station{
        //transfers is a list of characters where each character references a different line
        ArrayList<String> lineReferences = new ArrayList<>();
        ArrayList<Station> directionStationLink = new ArrayList<>();
        String name;
        int index;

        public Station(String name){
            this.name = name;
        }
        public Station(String name, String Xfers){
            this.name = name;
            for(int i = 0; i < Xfers.length(); i++){
                lineReferences.add(""+Xfers.charAt(i));
            }
        }

        public void linkTo(Station sTo){
            directionStationLink.add(sTo);
        }

        public String toString(){return name;}
    }