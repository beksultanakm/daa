public class DynamicArray<T> {

    private Object[] data;
    private int size;   ]
    private static final int DEFAULT_CAPACITY = 10;

    public DynamicArray() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0");
        }
        data = new Object[Math.max(initialCapacity, 1)];
        size = 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) {
            return;
        }
        int newCapacity = data.length * 2;
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
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

    public void add(T x) {
        ensureCapacity(size + 1);
        data[size] = x;
        size++;
    }

    public void add(int index, T x) {
        checkIndexForInsert(index);
        ensureCapacity(size + 1);

        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = x;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndexForAccess(index);
        T removed = (T) data[index];

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
        return removed;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndexForAccess(index);
        return (T) data[index];
    }

    public boolean contains(T x) {
        for (int i = 0; i < size; i++) {
            if (equalsHelper(data[i], x)) {
                return true;
            }
        }
        return false;
    }

    public int containsWithComparisons(T x) {
        int comparisons = 0;
        for (int i = 0; i < size; i++) {
            comparisons++;
            if (equalsHelper(data[i], x)) {
                return comparisons;
            }
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

    public int capacity() {
        return data.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
