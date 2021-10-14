import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GeneBankSearch {
    String[] args;
    boolean usingCache;
    CacheDriver<BTreeNode> cache;
    File btreeFile;
    File queryFile;
    int debugLevel;
    BTreeNode root;
    ScannerWrapper scan;
    int k;
    int degree;
    Scanner queryScan;

    public static void main(String[] args) {
        GeneBankSearch g = new GeneBankSearch(args);
        g.run();
    }

    public GeneBankSearch(String[] args) {
        this.args = args;
        debugLevel = 0;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        parseArgs();
        initVars();
        search();
        System.out.println("Program finsihed in "+ (System.currentTimeMillis() - startTime) + "ms");
    }

    public void search() {
        try {
            // init scanner, temp node, and target value
            queryScan = new Scanner(queryFile);
            BTreeNode currentNode;
            long targetValue;
            boolean notFound;
            long whereIsOutput;
            /**
             * While there are still queries to execute
             */
            while (queryScan.hasNext()) {
                notFound = true;
                // start looking at root
                currentNode = root;
                // get next query and convert to decimal
                targetValue = Parser.dnaToDecimal(queryScan.next());
                // while value is not found
                while (notFound) {
                    whereIsOutput = currentNode.whereIs(targetValue);
                    if (whereIsOutput == -1) {
                        notFound = false;
                    } else if (whereIsOutput == 0) {
                        // print value and frequency and break the loop
                        System.out.println(Parser.decimalToDNA(targetValue, k).toLowerCase() + ": " + currentNode.frequencyOf(targetValue));
                        notFound = false;
                    } else {
                        // get the child node and assign it to currentNode
                        currentNode = null;
                        if (usingCache) {
                            currentNode = cache.search(new BTreeNode(whereIsOutput));
                        }
                        if (currentNode == null) {
                            currentNode = scan.getNode(whereIsOutput);
                        }
                        if (usingCache)
                            cache.add(currentNode);
                    }
                }
            }
            queryScan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initVars() {
        // get metadata for the overall tree
        int[] meta = ScannerWrapper.getMetadata(btreeFile);
        k = meta[0];
        degree = meta[1];
        // init root node
        scan = new ScannerWrapper(btreeFile, degree, k);
        root = scan.getNode(meta[2]);
        if (usingCache) {
            cache.add(root);
        }
    }

    public void parseArgs() {
        usingCache = false;
        // check for correct length
        if (args.length < 3 || args.length > 5) {
            printUsage();
            System.exit(1);
        }
        // initialize BTree file
        btreeFile = new File(args[1]);
        if (!btreeFile.exists()) {
            FileNotFoundException e = new FileNotFoundException("Given BTree file does not exist.");
            e.printStackTrace();
            System.exit(1);
        }
        // with/without cache
        if (args[0].equals("1")) {
            // if cache size is not given, end
            if (args.length == 3) {
                printUsage();
                System.exit(1);
            }
            usingCache = true;
            cache = new CacheDriver<BTreeNode>("r", btreeFile, 1, Integer.parseInt(args[3]));
            // set debug level if it exists
            if (args.length == 5) {
                debugLevel = Integer.parseInt(args[4]);
            }
        } else {
            // set debug level if it exists
            if (args.length == 4) {
                debugLevel = Integer.parseInt(args[3]);
            }
        }
        // initialize query file
        queryFile = new File(args[2]);
        if (!queryFile.exists()) {
            FileNotFoundException e = new FileNotFoundException("Given query file does not exist.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void printUsage() {
        System.out.println(
                "Usage:\njava GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
    }
}