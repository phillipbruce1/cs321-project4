import java.io.File;

/**
 * Organize values and add methods within BTreeNode? Or create BTree driver
 * class to create and manage nodes? Probably within BTreeNode. Consult Wesley.
 * 
 * NOTE: In all cases, a value of -1 should be treated as EMPTY or NULL.
 * 
 * Any time there is a value inserted into or removed from a BTreeNode, the
 * array will be reordered and sorted.
 * 
 * Add a split method which splits arrays in half when the node is full and
 * returns 2 new nodes.
 */
public class BTreeNode implements Comparable<BTreeNode> {
    private long[] childPointers;
    // store values as numbers converted from binary
    // to convert back, convert value to binary, then fill in preceeding 0s based on
    // k
    long[] values;
    int[] frequency;
    private long selfPointer;
    private long parentPointer;
    private int k;
    private int degree;
    // total number of lines each node takes up on the disk
    // 5 lines of metadata, degree + 1 lines of child pointers, degree lines of
    // values stored
    private int nodeDiskSize;
    private File file;
    private long bytesPerNode;

    /**
     * Used exclusively in cache for finding the real node
     * @param selfPointer
     */
    public BTreeNode(long selfPointer) {
        this.selfPointer = selfPointer;
    }

    public BTreeNode(File file, int k, int degree, long selfPointer, long parentPointer) {
        this.file = file;
        this.k = k;
        this.degree = degree;
        this.selfPointer = selfPointer;
        this.parentPointer = parentPointer;
        childPointers = new long[degree + 1];
        values = new long[degree];
        frequency = new int[degree];
        childPointers = initEmptyValues(childPointers);
        values = initEmptyValues(values);
        frequency = initEmptyValues(frequency);
        nodeDiskSize = 5 + (2 * degree) + 1;
        bytesPerNode = (4 * 11) + (2) + ((degree + 1) * 11) + ((10 + 1 + 62 + 1) * degree);
    }

    public BTreeNode(File file, int k, int degree, long selfPointer, long parentPointer, long[] values, int[] frequency,
            long[] childPointers) {
        this.file = file;
        this.k = k;
        this.degree = degree;
        this.selfPointer = selfPointer;
        this.parentPointer = parentPointer;
        this.childPointers = childPointers;
        this.values = values;
        this.frequency = frequency;
        nodeDiskSize = 5 + (2 * degree) + 1;
        bytesPerNode = (4 * 11) + (2) + ((degree + 1) * 11) + ((10 + 1 + 62 + 1) * degree);
    }

    public BTreeNode(File file, int k, int degree) {
        this.file = file;
        this.k = k;
        this.degree = degree;
        selfPointer = parentPointer = -1;
        childPointers = new long[degree + 1];
        values = new long[degree];
        frequency = new int[degree];
        childPointers = initEmptyValues(childPointers);
        values = initEmptyValues(values);
        frequency = initEmptyValues(frequency);
        nodeDiskSize = 5 + (2 * degree) + 1;
        bytesPerNode = (4 * 11) + (2) + ((degree + 1) * 11) + ((10 + 1 + 62 + 1) * degree);
    }

    public int getDegree() {
        return degree;
    }

    public int getNodeDiskSize() {
        return nodeDiskSize;
    }

    /**
     * Attempts to add newValue to node
     * 
     * IMPORTANT: Before running this method, check if node is full If it is, split
     * it, write the changes to the disk, then try again
     * 
     * Output Definitions: 0 = successfully added to self -1 = not added because
     * node is full any other int = child pointer of where to attempt to add next
     * 
     * @param newValue
     * @return
     */
    public long add(long newValue) {
        // if the node is full, return -1
        if (isFull() && degree > 1) {
            return -1;
        }

        // if newValue already exists in node, increment frequency and return 0
        for (int i = 0; i < degree; i++) {
            if (values[i] == newValue) {
                frequency[i]++;
                return 0;
            }
        }

        // if newValue belongs in the last child node
        if (childPointers[degree] != -1 && newValue > values[degree - 1]) {
            return childPointers[degree];
        }

        // if newValue belongs in the first child node
        if (childPointers[0] != -1 && newValue < values[0]) {
            return childPointers[0];
        }

        // if newValue belongs in a child node, return that child node's pointer
        for (int i = 1; i < degree; i++) {
            // if there is a child node
            if (childPointers[i] != -1) {
                if (values[i] != -1) {
                    // if newValue belongs between the surrounding values
                    if (newValue > values[i - 1] && newValue < values[i]) {
                        return childPointers[i];
                    }
                } else {
                    return childPointers[i];
                }
            }
        }

        // add newValue to the end of the array and sort
        values[values.length - 1] = newValue;
        frequency[frequency.length - 1] = 1;
        sort();
        return 0;
    }

