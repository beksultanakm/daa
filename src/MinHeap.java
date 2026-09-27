/**
 * MinHeap.java
 *
 * A binary min-heap backed by a dynamically resizing array, written from
 * scratch for Assignment 2. Standard 0-indexed layout:
 *   parent(i) = (i-1)/2
 *   left(i)   = 2*i + 1
 *   right(i)  = 2*i + 2
 */
public class MinHeap<T extends Comparable<T>> {

    private Object[] data;
    private int size;
    private long comparisonCount; // running total, used by the benchmark
    private static final int DEFAULT_CAPACITY = 16;

    public MinHeap() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
        comparisonCount = 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) return;
        int newCapacity = data.length * 2;
        if (newCapacity < minCapacity) newCapacity = minCapacity;
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) newData[i] = data[i];
        data = newData;
    }

    @SuppressWarnings("unchecked")
    private T at(int i) {
        return (T) data[i];
    }

    private int compare(T a, T b) {
        comparisonCount++;
        return a.compareTo(b);
    }

    private void swap(int i, int j) {
        Object tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    // ------------------------------------------------------------------
    // Required operations
    // ------------------------------------------------------------------

    /**
     * Insert x, then restore the heap property by "bubbling up".
     *
     * This is the operation used for the "heap insertion" loop-invariant
     * proof. O(log n) worst/average case, O(1) best case (new element is
     * already >= its parent, no swaps needed).
     */
    public void insert(T x) {
        ensureCapacity(size + 1);
        data[size] = x;
        int i = size;
        size++;

        // Loop invariant (see report, Section 4):
        // Before each iteration, the subtree rooted at every index except
        // possibly index i satisfies the min-heap property; i is the only
        // position that might still violate it with respect to its parent.
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (compare(at(i), at(parent)) < 0) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    /** Look at the minimum element without removing it. O(1). */
    public T peekMin() {
        if (size == 0) {
            throw new java.util.NoSuchElementException("Heap is empty");
        }
        return at(0);
    }

    /**
     * Remove and return the minimum element, then restore the heap
     * property by "bubbling down" (sift-down) the element moved to the
     * root. O(log n) worst/average case, O(1) best case (heap becomes
     * empty or has just one element left).
     */
    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) {
            throw new java.util.NoSuchElementException("Heap is empty");
        }
        T min = at(0);
        size--;
        data[0] = data[size];
        data[size] = null;

        int i = 0;
        // Loop invariant: before each iteration, every subtree of the
        // heap except possibly the one rooted at i satisfies the
        // min-heap property.
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size && compare(at(left), at(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && compare(at(right), at(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == i) break;

            swap(i, smallest);
            i = smallest;
        }
        return min;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public long getComparisonCount() {
        return comparisonCount;
    }

    public void resetComparisonCount() {
        comparisonCount = 0;
    }

    /** Utility for testing: verifies the min-heap property holds everywhere. */
    public boolean isValidHeap() {
        for (int i = 1; i < size; i++) {
            int parent = (i - 1) / 2;
            if (at(i).compareTo(at(parent)) < 0) {
                return false;
            }
        }
        return true;
    }
}
