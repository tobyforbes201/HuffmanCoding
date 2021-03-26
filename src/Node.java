import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Node class which contains the attributes for a tree node, used to construct the huffman tree
 */
public class Node implements Comparable, java.io.Serializable {
    private char character;
    private int count;
    private Node leftChild;
    private Node rightChild;

    /**
     * method to get Node count
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * method to get Node character
     * @return character
     */
    public char getCharacter() {
        return character;
    }

    /**
     * method to increment a character count
     */
    public void incrementCount(){
        this.count = this.count + 1;
    }

    /**
     * Class constructor for Node class
     * @param inputCharacter
     * @param inputCount
     * @param inputLeftNode
     * @param inputRightNode
     */
    public Node(Character inputCharacter, int inputCount, Node inputLeftNode, Node inputRightNode){
        if(inputCharacter != null){
            character = inputCharacter;
        }
        count = inputCount;
        leftChild = inputLeftNode;
        rightChild = inputRightNode;
    }

    /**
     * updates a right child, used when a character needs to be added to tree after the creation
     * @param rightChild
     */
    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * method which determines whether a node is a leaf
     * @return boolean
     */
    public boolean isEnd(){
        if(this.rightChild == null && this.leftChild == null){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * method to get the left child of a node
     * @return left child Node
     */
    public Node getLeftChild() {
        return leftChild;
    }

    /**
     * method to get the right child of a node
     * @return right child Node
     */
    public Node getRightChild() {
        return rightChild;
    }

    /**
     * method which finds the character with the greater count, used in the java .sort sorting algorithm
     * @param o the Node which you want to compare
     * @return an int which is the difference in character counts
     */
    @Override
    public int compareTo(Object o) {
        int compareCount=((Node)o).getCount();
        return this.count-compareCount;
    }

    /**
     * method to get convert a Node to a String
     * @return Node as a String
     */
    public String toString() {
        return "Frequency{" +
                "character=" + character +
                ", count=" + count +
                '}';
    }

    /**
     * method to convert whole tree to String for user to view
     * @return String
     */
    public String toTreeString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    /**
     * method used as part of toTreeString() to built the UI tree
     * @param buffer
     * @param prefix
     * @param childrenPrefix
     */
    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append("| " + character + " |");
        buffer.append('\n');
        List<Node> children = new ArrayList<>();
        if(rightChild != null){
            children.add(rightChild);
        }
        if(leftChild != null){
            children.add(leftChild);
        }

        for (Iterator<Node> it = children.iterator(); it.hasNext();) {
            Node next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
