import java.io.*;
import java.util.*;

/**
 * Main class which contains the main method which runs at startup, and many other methods for the purpose of
 * compressing and de-compressing a text file
 */
public class Main {
    public static String treeTraining = "src/english.txt";
    public static String toBeCompressed = "src/english.txt";

    /**
     * main method which runs at startup. Provides an interface to the user.
     * @param args
     */
    public static void main(String args[]){
        int input;
        while (true) {
            do {
                System.out.println("Please select option:\n-1: end program\n0: create new tree\n1: compress new file\n" +
                        "2: decompress file\n3: print current tree");
                input = Integer.parseInt(new Scanner(System.in).nextLine());
            } while (input < -1 || input > 4);

            switch (input){
                case -1: //end program
                    System.exit(0);
                    break;
                case 0: //create new tree
                    createTree();
                    break;
                case 1: //compress new
                    // file
                    compress();
                    System.out.println("File compressed");
                    break;
                case 2: //decompress file
                    StringBuilder text = new StringBuilder();
                    try (
                            InputStream inputStream = new FileInputStream("binary.bin");
                    ) {
                        int byteRead;
                        byte[] bytes = new byte[1];
                        while ((byteRead = inputStream.read()) != -1) {
                            bytes[0] = (byte) byteRead;
                            text.append(getString(bytes));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    decode(text.toString(), getTree().get(0));
                    break;
                case 3: //print current tree
                    System.out.println(getTree().get(0).toTreeString());
                    break;
            }
        }
    }

    /**
     * Method which converts the binary bytes to a String
     * @param bytes
     * @return binary String
     */
    static String getString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    /**
     * method which converts a binary String to actual binary, stored in a byte[]
     * @param s
     * @return binary byte[]
     */
    static byte[] getBinary(String s) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() % 8 != 0) {
            sBuilder.append('0');
        }
        s = sBuilder.toString();

        byte[] data = new byte[s.length() / 8];

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            }
        }
        return data;
    }

    /**
     * method to add a new character to a tree. Used when using a tree trained on a different file to the one which is
     * being compressed
     * @param newChar
     */
    public static void addToTree(char newChar){
        Node rootNode = getTree().get(0);
        Node previousNode = rootNode;
        Node currentNode = rootNode;
        while (!currentNode.isEnd()){
            previousNode = currentNode;
            currentNode = currentNode.getRightChild();
        }
        Node newTree = new Node(null, 0, new Node(newChar, 0, null,
                null), currentNode);

        previousNode.setRightChild(newTree);

        ArrayList<Node> newnewTree = new ArrayList<>();
        newnewTree.add(rootNode);

        //saves the updated tree to file
        try {
            FileOutputStream fileOut = new FileOutputStream("CompressionTree");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(newnewTree);
            objectOut.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method which gets the tree which is stored in the "CompressionTree" file
     * @return tree, stored as an ArrayList of Nodes
     */
    public static ArrayList<Node> getTree() {
        ArrayList<Node> rootNode = null;
        try {
            FileInputStream fileIn = new FileInputStream("CompressionTree");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            rootNode = (ArrayList<Node>) objectIn.readObject();

            objectIn.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rootNode;
    }

    /**
     * method which compresses the current file defined in the constant "toBeCompressed" with the tree stored in the
     * file "CompressionTree"
     */
    public static void compress(){
        Map<Character, String> dictionary  = new HashMap<>();
        dictionary = treeTraversal(getTree().get(0), dictionary, "");
        StringBuilder output = new StringBuilder();
        String charCode;
        try {
            File myObj = new File(toBeCompressed);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                output.append(dictionary.get('\n'));
                for (int i = 0; i < data.length(); i++) {
                    charCode = dictionary.get(data.charAt(i));
                    if(charCode != null){
                        output.append(charCode);
                    }
                    else{
                        addToTree(data.charAt(i));
                        System.out.println("Character added to the tree");
                        compress();
                        return;
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //saves completed binary string to "binary.bin"
        try {
            byte[] newOutput = getBinary(output.toString());
            OutputStream outputStream = new FileOutputStream("binary.bin");
            outputStream.write(newOutput);

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        System.out.println("Successfully compressed.");
    }

    /**
     * method which decodes the compressed string passed as:
     * @param code
     * @param startNode
     */
    public static void decode(String code, Node startNode){
        Node currentNode = startNode;
        StringBuilder output = new StringBuilder();
        for(int i = 0; i<code.length(); i++){
            if (currentNode.isEnd()){
                output.append(currentNode.getCharacter());
                i--;
                currentNode = startNode;
            }
            else{
                if(code.charAt(i) == '1'){
                    currentNode = currentNode.getRightChild();
                }
                else if(code.charAt(i) == '0'){
                    currentNode = currentNode.getLeftChild();
                }
            }
        }

        try {
            FileWriter myWriter = new FileWriter("decompressed.txt");
            myWriter.write(output.toString());
            myWriter.close();

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        System.out.println("Successfully decompressed.");
    }

    /**
     * method which creates a new optimum tree using the the text file given by the constant "treeTraining"
     * @return tree stored in Arraylist of Nodes
     */
    public static ArrayList<Node> createTree(){
        ArrayList<Node> nodes = new ArrayList<>();
        try {
            File myObj = new File(treeTraining);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                addCharNode(nodes, '\n');
                for (int i = 0; i < data.length(); i++) {
                    addCharNode(nodes, data.charAt(i));
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Collections.sort(nodes);

        while(nodes.size()>1){
            nodes.add(new Node(null, (nodes.get(0).getCount() + nodes.get(1).getCount()), nodes.get(0), nodes.get(1)));
            nodes.remove(0);
            nodes.remove(0);
            Collections.sort(nodes);
        }

        //stores the new tree in a file
        try {
            FileOutputStream fileOut = new FileOutputStream("CompressionTree");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(nodes);
            objectOut.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }

    /**
     * method which gets the character codes from the Huffman tree, a 1 for every right fork and a 0 for every left
     * fork to get to a character
     * @param currentNode
     * @param dictionaries
     * @param binary
     * @return a hashmap which links each character to it's binary encoding
     */
    public static Map<Character, String> treeTraversal(Node currentNode, Map<Character, String> dictionaries, String binary){
        if(currentNode.isEnd()) {
            dictionaries.put(currentNode.getCharacter(), binary);
            return dictionaries;
        }
        else{
            treeTraversal(currentNode.getLeftChild(), dictionaries, binary + "0");
            treeTraversal(currentNode.getRightChild(), dictionaries, binary + "1");
        }
        return dictionaries;
    }

    /**
     * method which is used as part of createTree() which checks to see if a there already is a Node for a character,
     * if so it will increment the count, if not a new Node will be created
     * @param nodes
     * @param character
     * @return an updated ArrayList of Nodes which contains the new character, or updated count
     */
    public static ArrayList<Node> addCharNode(ArrayList<Node> nodes, char character){
        for(int i = 0; i<nodes.size(); i++){
            if(nodes.get(i).getCharacter() == character){
                nodes.get(i).incrementCount();
                return nodes;
            }
        }
        nodes.add(new Node(character, 1, null, null));
        return nodes;
    }
}