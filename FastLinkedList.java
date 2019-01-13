package editor;

//import static org.junit.Assert.*;
//import org.junit.Test;

import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Iterator;

public class FastLinkedList implements Iterable{
    private Node sentinel;
    private Node currentNode;
    private int currentPos;
    private StartNodeArrayList snal;

    public FastLinkedList() {
        sentinel = new Node();
        snal = new StartNodeArrayList();
        /** The start node of the first line should always be sentinel. */
        snal.addNode(sentinel, 0);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        currentNode = sentinel;
        currentPos = 0;
    }

//    public void addChar(String s) {
//        //TODO: pass fontName and Size to this method. ReadChar has default font type and size
//        double xPos;
//        double yPos;
//        Text et = new Text(0, 0, "a");
//        et.setTextOrigin(VPos.TOP);
//        et.setFont(Font.font("Verdana", 20));
//        double benchmarkHeight = Math.round(et.getLayoutBounds().getHeight());
//
//        if (s.charAt(0) == '\n') {
//            // handle RETURN character
//            // When reading a new file, place \n at the beginning of next line
//            xPos = 5;
//            yPos = currentNode.content.getY() + benchmarkHeight;
//        } else {
//            xPos = currentNode.content.getX() + currentNode.getWidth();
//            yPos = currentNode.content.getY();
//        }
//
//        Text text = new Text(xPos, yPos, s);
//        text.setTextOrigin(VPos.TOP);
//        text.setFont(Font.font("Verdana", 20));
//        Node newNode = new Node(text);
//
//        // add the new node into linked list
//        newNode.next = currentNode.next;
//        currentNode.next.prev = newNode;
//        newNode.prev = currentNode;
//        currentNode.next = newNode;
//
//        currentNode = newNode;
//        currentPos += 1;
//
//        if (s.equals("\n")) {
//            // currentNode is '\n', startNode/tempNode is the node right after cursor
//            Node startNode = currentNode.getNextNode();
//            double benchmarkX = startNode.getContent().getX();
//            double benchmarkY = startNode.getContent().getY();
//
//            Node tempNode = startNode;
//            // characters after the cursor go to next line
//            while (tempNode != sentinel && tempNode.getContent().getText().charAt(0) != '\n') {
//
//                Text t = tempNode.getContent();
//                // the difference between current text position and benchmark text is text.getX() - benchmarkX
//                t.setX(t.getX() - benchmarkX + 5);
//                t.setY(benchmarkY + benchmarkHeight);
//
//                tempNode = tempNode.getNextNode();
//            }
//
//            // characters below go to next line
//            while (tempNode != sentinel) {
//                Text t = tempNode.getContent();
//                t.setY(t.getY() + benchmarkHeight);
//                tempNode = tempNode.getNextNode();
//            }
//        } else {
//            // handle positions of rest characters in single line
//            Node temp = currentNode.next;
//            double addedWidth = newNode.getWidth();
//            while (temp != sentinel && temp.getContent().getText().charAt(0) != '\n') {
//                double originalX = temp.getContent().getX();
//                temp.getContent().setX(originalX + addedWidth);
//                //temp.getContent().setY(yPos);
//                temp = temp.next;
//            }
//        }
//    }

