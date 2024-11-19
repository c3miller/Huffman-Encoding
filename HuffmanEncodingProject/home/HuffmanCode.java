// Cameron Miller / Section: AO / 5/16/2023 / CSE 123
import java.util.*;
import java.io.*;

// The HuffmanCode class implements the Huffman coding algorithm, which is a lossless
// data compression technique. It provides methods for constructing the Huffman tree
// from frequency information, saving the tree structure and codes to a file, and
// translating encoded bits back to the original message using the Huffman tree.
public class HuffmanCode {
    private HuffmanNode root;

    // Constructs a HuffmanCode object from the parametrized array of frequencies.
    // If there exists a character with a frequency <= 0, the character will not be included. 
    // Parameter: frequencies is an array of frequencies where frequences[i] is the
    // count of the character with ASCII value i.
    public HuffmanCode(int[] frequencies) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                HuffmanNode node = new HuffmanNode((char) i, frequencies[i]);
                pq.add(node);
            }
        }
        while (pq.size() > 1) {
            HuffmanNode left = pq.remove();
            HuffmanNode right = pq.remove();

            HuffmanNode merged = new HuffmanNode('\0', left.frequency + right.frequency, left, right);
            pq.add(merged);
        }

        if (!pq.isEmpty()) {
            root = pq.remove();
        }
    }

    // Constructs a new HuffmanCode object by reading in a previously constructed code from
    // a .code file. 
    // Parameter: Scanner 'input' - Used to read in tree structure and codes. 
    public HuffmanCode(Scanner input) {
        root = new HuffmanNode();
        while (input.hasNextLine()) {
            int character = Integer.parseInt(input.nextLine());
            String code = input.nextLine();
            insertCode(root, character, code);
        }
    }

    // Private helper method
    // Inserts a character code into the HuffmanTree
    // Parameters: 
    // HuffmanNode 'node' - Current node being processed. 
    // int 'value' - The character value to insert.
    // String 'code' - String represents the character in binary form.
    private HuffmanNode insertCode(HuffmanNode node, int value, String code) {
        if (node == null) {
            node = new HuffmanNode();
        }
        if(code.isEmpty()) {
            node.value = (char) value;
        } else {
            char bit = code.charAt(0);
            if (bit =='0') {
                node.left = insertCode(node.left, value, code.substring(1));
            } else if (bit == '1') {
                node.right = insertCode(node.right, value, code.substring(1));
            }
        }
        return node;
    }

    // This method stores the current Huffman Code to the given output stream in the standard format.
    // Parameter: PrintStream 'output' - Used to write the tree structure/codes to the external file. 
    public void save(PrintStream output) {
        if (root != null) {
            save(root, output, "");
        }
    }

    // Private Helper method.
    // This method stores the current Huffman Code to the given output stream in the standard format.
    // Parameter: PrintStream 'output' - Used to write the tree structure/codes to the external file.
    // HuffmanNode 'node' - the current HuffmanNode being processed.
    // String 'code' - The current code string. 
    private void save(HuffmanNode node, PrintStream output, String code) {
        if (node.isLeaf()) {
            output.println((int) node.value);
            output.println(code);
        } else {
            save(node.left, output, code + "0");
            save(node.right, output, code + "1");
        }
    }

    // This method reads individual bits from the input stream and writes the correspoinding 
    // characters to the output. 
    // Parameters: 
    // BinInputStream 'input' - Contains the encoded bits. 
    // PrintStream 'output' - Used to write the translated message to an external file. 
    public void translate(BitInputStream input, PrintStream output) {
        HuffmanNode curr = root;
        while (input.hasNextBit()) {
            int bit = input.nextBit();
            if (bit == 0) {
                curr = curr.left;
            } else if (bit == 1) {
                curr = curr.right;
            }
            if (curr.isLeaf()) {
                output.write((char) curr.value);
                curr = root;
            }
        }
    }

    // This inner class represents a node in the Huffman Tree. 
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        private char value;
        private int frequency;
        private HuffmanNode left;
        private HuffmanNode right;

        // Constructs a new Huffman code with default data fields. 
        public HuffmanNode() {
            this.value = 0;
            this.frequency = 0;
        }

        // Constructs a new Huffman code with the given values: 
        // Parameters: 
        // char 'value' - the character value of the node. 
        // int 'frequency' - the frequency of the character. 
        public HuffmanNode(char value, int frequency) {
            this.value = value;
            this.frequency = frequency;
        }

        // Constructs a new Huffman code with the given values:
        // Parameters: 
        // char 'value' - the character value of the node. 
        // int 'frequency' - the frequency of the character.
        // HuffmanNode 'left' - the left child of the current HuffmanNode.
        // HuffmanNode 'right' - the right child of the current HuffmanNode.
        public HuffmanNode(char value, int frequency, HuffmanNode left, HuffmanNode right) {
            this.value = value;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        // Returns true if the node does not have children, false if not. 
        public boolean isLeaf() {
            if (this.left == null && this.right == null) {
                return true;
            }
            return false;
        }

        // Compares the current HuffmanNode with another HuffmanNode based on their frequencies.
        // Parameter: HuffmanNode ' other' - The other HuffmanNode to compare. 
        // Returns an integer that represents whether this HuffmanNode is less than, equal,
        // or greater than the other HuffmanNode based on whether it's positive, negative, or zero.
        public int compareTo(HuffmanNode other) {
            return this.frequency - other.frequency;
        }
    }
}
