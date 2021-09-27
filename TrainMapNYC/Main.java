public class Main {
    public static void main(String[] args){
        StationGraph Manhattan = new StationGraph("Manhattan", "Manhattan Connections");

        StationGraph Brooklyn = new StationGraph("Brooklyn", "Brooklyn Connections");

        StationGraph Queens = new StationGraph("Queens", "Queens Connections");

        StationGraph Bronx = new StationGraph("Bronx", "Bronx Connections");


        Manhattan.join(Bronx);

        Brooklyn.join(Queens);

        //Manhattan.join(Queens);

        //Manhattan.join(Brooklyn);

        //Manhattan.printMap();



    }
}