    public void addChar(String s, String fontName, int fontSize) {
        //TODO: bug in addChar when press BACK SPACE for a RETURN
        double xPos;
        double yPos;
        Text et = new Text(0, 0, "a");
        et.setTextOrigin(VPos.TOP);
        et.setFont(Font.font(fontName, fontSize));
        double benchmarkHeight = Math.round(et.getLayoutBounds().getHeight());
        // different currentContent, might be able to merge
        int line = (int) (currentContent().getY() / benchmarkHeight) + 1;

        if (s.charAt(0) == '\n') {
            // handle RETURN character
            // When reading a new file, place \n at the beginning of next line
            xPos = 5;
            yPos = currentNode.content.getY() + benchmarkHeight;
        } else {
            xPos = currentNode.content.getX() + currentNode.getWidth();
            yPos = currentNode.content.getY();
        }

        Text text = new Text(xPos, yPos, s);
        text.setTextOrigin(VPos.TOP);
        text.setFont(Font.font(fontName, fontSize));
        Node newNode = new Node(text);

        // add the new node into linked list
        newNode.next = currentNode.next;
        currentNode.next.prev = newNode;
        newNode.prev = currentNode;
        currentNode.next = newNode;

        currentNode = newNode;
        currentPos += 1;

        if (currentNode.getContent().getText().equals("\n")) {
            // currentNode is '\n', startNode/tempNode is the node right after cursor
            double benchmarkX;
            double benchmarkY;
            Node startNode = currentNode.getNextNode();
            if (startNode != sentinel) {
                // If it is not reading a new file
                benchmarkX = startNode.getContent().getX();
                benchmarkY = startNode.getContent().getY();

                Node tempNode = startNode;
                // characters after the cursor go to next line
                double wholeWordWidth = 0;
                while (tempNode != sentinel && tempNode.equals(snal.get(line))) {

                    Text t = tempNode.getContent();
                    // the difference between current text position and benchmark text is text.getX() - benchmarkX
                    t.setX(t.getX() - benchmarkX + 5);
                    t.setY(benchmarkY + benchmarkHeight);
                    wholeWordWidth += tempNode.getWidth();
                    tempNode = tempNode.getNextNode();
                }

                // benchmarkX is different
                while (tempNode != sentinel && tempNode.getContent().getText().charAt(0) != '\n') {
                    Text t = tempNode.getContent();
                    // the difference between current text position and benchmark text is text.getX() - benchmarkX
                    t.setX(t.getX() + wholeWordWidth);
//                    t.setY(benchmarkY + benchmarkHeight);

                    tempNode = tempNode.getNextNode();
                }

                // characters below go to next line
                while (tempNode != sentinel) {
                    Text t = tempNode.getContent();
                    t.setY(t.getY() + benchmarkHeight);
                    tempNode = tempNode.getNextNode();
                }
            } else {
                // When reading a new file, next node is sentinel, benchmarkY is should be different.
                benchmarkY = currentNode.getContent().getY() - benchmarkHeight;
            }

            snal.addNode(currentNode, line);
        } else {
            // handle positions of rest characters in single line
            Node temp = currentNode.next;
            double addedWidth = newNode.getWidth();

            // not equals to the beginning of next line
            int nextLine = (int) (currentContent().getY() / benchmarkHeight) + 1;
            while (temp != sentinel && !temp.equals(snal.get(nextLine))) {
                double originalX = temp.getContent().getX();
                temp.getContent().setX(originalX + addedWidth);
                //temp.getContent().setY(yPos);
                temp = temp.next;
            }
            //TODO: check word wrap here
            wordWrap(benchmarkHeight);
        }
    }

    /** Delete character before the cursor, which is currentNode */
    public String deleteChar(double benchmarkHeight) {
        if (currentNode == sentinel) {
            return null;
        }
        Node removedNode = currentNode;
        currentNode.prev.next = currentNode.next;
        currentNode.next.prev = currentNode.prev;

        if (currentContent().getText().equals("\n")) {
            double benchmarkX = currentContent().getX();
            double benchmarkY = currentContent().getY();
            double previousLineRightSide = currentNode.prev.getContent().getX() + currentNode.prev.getWidth();

            Node tempNode = currentNode.next;
            // characters after the cursor go to next line
            while (tempNode != sentinel && tempNode.getContent().getText().charAt(0) != '\n') {

                Text t = tempNode.getContent();
                // the difference between current text position and benchmark text is text.getX() - benchmarkX
                t.setX(t.getX() - benchmarkX + previousLineRightSide);
                t.setY(benchmarkY - benchmarkHeight);

                tempNode = tempNode.getNextNode();
            }

            // characters below go to next line
            while (tempNode != sentinel) {
                Text t = tempNode.getContent();
                t.setY(t.getY() - benchmarkHeight);
                tempNode = tempNode.getNextNode();
            }
        } else {
            // handle characters in a single line
            Node temp = currentNode.next;
            double reducedWidth = currentNode.getWidth();
            while (temp != sentinel && temp.getContent().getText().charAt(0) != '\n') {
                double originalX = temp.getContent().getX();
                temp.getContent().setX(originalX - reducedWidth);
                temp = temp.next;
            }
        }

        currentNode = currentNode.prev;
        currentPos -= 1;

        // WHY??? When I didn't set removedNode as null, I need to press BACK SPACE twice to remove a \n
        String removedContent = removedNode.getContent().getText();
        removedNode = null;

        return removedContent;
    }

