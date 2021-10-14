import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class ScannerWrapper {
    int k;
    int degree;
    Scanner fileScan;
    File file;
    int nodeDiskSize;
    Scanner gbkScanner;
    long bytesPerNode;

    public ScannerWrapper(File f) {
        try {
            if (!f.exists()) {
                throw new FileNotFoundException("Given file does not exist");
            }
            gbkScanner = new Scanner(f);
            file = f;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ScannerWrapper(File fileIn, int degree, int k) {
        if (!fileIn.exists()) {
            FileNotFoundException e = new FileNotFoundException("Given target does not exist.");
            e.printStackTrace();
            System.exit(1);
        }
        file = fileIn;
        this.degree = degree;
        this.k = k;
        nodeDiskSize = 5 + (2 * degree) + 1;
        bytesPerNode = (4 * 11) + (2) + ((degree + 1) * 11) + ((10 + 1 + 62 + 1) * degree);
    }

    public void close() {
        gbkScanner.close();
    }

    public static int[] getMetadata(File f) {
        int[] output = new int[4];
        try {
            Scanner scan = new Scanner(f);
            for (int i = 0; i < 4; i++) {
                output[i] = scan.nextInt();
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return output;
    }

    // returns next available node pointer. Used in split method in BTreeNode
    public long getNextPointer() {
        return file.length();
    }

    public BTreeNode getNode(long pointer) {
        // read file into a string
        String node = "";
        try {
            RandomAccessFile f = new RandomAccessFile(file, "r");
            f.seek(pointer);
            byte[] b = new byte[Integer.valueOf(Long.toString(bytesPerNode))];
            f.readFully(b);
            f.close();
            node = new String(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        fileScan = new Scanner(node);
        // read string into variables
        long selfPointer = fileScan.nextLong();
        int leaf = fileScan.nextInt();
        boolean isLeaf;
        if (leaf == 1)
            isLeaf = true;
        else
            isLeaf = false;
        long parentPointer = fileScan.nextLong();
        fileScan.nextInt();
        fileScan.nextInt();
        long[] childPointers = new long[degree + 1];
        String temp;
        for (int i = 0; i < degree + 1; i++) {
            temp = fileScan.next();
            if (temp.contains("-1"))
                childPointers[i] = -1;
            else
                childPointers[i] = Long.parseLong(temp);
        }
        long[] values = new long[degree];
        int[] frequency = new int[degree];
        /**
         * Added special case for -1
         */
        for (int i = 0; i < degree; i++) {
            frequency[i] = fileScan.nextInt();
            temp = fileScan.next();
            if (temp.contains("-1"))
                values[i] = -1;
            else
                values[i] = Long.parseLong(temp, 2);
        }

        fileScan.close();
        return new BTreeNode(file, k, degree, selfPointer, parentPointer, values, frequency, childPointers);
    }
}