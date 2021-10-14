/**
 * A simple Node class for a singly-linked list
 * @param <T>
 */
public class Node<T> {
    private T object;
    private Node<T> next;

    private Node<T> previous;

    public Node(T object, Node<T> next, Node<T> previous) {
        this.object = object;
        this.next = next;
        this.previous = previous;
    }

    public Node(T object, Node<T> next) {
        this.object = object;
        this.next = next;
        previous = null;
    }

    public Node(T object) {
        this.object = object;
        next = previous = null;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasPrevious() {
        return previous != null;
    }

    public Node<T> getPrevious() {
        return previous;
    }

    public void setPrevious(Node<T> previous) {
        this.previous = previous;
    }
}
