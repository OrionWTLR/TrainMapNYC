public class MyQueue<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T d, Node<T> n, Node<T> p) {
            data = d;
            next = n;
            prev = p;
        }
    }

    private int size = 0;
    private Node<T> head;
    private Node<T> tail;

    public void enqueue(T firstData){
        Node<T> newNode = new Node<>(firstData, head, null);

        if (tail == null) {
            tail = newNode;
        } else {
            head.prev = newNode;
        }
        head = newNode;

        size++;
    }

    public T dequeue(){
        if(size == 0){
           return null;
        }

        T firstData = tail.data;
        if(size == 1){
            tail = null;
            head = null;
            size--;
            return firstData;
        }

        tail = tail.prev;
        tail.next = null;
        size--;

        return firstData;
    }

    public void printQueue() {
        Node<T> current = head;
        System.out.print("[");
        while (current != null) {
            System.out.print(current.data);
            if (current != tail) {
                System.out.print(", ");
            }
            current = current.next;
        }
        System.out.println("]");
    }

    public boolean isEmpty(){
        return (size == 0);
    }

    public int size(){return size;}

    public String toString(){
        Node<T> current = head;
        StringBuilder s = new StringBuilder();
        s.append("[");
        while(current != null){
            s.append(current.data);
            if(current != tail) {
                s.append(", ");
            }
            current = current.next;
        }
        s.append("]");
        return s.toString();
    }

}
