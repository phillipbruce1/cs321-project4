import java.io.File;
import java.io.FileNotFoundException;

public class Parser {


    // instantiates a ScannerWrapper and reads file into a BTreeNode object
    public static BTreeNode fileToNode(int lineNumber, File file, int degree, int k) throws FileNotFoundException {
        ScannerWrapper X = new ScannerWrapper(file, degree, k);
        return X.getNode(lineNumber);
    }

    // calls BTreeNode.toString
    public static String nodeToFile(BTreeNode node) {
        return node.toString();
    }

    // converts binary long to String of DNA
    public static String decimalToDNA(long bin, int k) {
        String binary = Long.toBinaryString(bin);
        String retVal = "";
        String holder = "";
        int len = binary.length();

        for (int i = 0; i < (k * 2) - len; i++) {
            binary = "0" + binary;
        }

        for (int i = 0; i < k * 2; i += 2) {
            holder = binary.substring(0, 2);
            binary = binary.substring(2);

            if (holder.equals("00")) {
                retVal = retVal + "A";
            } else if (holder.equals("01")) {
                retVal = retVal + "C";
            } else if (holder.equals("10")) {
                retVal = retVal + "G";
            } else if (holder.equals("11")) {
                retVal = retVal + "T";
            }
        }
        return retVal;
    }

    // converts String DNA to a binary long
    public static long dnaToDecimal(String dna) {
        String bin = "";
        dna = dna.toUpperCase();
        for (int i = 0; i < dna.length(); i++) {
            if (dna.charAt(i) == 'A') {
                bin += "00";
            } else if (dna.charAt(i) == 'T') {
                bin += "11";
            } else if (dna.charAt(i) == 'C') {
                bin += "01";
            } else if (dna.charAt(i) == 'G') {
                bin += "10";
            } else {
                return -1L;
            }
        }
        return Long.parseLong(bin, 2);
    }

    /**
     * Adds 10 spaces in order to allocate a constant size on the disk.
     * @param input
     * @return
     */
    public static String add10Spaces(int input) {
        String output = "";
        for (int i = 0; i + Integer.toString(input).length() < 10; i++) {
            output += " ";
        }
        return output + input;
    }

    /**
     * Adds 62 spoaces in order to allocate a constant size on the disk.
     * @param input
     * @return
     */
    public static String add62Spaces(String input) {
        if (input.length() >= 62)
            return input;
        String output = "";
        for (int i = 0; i + input.length() < 62; i++) {
            output += " ";
        }
        return output + input;
    }
}
