import java.util.ArrayList;

class TrainLine {

    private Station head;
    private Station tail;
    private int size;
    private String fn;

    TrainLine(String filename){
        fn = filename;
        extractLine(filename);
    }

    public Station getFirst(){
        return head;
    }

    public void add(String name, String XFer){
        Station newStop = new Station(name, tail, null);
        newStop.transfers = XFer;
        if(head == null){
            head = newStop;
        }else{
            tail.next = newStop;
        }
        tail = newStop;
        size++;
    }

    public void add(String name){
        Station newStop = new Station(name, tail, null);
        if(head == null){
            head = newStop;
        }else{
            tail.next = newStop;

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
                add(half[0], half[1]);
            }else{
                String half0 = stop.substring(0, stop.length()-2);
                add(half0);
            }
        }

        Station current = head;
        while(current != null){
            if(current.prev == null){
                current.adjacencyList.add(current.next);
            }else if(current.next == null){
                current.adjacencyList.add(current.prev);
            }else{
                current.adjacencyList.add(current.prev);
                current.adjacencyList.add(current.next);
            }
            current = current.next;
        }


    }



    public void receiveMergeFrom(TrainLine tl){
        head.adjacencyList.add(tl.head);
        tl.head.adjacencyList.add(head);
    }

    public void appendUp(TrainLine tl){
        head.prev = tl.tail;
        tl.tail.next = head;

        head.adjacencyList.add(tl.tail);

        head = tl.head;

        tl.tail.adjacencyList.add(head);
    }

    public void joinAt(String name1, TrainLine objLine, String objName){
        Station c = head;
        while(c != null){
            if(c.name.equals(name1)){
                break;
            }
            c = c.next;
        }

        Station objC = objLine.head;
        while(objC != null){
            if(objC.name.equals(objName)){
                break;
            }
            objC = objC.next;
        }

        assert c != null;
        c.adjacencyList.add(objC);
        assert objC != null;
        objC.adjacencyList.add(c);

    }

    public void printForward(){
        Station current = head;
        while (current != null){

            if(current.adjacencyList.size() <= 2){
                System.out.println(current+": "+current.adjacencyList);
            }else{
                System.out.print(current+": "+current.adjacencyList +"\n");
                for(int i = 0; i < current.adjacencyList.size(); i++){
                    Station cur = current.adjacencyList.get(i);

                    if(cur.prev != null){
                        while(cur.prev != null) {
                            cur = cur.prev;
                        }
                    }

                    if(!cur.name.equals(head.name)) {
                        System.out.print("<");
                        while (cur != null) {

                            if(current.adjacencyList.contains(cur)) System.out.print("(("+cur + ")) <-> ");
                            if(!current.adjacencyList.contains(cur)) System.out.print(cur + " <-> ");

                            cur = cur.next;
                        }
                        System.out.println(">");
                    }
                }
            }
            current = current.next;

        }
        System.out.println();
    }



    public int size(){return size;}

}