    private void wordWrap(double benchmarkHeight) {
        // TODO: some place should add addNode function.
        // TODO: pass window width, height
        Node lastNodeOfCurrentLine;
        double windowWidth = 500;
        int nextLine = (int) (currentContent().getY() / benchmarkHeight) + 1;

        // Cascade word wrapping
        while (nextLine <= snal.getNumberOfLines()) {

            // Check if cursor is in the last line
            if (nextLine == snal.getNumberOfLines()) {
                lastNodeOfCurrentLine = sentinel.getPrevNode();
            } else {
                lastNodeOfCurrentLine =  snal.get(nextLine).getPrevNode();
            }

            Node node = lastNodeOfCurrentLine;
            if (lastNodeOfCurrentLine.getContent().getText().equals(" ")) {
                // If there are a lot of whitespaces at the end of line, get the last character that is not whitespace in the line.
                while (node.getContent().getText().equals(" ")) {
                    node = node.getPrevNode();
                }
            }

            double rightEdgeX = node.getContent().getX() + node.getWidth();

            if (rightEdgeX > windowWidth - 10) {
                while (!node.getContent().getText().equals(" ")) {
                    node = node.getPrevNode();
                }
                //TODO: node is space right now. Cursor position should be updated.
                Node startNode = node.getNextNode();
                node = startNode;
                double benchmarkX = startNode.getContent().getX();
                double benchmarkY = startNode.getContent().getY();

//                startNode.getContent().setX(5);
//                startNode.getContent().setY(benchmarkY + benchmarkHeight);

                // Whole word go to new line
                double wholeWordWidth = 0;
                while (node != sentinel && !node.equals(snal.get(nextLine))) {

                    Text t = node.getContent();
                    // the difference between current text position and benchmark text is text.getX() - benchmarkX
                    t.setX(t.getX() - benchmarkX + 5);
                    t.setY(benchmarkY + benchmarkHeight);
                    wholeWordWidth += node.getWidth();

                    node = node.getNextNode();
                }

                // Right go right
                // characters after the word go right
                while (node != sentinel && node.getContent().getText().charAt(0) != '\n') {

                    Text t = node.getContent();
                    // the difference between current text position and benchmark text is text.getX() - benchmarkX
                    t.setX(t.getX() + wholeWordWidth);
//                    t.setY(benchmarkY + benchmarkHeight);

                    node = node.getNextNode();
                }

                if (nextLine < snal.getNumberOfLines()) {
                    snal.replaceNode(nextLine, startNode);
                } else {
                    snal.addNode(startNode, nextLine);
                }
            }

            nextLine += 1;
        }

    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Node getSentinel() {
        return sentinel;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void startingPosition() {
        currentNode = sentinel;
        currentPos = 0;
    }

    public void moveLeft() {
        if (currentNode == sentinel) {
            return;
        }
        if (currentNode.getContent().getText().equals("\r")) {
            currentNode = currentNode.prev.prev;
            currentPos -= 2;
        }
        currentNode = currentNode.prev;
        currentPos -= 1;
    }

    public void moveRight() {
        // TODO: Four scenarios: 1 Move one step right 2 End of file no move 3 word wrap next line 4 \n next line
        if (currentNode.next == sentinel) {
            return;
        }
        if (currentNode.getContent().getText().equals("\r")) {
            currentNode = currentNode.next.next;
            currentPos += 2;
        }
        currentNode = currentNode.next;
        currentPos += 1;
    }

    public Text currentContent() {
        return currentNode.content;
    }

    public Text nextContent() {
        return currentNode.next.content;
    }

    public Node get(int pos) {
        return sentinel;
    }

    @Override
    public Iterator iterator() {
        return new newIterator();
    }

    private class newIterator implements Iterator{
        Node currentNode;
        public newIterator() {
            currentNode = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return (currentNode != sentinel);
        }

        @Override
        public Node next() {
            Node temp = currentNode;
            currentNode = currentNode.next;
            return temp;
        }
    }

    public class StartNodeArrayList {
        /**
         * firstPointer is empty space for next first element
         * lastPointer is empty space for next last element
         * e.g. [--*1343#-----]
         */
        Node[] items;
        int size;
        int firstPointer;
        int lastPointer;

        public StartNodeArrayList() {
            items = new Node[8];
            size = 0;
            firstPointer = 0;
            lastPointer = 1;
        }
        //TODO: replace node
        public void replaceNode(int index, Node n) {
            items[(firstPointer + index + 1) % items.length] = n;
        }

        public void addNode(Node n, int index){
            if (index > size) {
                return;
            }
            if (size == items.length) {
                resize(2 * items.length);
            }
            int realIndex = (firstPointer + index + 1) % items.length;
            if (index >=  size / 2) {
                // The second half elements move to one position afterwards
                // [9#-----*]
//                if (firstPointer < lastPointer && lastPointer < items.length - 1) {
                if (size - index < lastPointer) {
                    //  [----*123235#---] [1#-------*]
                    System.arraycopy(items, realIndex, items, realIndex + 1, size - index);
                    items[realIndex] = n;
                } else {
                    //  * first # last [#-------*13242]  [123#-----*312] [1#-------*]
                    System.arraycopy(items, 0, items, 1, lastPointer);
                    items[0] = items[items.length - 1];
                    //TODO: might be bug here
                    System.arraycopy(items, realIndex, items, realIndex + 1, size - index - lastPointer - 1);
                    items[realIndex] = n;
                }
                lastPointer = plusOne(lastPointer);
            } else {
                // The first half elements move to one position forwards
//                if (firstPointer < lastPointer && firstPointer > -1) {
                if (index < items.length - firstPointer - 1) {
                    // [----*13242#----] [123453#-----*312]
                    System.arraycopy(items, plusOne(firstPointer), items, firstPointer, index);
                    items[realIndex] = n;
                } else {
                    //  [13242---------]  [123453-------312]
                    // move elements before items[0], then move rest
                    System.arraycopy(items, plusOne(firstPointer), items, firstPointer, size - lastPointer);
                    items[items.length - 1] = items[0];
                    System.arraycopy(items, 1, items, 0, realIndex);
                    items[realIndex] = n;
                }
                firstPointer = minusOne(firstPointer);
            }
            size += 1;
        }

        public Node removeNode(int index) {
            if (index > size - 1) {
                return null;
            }
            if (size == items.length / 4) {
                resize(items.length / 2);
            }
            int realIndex = (firstPointer + index + 1) % items.length;
            Node temp = items[realIndex];
            if (index >=  size / 2) {
                // The second half elements move to one position forwards
                if (size - index < lastPointer) {
                    // [1432#-------*]
                    // lastPointer can be copied together, so don't need items[lastPointer] = null; That would cause a bug when the array is full
                    System.arraycopy(items, realIndex + 1, items, realIndex, size - index - 1);
                } else {
                    //  * first # last [#-------*13242]  [123#-----*312] [1#-------*]
                    System.arraycopy(items, realIndex + 1, items, realIndex, size - index - lastPointer - 1);
                    items[items.length - 1] = items[0];
                    System.arraycopy(items, 1, items, 0, lastPointer);
                }
                lastPointer = minusOne(lastPointer);
                items[lastPointer] = null;
            } else {
                // Three scenarios: 1. [----*13242#----]  2. [4323453#-----*312] remove 1  3. [45123453#-----*3342] remove 1
                // 1.2 has same situation, 3 has another situation
                if (realIndex > firstPointer) {
                    // [----*13242#----] [123453#-----*312]
                    System.arraycopy(items, plusOne(firstPointer), items, plusOne(plusOne(firstPointer)), index);
                } else {
                    //  [13242---------]  [123453-------312]
                    // move elements after items[0], then move rest
                    System.arraycopy(items, 0, items, 1, realIndex);
                    items[0] = items[items.length - 1];
                    System.arraycopy(items, plusOne(firstPointer), items, plusOne(plusOne(firstPointer)), Math.max(size - lastPointer - 1, 0));
                }
                firstPointer = plusOne(firstPointer);
                items[firstPointer] = null;
            }

            size -= 1;
            return temp;
        }

        public Node get(int index) {
            return items[(firstPointer + index + 1) % items.length];
        }

        private void resize(int capacity){
            if (capacity > 7) {
                Node[] a = new Node[capacity];

                if (lastPointer == plusOne(firstPointer)) {
                    /* Expand the arraylist */
                    System.arraycopy(items, plusOne(firstPointer), a, 1, items.length - plusOne(firstPointer));
                    System.arraycopy(items, 0, a, items.length - plusOne(firstPointer) + 1, lastPointer);
                } else {
                    /* Shrink the arraylist */
                    if (firstPointer > lastPointer) {
                        System.arraycopy(items, plusOne(firstPointer), a, 1, items.length - firstPointer - 1);
                        System.arraycopy(items, 0, a, items.length - firstPointer, lastPointer);
                    } else {
                        /* firstPointer < lastPointer */
                        System.arraycopy(items, firstPointer + 1, a, 1, size);
                    }
                }
                firstPointer = 0;
                lastPointer = size + 1;
                items = a;
            }
        }

        public int getNumberOfLines() {
            return size;
        }

        private int plusOne(int index) {
            return (index + 1) % items.length;
        }

        private int minusOne(int index) {
            if (index == 0) {
                return items.length - 1;
            }
            return index - 1;
        }
    }

    public class Node {
        private Text content;
        private Node prev;
        private Node next;

        public Node() {
            this.content = new Text(0,0,"");
            this.content.setTextOrigin(VPos.TOP);
            this.prev = null;
            this.next = null;
        }

        public Node(String s) {
            Text characterTyped = new Text(0, 0, s);
            characterTyped.setTextOrigin(VPos.TOP); // needed or not?
            this.content = characterTyped;
            this.prev = null;
            this.next = null;
        }

        public Node(String s, Node p, Node n) {
            Text characterTyped = new Text(0, 0, s);
            characterTyped.setTextOrigin(VPos.TOP); // needed or not?
            this.content = characterTyped;
            this.prev = p;
            this.next = n;
        }

        public Node(Text t, Node p, Node n) {
            this.content = t;
            this.prev = p;
            this.next = n;
        }

        public Node(Text t) {
            this.content = t;
            this.prev = null;
            this.next = null;
        }

        public Text getContent() {
            return content;
        }

        public Node getPrevNode() {
            return this.prev;
        }

        public Node getNextNode() {
            return this.next;
        }

        public double getXwhenResizing() {
            return this.prev.content.getX() + this.prev.getWidth();
        }

        public double getWidth() {
            return Math.round(content.getLayoutBounds().getWidth());
        }

        public double getHeight() {
            return Math.round(content.getLayoutBounds().getHeight());
        }
    }

//    @Test
    public void testArrayList() {
        StartNodeArrayList snal = new StartNodeArrayList();
        Node n1 = new Node("1");Node n2 = new Node("2");Node n3 = new Node("3");Node n4 = new Node("4");Node n5 = new Node("5");Node n6 = new Node();
        snal.addNode(n1, 0);
        snal.addNode(n2, 1);
        snal.addNode(n3, 2);
        Node n0 = snal.get(0);
        snal.addNode(n4, 3);
        snal.addNode(n5, 0);
        snal.addNode(n2, 0);
        snal.addNode(n3, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        snal.addNode(n4, 0);
        n6 = snal.removeNode(10);
        n6 = snal.removeNode(8);
        n6 = snal.removeNode(5);
        n6 = snal.removeNode(3);
        n6 = snal.removeNode(2);
        n6 = snal.removeNode(2);
        n6 = snal.removeNode(2);
        n6 = snal.removeNode(2);
        n6 = snal.removeNode(0);
        n6 = snal.removeNode(0);
        n6 = snal.removeNode(0);
        n6 = snal.removeNode(0);
        n6 = snal.removeNode(0);
        n6 = snal.removeNode(0);
        n6 = snal.removeNode(0);
    }
}
