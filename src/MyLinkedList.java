/**
 * MyLinkedList.java
 *
 * A minimal singly-linked-list-with-tail-pointer implementation, written
 * from scratch for Assignment 2. (Named MyLinkedList instead of LinkedList
 * to avoid clashing with java.util.LinkedList, which is still available
 * for validation purposes elsewhere in the project.)
 */
public class MyLinkedList<T> {

    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    private void checkIndexForAccess(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkIndexForInsert(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    // ------------------------------------------------------------------
    // Required operations
    // ------------------------------------------------------------------

    /** Append x at the tail. O(1) thanks to the tail pointer. */
    public void add(T x) {
        Node<T> node = new Node<>(x);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /**
     * Insert x before the node currently at position `index`.
     * O(1) if index == 0 or index == size (tail append),
     * O(n) otherwise, because we must walk from head to find the
     * predecessor node.
     */
    public void add(int index, T x) {
        checkIndexForInsert(index);
        if (index == size) {
            add(x);
            return;
        }
        if (index == 0) {
            Node<T> node = new Node<>(x);
            node.next = head;
            head = node;
            if (tail == null) tail = node;
            size++;
            return;
        }

        // Loop invariant (walk to predecessor of position `index`):
        // Before each iteration, `prev` points to the node originally
        // at position (i-1) in the list, for the current loop counter i.
        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        Node<T> node = new Node<>(x);
        node.next = prev.next;
        prev.next = node;
        size++;
    }

    /**
     * Remove and return the element at the given index.
     *
     * This is the operation used for the "removal" loop-invariant proof
     * (searching for the predecessor node).
     * O(1) if index == 0, O(n) otherwise.
     */
    public T remove(int index) {
        checkIndexForAccess(index);
        T removed;
        if (index == 0) {
            removed = head.value;
            head = head.next;
            if (head == null) tail = null;
            size--;
            return removed;
        }

        // Loop invariant (see report, Section 4):
        // Before each iteration, `prev` references the node that was
        // originally at position (i-1); after the loop terminates,
        // prev references the node at position (index-1), i.e. the
        // predecessor of the node to be removed.
        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        Node<T> target = prev.next;
        removed = target.value;
        prev.next = target.next;
        if (target == tail) tail = prev;
        size--;
        return removed;
    }

    /**
     * Sequential access. O(1) best case (index == 0), O(n) average/worst
     * because the list must be walked from the head.
     */
    public T get(int index) {
        checkIndexForAccess(index);
        Node<T> cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.value;
    }

    /** Linear search. O(n) worst/average, O(1) best case. */
    public boolean contains(T x) {
        Node<T> cur = head;
        while (cur != null) {
            if (equalsHelper(cur.value, x)) return true;
            cur = cur.next;
        }
        return false;
    }

    /** Same as contains(), but also returns the number of comparisons made. */
    public int containsWithComparisons(T x) {
        int comparisons = 0;
        Node<T> cur = head;
        while (cur != null) {
            comparisons++;
            if (equalsHelper(cur.value, x)) return comparisons;
            cur = cur.next;
        }
        return comparisons;
    }

    private boolean equalsHelper(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> cur = head;
        while (cur != null) {
            sb.append(cur.value);
            if (cur.next != null) sb.append(", ");
            cur = cur.next;
        }
        return sb.append("]").toString();
    }
}
