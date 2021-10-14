import java.io.File;

/**
 * Cache implementation using a double linked list
 *
 * @param <T>
 */
public class Cache<T> {

    /**
     * General Variables
     */
    private DoubleLinkedList<T> list;    // linked list to be used as cache
    public int maxSize;   // maximum size of the cache
    public int size;   // current size of cache
    private T temp;
    File file;
    private String mode;

    /**
     * Constructor
     *
     * @param maxSize
     */
    public Cache(String mode, int maxSize, File file) {
        this.maxSize = maxSize;
        size = 0;
        list = new DoubleLinkedList<T>();
        this.file = file;
        this.mode = mode;
    }

    /**
     * Finds specified object
     *
     * @param object
     * @return
     */
    public T search(T object) {
        if (exists(object)) {
            update();
            return temp;
        } else {
            return null;
        }
    }

    /**
     * Adds given object to top of cache
     *
     * @param object
     */
    public void addToTop(T object) {
        list.remove(object);
        list.addToFront(object);
        if (size == maxSize) {
            if (mode.equals("w"))
                PrintWrapper.writeNode((BTreeNode) list.removeLast(), file);
            else
                list.removeLast();
        } else {
            size++;
        }
    }

    public void flush() {
        while (list.hasNext()) {
            PrintWrapper.writeNode((BTreeNode) list.removeLast(), file);
        }
    }

    /**
     * Updates list, moving the currently selected
     * object to the top
     */
    private void update() {
        temp = list.remove();
        size--;
        if (temp == null) {
            throw new NullPointerException();
        }
        addToTop(temp);
    }

    /**
     * Overwrites the currently selected node
     *
     * @param object
     */
    public T write(T object) {
        temp = list.current.getObject();
        list.remove();
        list.add(object);
        return temp;
    }

    /**
     * Removes the currently selected node
     *
     * @return
     */
    public T remove() {
        size--;
        return list.remove();
    }

    /**
     * Adds an object after the currently selected node
     *
     * @param object
     */
    public void add(T object) {
        size++;
        list.add(object);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    /**
     * Cycles through Cache to find a given object. Desired
     * object is still selected in list after operation
     *
     * @param object
     * @return
     */
    public boolean exists(T object) {
        list.restart();
        for (int i = 0; i < size; i++) {
            if (list.current.getObject().equals(object)) {
                return true;
            } else {
                list.next();
            }
        }
        return false;
    }
}