    /**
     * Searches for target value Functions like add method
     * 
     * OUTPUT DEFINITIONS: 0 = contained in this node -1 = value not in tree any
     * other int = child node pointer of where to look
     * 
     * @param target
     * @return
     */
    public long whereIs(long target) {
        // check this node
        for (int i = 0; i < degree; i++) {
            if (values[i] == -1) {
                break;
            }
            if (values[i] == target) {
                return 0;
            }
        }

        // check child nodes

        // if target belongs in the last child node
        if (childPointers[degree] != -1 && target > values[degree - 1]) {
            return childPointers[degree];
        }

        // if target belongs in the first child node
        if (childPointers[0] != -1 && target < values[0]) {
            return childPointers[0];
        }

        // if target belongs in a child node, return that child node's pointer
        for (int i = 1; i < degree; i++) {
            // if there is a child node
            if (childPointers[i] != -1) {
                if (values[i] != -1) {
                    // if target belongs between the surrounding values
                    if (target > values[i - 1] && target < values[i]) {
                        return childPointers[i];
                    }
                } else {
                    return childPointers[i];
                }
            }
        }

        // value is not in this tree
        return -1;
    }

    // returns true if the node is full
    public boolean isFull() {
        return getTotalObjects() == degree;
    }

    /**
     * If node is not full, return null. If degree is 1, return null. If degree is
     * 2, keep lesser value in this node and return a right child node with the
     * greater value. Moves middle and right children to new child node. If degree
     * >= 3 Split values in half, keeping one for this node Make 2 new child nodes
     * with the other values Return the child nodes in an array
     * 
     * Used only after the BTreeNode.add method has returned -1 (meaning node is full). 
     * When degree >= 3, the BTreeNode.split method will return a BTreeNode[] array
     * of length 2. The node at position 0 is the left child, while the node at position 1 is
     * the right child. When calling this method, save the returned array. Then, write 
     * the currently selected node (the one which was just split), and both nodes stored
     * in the returned array. This should be done BEFORE proceeding with any other changes.
     * These nodes should all be updated in the cache
     * 
     * node at pos 0 is left child
     * node at pos 1 is right child
     * node at pos 2 is parent 
     */
    public BTreeNode[] split() {
        BTreeNode[] arrayOut;
        if (!isFull() || degree == 1)
            return null;
        if (degree == 2) {
            // 0. initialize variables
            long[] childValues = new long[degree];
            int[] childFrequency = new int[degree];
            long[] childChildPointers = new long[degree + 1];
            childValues = initEmptyValues(childValues);
            childFrequency = initEmptyValues(childFrequency);
            childChildPointers = initEmptyValues(childChildPointers);
            arrayOut = new BTreeNode[1];
            // 1. assign values and frequency
            childValues[0] = values[1];
            values[1] = -1;
            childFrequency[0] = frequency[1];
            frequency[1] = -1;
            // 2. assign child pointers
            childChildPointers[0] = childPointers[1];
            childChildPointers[1] = childPointers[2];
            ScannerWrapper wrapper = new ScannerWrapper(file, degree, k);
            childPointers[1] = wrapper.getNextPointer();
            childPointers[2] = -1;
            // 3. instantiate BTreeNode
            arrayOut[0] = new BTreeNode(file, k, degree, childPointers[1], selfPointer, childValues, childFrequency,
                    childChildPointers);
        } else {
            // 0. initialize variables
            long[] leftValues = new long[degree];
            long[] rightValues = new long[degree];
            int[] leftFrequency = new int[degree];
            int[] rightFrequency = new int[degree];
            long[] leftChildPointers = new long[degree + 1];
            long[] rightChildPointers = new long[degree + 1];
            leftValues = initEmptyValues(leftValues);
            rightValues = initEmptyValues(rightValues);
            leftFrequency = initEmptyValues(leftFrequency);
            rightFrequency = initEmptyValues(rightFrequency);
            leftChildPointers = initEmptyValues(leftChildPointers);
            rightChildPointers = initEmptyValues(rightChildPointers);
            arrayOut = new BTreeNode[2];
            // 1. assign values and frequency, as well as child pointers if possible
            int middle = values.length / 2;
            for (int i = 0; i < degree; i++) {
                if (i < middle) {
                    leftValues[i] = values[i];
                    values[i] = -1;
                    leftFrequency[i] = frequency[i];
                    frequency[i] = -1;
                    if (childPointers[i] != -1) {
                        leftChildPointers[i] = childPointers[i];
                        childPointers[i] = -1;
                    }
                } else if (i > middle) {
                    rightValues[i - middle - 1] = values[i];
                    values[i] = -1;
                    rightFrequency[i - middle - 1] = frequency[i];
                    frequency[i] = -1;
                    if (childPointers[i] != -1) {
                        rightChildPointers[i - middle - 1] = childPointers[i];
                        childPointers[i] = -1;
                    }
                } else if (i == middle && childPointers[i] != -1) {
                    // if the middle childPointer exists, that means the subtree is less than the
                    // mdiddle value
                    // therefore, must be assign to left (smaller) subtree
                    leftChildPointers[i] = childPointers[i];
                    childPointers[i] = -1;
                }
            }
            // move last value to beginning
            values[0] = values[middle];
            values[middle] = -1;
            frequency[0] = frequency[middle];
            frequency[middle] = -1;
            // assign next childPointer
            ScannerWrapper wrapper = new ScannerWrapper(file, degree, k);
            childPointers[0] = wrapper.getNextPointer();
            childPointers[1] = childPointers[0] + bytesPerNode;
            // 2. instantiate BTreeNodes
            arrayOut[0] = new BTreeNode(file, k, degree, childPointers[0], selfPointer, leftValues, leftFrequency,
                    leftChildPointers);
            arrayOut[1] = new BTreeNode(file, k, degree, childPointers[1], selfPointer, rightValues, rightFrequency,
                    rightChildPointers);
        }
        return arrayOut;
    }

