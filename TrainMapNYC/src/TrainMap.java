public class TrainMap {

    TrainMap(){

    }

    public void interlock(){
        TrainLine bayRidge = new TrainLine("Bay Ridge");
        TrainLine coneyIsland_N = new TrainLine("Coney Island N");
        TrainLine _53rdStreet = new TrainLine("53rd Street");
        TrainLine coneyIsland_D = new TrainLine("Coney Island D");
        TrainLine prospectAvSeg = new TrainLine("Prospect Ave Segment DNR");
        TrainLine coneyIsland_F = new TrainLine("Coney Island F");
        TrainLine coneyIsland_Q = new TrainLine("Coney Island Q");


        coneyIsland_N.receiveMergeFrom(bayRidge);

        coneyIsland_N.appendUp(_53rdStreet);

        coneyIsland_N.receiveMergeFrom(coneyIsland_D);

        coneyIsland_N.appendUp(prospectAvSeg);


        coneyIsland_N.joinAt("4 Av- 9 St", coneyIsland_F, "4 Av");

        coneyIsland_F.joinAt("West 8 St NY Aquarium", coneyIsland_Q, "West 8 St NY Aquarium");

        Station AtlanticAvBarclaysCtr = new Station("Atlantic Av - Barclays Ctr", new Station[]{coneyIsland_N.getFirst(), coneyIsland_Q.getFirst()});//prev and next are null, use adjacency list only

        coneyIsland_N.printForward();
        //coneyIsland_F.printForward();
        coneyIsland_Q.printForward();


        coneyIsland_N.printForward();


    }

    public static void main(String[] args){
        TrainMap map = new TrainMap();
        map.interlock();
    }

}
