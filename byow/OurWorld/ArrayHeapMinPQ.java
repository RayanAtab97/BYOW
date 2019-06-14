package byow.OurWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * @author Matteo Ciccozzi
 */


public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    /**
     * We organize elements in a heap
     * where the priority is dictated by a
     * double value "priority"
     */
    private int CAP = 1000000; //initial capacity
    private int size;
    private ArrayList<Node> heapTree = new ArrayList<>(CAP);
    private HashMap<T, Integer> itemsToIndex;


    public ArrayHeapMinPQ() {
        heapTree.add(new Node(null, -1));
        itemsToIndex = new HashMap<>();
        size = 0;
    }

    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        }
        heapTree.add(null);
        heapTree.set(size + 1, new Node(item, priority));
        size += 1;
        itemsToIndex.put(item, size);
        swimUp(size);


    }

    /* Returns true if the PQ contains the given item. */
    public boolean contains(T item) {
        return itemsToIndex.containsKey(item);
    }

    /* Returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T getSmallest() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return heapTree.get(1).getItem();
    }

    /* Removes and returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T removeSmallest() {
        T item = getSmallest();
        itemsToIndex.remove(item);
        Node promotedNode = heapTree.get(size); // new temporary smallest
        heapTree.set(size, null);
        heapTree.set(1, promotedNode);
        size -= 1;
        swimDown(1);
        return item;
    }

    /**
     * Returns the number of items in the PQ.
     * it returns size -1 because size accounts
     * for dummy node too.
     */
    public int size() {
        return size;
    }

    public double getPriority(T item) {
        int k = getIndex(item);
        return heapTree.get(k).getPriority();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /* Changes the priority of the given item. Throws NoSuchElementException if the item
     * doesn't exist. */
    public void changePriority(T item, double priority) {
        if (!itemsToIndex.containsKey(item)) {
            throw new NoSuchElementException();
        }
        int k = getIndex(item);
        double oldPriority = heapTree.get(k).priority;
        heapTree.get(k).setPriority(priority);
        if (oldPriority == priority) {
            return;
        } else if (oldPriority - priority > 0) {
            swimUp(k);
        } else {
            swimDown(k);
        }
    }

    /* Changes the position of elements based on the priority
     */
    private void swimUp(int key) {
        if (key / 2 < 1) {
            return;
        }
        int parentKey = key / 2;
        Node current = heapTree.get(key);
        Node parent = heapTree.get(parentKey);

        if (parent.compareTo(current) > 0) {
            swap(key, parentKey);
            itemsToIndex.replace(current.getItem(), parentKey);
            itemsToIndex.replace(parent.getItem(), key);
            swimUp(key / 2);
        }

    }

    private void swimDown(int key) {
        if (key * 2 > size) {
            return;
        }

        int minChildKey = swimDownHelper(2 * key, 2 * key + 1);
        Node current = heapTree.get(key);
        Node minChild = heapTree.get(minChildKey);
        if (minChild == null) {
            return;
        }
        if (minChild.compareTo(current) < 0) {
            swap(key, minChildKey);
            itemsToIndex.replace(current.getItem(), minChildKey);
            itemsToIndex.replace(minChild.getItem(), key);
            swimDown(minChildKey);
        }

    }

    /* returns the key of the child with smallest priority
     * between left and right child
     */
    private int swimDownHelper(int leftChildKey, int rightChildKey) {
        if (rightChildKey > size) {
            return leftChildKey;
        }
        Node left = heapTree.get(leftChildKey);
        Node right = heapTree.get(rightChildKey);


        if (left.compareTo(right) <= 0) {
            return leftChildKey;
        } else {
            return rightChildKey;
        }
    }

    /* swaps the node at key with the node
     * at targetKey
     */
    private void swap(int key, int targetKey) {
        Node temp = heapTree.get(key);
        Node targetTemp = heapTree.get(targetKey);

        heapTree.set(targetKey, temp);
        heapTree.set(key, targetTemp);
    }

    /* Returns the index of the given element in the tree
     */
    private int getIndex(T item) {
        return itemsToIndex.get(item);
    }

    /**
     * @source NaiveMinPQ
     */
    private class Node implements Comparable<Node> {
        private T item;
        private double priority;

        Node(T e, double p) {
            this.item = e;
            this.priority = p;
        }

        T getItem() {
            return item;
        }

        double getPriority() {
            return priority;
        }

        void setPriority(double priority) {
            this.priority = priority;
        }

        @Override
        public int compareTo(Node other) {
            if (other == null) {
                return -1;
            }
            return Double.compare(this.getPriority(), other.getPriority());
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            } else {
                return ((Node) o).getItem().equals(getItem());
            }
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }
    }

}