    @Override
    public boolean equals(Object obj) {
        BTreeNode node = (BTreeNode) obj;
        return selfPointer == node.getSelfPointer();
    }

    public int compareTo(BTreeNode n) {
        if (values[0] - n.getValues()[0] < 0) {
            return 1;
        } else if (values[0] - n.getValues()[0] > 0) {
            return -1;
        } else {
            return 0;
        }
    }

    public int frequencyOf(long target) {
        for (int i = 0; i < degree && values[i] != -1; i++) {
            if (values[i] == target) {
                return frequency[i];
            }
        }
        return -1;
    }

    public long[] getValues() {
        return values;
    }

    public long[] getChildren() {
        return childPointers;
    }

    public long getSelfPointer() {
        return selfPointer;
    }

    public void setSelfPointer(int selfPointer) {
        this.selfPointer = selfPointer;
    }

    public long getParentpointer() {
        return parentPointer;
    }

    public void setParentPointer(int parentPointer) {
        this.parentPointer = parentPointer;
    }

    public int[] getFrequency() {
        return frequency;
    }

    public int getTotalObjects() {
        int totalObjects = 0;
        for (int i = 0; i < degree; i++) {
            // what if stored value is 000000? use size k to infer values stored, but how to
            // discern empty vs 000000?
            // default array filled with -1?
            if (values[i] != -1L)
                totalObjects++;
        }
        return totalObjects;
    }

    public int getTotalChildren() {
        int totalChildren = 0;
        for (int i = 0; i < childPointers.length; i++) {
            if (childPointers[i] != -1)
                totalChildren++;
        }
        return totalChildren;
    }

