/**
 * A double linked list made for Caching
 *
 * @param <T>
 */
public class DoubleLinkedList<T> {
    public Node<T> head;
    public Node<T> current;
    public Node<T> previous;
    public Node<T> tail;
    private T temp;

    public DoubleLinkedList() {
        head = current = tail = previous = null;
    }

    public boolean remove(T object) {
        restart();
        while (current != null && !current.getObject().equals(object)) {
            if (next() == null) {
                return false;
            }
        }
        if (current == null)
            return false;
        else
            return true;
    }

    public boolean hasNext() {  // true if next node exists
        return current.getNext() != null;
    }

    public Node<T> next() { // if has next, sets current node to next and returns that one
        if (hasNext()) {
            current = current.getNext();
            return current;
        } else {
            return null;
        }
    }

    public Node<T> restart() {  // current node is set to the top and returns that node
        current = head;
        return current;
    }

    public void add(T object) { // adds a node after the current node
        if (current.getPrevious() != null) {
            if (current.getNext() != null) {
                current.setNext(new Node<T>(object, current.getNext(), current));
                current.getNext().getNext().setPrevious(current.getNext());
            } else {
                current.setNext(new Node<T>(object, null, current));
                tail = current.getNext();
            }
        } else {
            if (current.getNext() != null) {
                current.setNext(new Node<T>(object, current.getNext(), current));
            } else {
                head = tail = current = new Node<T>(object);
            }
        }
    }

    public void addToFront(T object) {
        if (head == null) {
            head = tail = current = new Node<T>(object);
        } else {
            head.setPrevious(new Node<T>(object, head, null));
            head = head.getPrevious();
        }
    }

    public T removeLast() {
        if (tail == null) {
            return null;
        }
        temp = tail.getObject();
        if (head == tail) {
            head = tail = current = null;
        } else {
            tail = tail.getPrevious();
            tail.setNext(null);
        }
        return temp;
    }

    public T remove() { // removes the currently selected node and returns its value
        if (current == null) {
            return null;
        }
        temp = current.getObject();
        if (current.getPrevious() == null) {
            if (current.getNext() == null) {
                head = tail = current = previous = null;
            } else {
                current = current.getNext();
                current.setPrevious(null);
                head = current;
            }
        } else {
            if (current.getNext() == null) {
                current.getPrevious().setNext(null);
                tail = current = current.getPrevious();
            } else {
                current.getPrevious().setNext(current.getNext());
                current.getNext().setPrevious(current.getPrevious());
                current = current.getPrevious();
            }
        }
        return temp;
    }

    @Override
    public String toString() {
        String str = "";
        Node<T> yeet = head;
        while (yeet.hasNext()) {
            str += yeet.getObject().toString();
            yeet = yeet.getNext();
        }
        str += yeet.getObject().toString();
        return str;
    }
}