    @Override
    public String toString() {
        String output = "";
        output += Parser.add10Spaces(Integer.valueOf(Long.toString(selfPointer))) + "\n";
        if (getTotalChildren() == 0)
            output += "1\n";
        else
            output += "0\n";
        output += Parser.add10Spaces(Integer.valueOf(Long.toString(parentPointer))) + "\n";
        output += Parser.add10Spaces(getTotalObjects()) + "\n";
        output += Parser.add10Spaces(getTotalChildren()) + "\n";
        for (int i = 0; i < degree + 1; i++) {
            output += Parser.add10Spaces(Integer.valueOf(Long.toString(childPointers[i]))) + "\n";
        }
        for (int i = 0; i < degree; i++) {
            output += Parser.add10Spaces(frequency[i]) + " " + Parser.add62Spaces(valueToString(values[i]));
            if (i != degree - 1)
                output +="\n";
        }
        return output;
    }

    /**
     * Added special case for -1
     */
    private String valueToString(long input) {
        if (input == -1) {
            return Long.toString(-1);
        }
        String output = "";
        String binary = Long.toBinaryString(input);
        for (int i = binary.length(); i < k * 2; i++)
            output += "0";
        return output + binary;
    }

    // initializes given int array to -1
    private static int[] initEmptyValues(int[] arr) {
        int[] output = arr;
        for (int i = 0; i < output.length; i++) {
            output[i] = -1;
        }
        return output;
    }

    // initializes given int array to -1
    private static long[] initEmptyValues(long[] arr) {
        long[] output = arr;
        for (int i = 0; i < output.length; i++) {
            output[i] = -1;
        }
        return output;
    }

    // sort values and frequency based on ordering of values
    // afterwards, sort childPointers based on ordered values
    private void sort() {
        long templong = values[degree - 1];
        int tempint = frequency[degree - 1];
        for (int i = 0; i < degree; i++) {
            if (values[i] == -1) {
                values[i] = templong;
                frequency[i] = tempint;
                values[degree - 1] = -1;
                frequency[degree - 1] = -1;
                return;
            }
            if (values[i] > templong) {
                shift(i);
                values[i] = templong;
                frequency[i] = tempint;
                return;
            }
        }
        sortChildren();
    }

    private void sortChildren() {
        long[] temp = childPointers; // used to store pointers and iterate through them
        BTreeNode tempNode;
        for (int i = 0; i < degree + 1; i++) {
            childPointers[i] = -1;
        }
        ScannerWrapper wrapper = new ScannerWrapper(file, degree, k);
        for (int i = 0; i < degree + 1; i++) { // iterate through childPointers
            if (temp[i] != -1) { // if a pointer is stored here
                tempNode = wrapper.getNode(temp[i]);
                if (i == degree) { // if we are at the end
                    if (tempNode.getValues()[0] < values[i - 1]) { // if the last child pointer does not belong
                                                                   // there
                        for (int k = 0; k < degree + 1; k++) { // iterate until you find where it belongs
                            if (k == degree - 1) {
                                childPointers[degree] = tempNode.getSelfPointer();
                                break;
                            } else if (k == 0) {
                                if (tempNode.getValues()[0] < values[k]) {
                                    childPointers[k] = tempNode.getSelfPointer();
                                    break;
                                }
                            } else if (tempNode.getValues()[0] < values[k] && tempNode.getValues()[0] > values[k - 1]) {
                                childPointers[k] = tempNode.getSelfPointer();
                                break;
                            }
                        }
                        childPointers[i] = -1; // reset the value of that child pointer to empty

                    }
                } else if (tempNode.getValues()[0] > values[i] || tempNode.getValues()[0] < values[i - 1]) { // if that
                                                                                                             // pointer
                                                                                                             // does not
                                                                                                             // belong
                                                                                                             // there
                    for (int k = 0; k < degree + 1; k++) { // iterate until you find where it belongs
                        if (k == degree - 1) {
                            childPointers[degree] = tempNode.getSelfPointer();
                            break;
                        } else if (k == 0) {
                            if (tempNode.getValues()[0] < values[k]) {
                                childPointers[k] = tempNode.getSelfPointer();
                                break;
                            }
                        } else if (tempNode.getValues()[0] < values[k] && tempNode.getValues()[0] > values[k - 1]) {
                            childPointers[k] = tempNode.getSelfPointer();
                            break;
                        }
                    }
                    childPointers[i] = -1; // reset the value of that child pointer to empty
                }
            }
        }
    }

    // shifts values and frequency to make space for new entries
    private void shift(int position) {
        for (int i = degree - 2; i >= position; i--) {
            values[i + 1] = values[i];
            frequency[i + 1] = frequency[i];
        }
    }
}