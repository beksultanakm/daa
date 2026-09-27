import java.util.Random;

/**
 * Tests.java
 *
 * Lightweight, dependency-free correctness tests for DynamicArray,
 * MyLinkedList and MinHeap (Assignment 2, Section 10).
 *
 * No JUnit is used so the file can be compiled and run with nothing but
 * a plain JDK: `javac *.java && java Tests`
 */
public class Tests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testDynamicArray();
        testLinkedList();
        testMinHeap();
        testAgainstJavaCollections();

        System.out.println();
        System.out.println("=========================================");
        System.out.println("Passed: " + passed + "   Failed: " + failed);
        System.out.println("=========================================");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void expectException(String name, Runnable r) {
        try {
            r.run();
            failed++;
            System.out.println("[FAIL] " + name + " (expected exception, none thrown)");
        } catch (IndexOutOfBoundsException | java.util.NoSuchElementException e) {
            passed++;
            System.out.println("[PASS] " + name);
        }
    }

    // ------------------------------------------------------------------
    // DynamicArray
    // ------------------------------------------------------------------
    private static void testDynamicArray() {
        System.out.println("--- DynamicArray ---");
        DynamicArray<Integer> a = new DynamicArray<>();

        // empty structure
        check("empty size == 0", a.size() == 0);
        check("empty isEmpty()", a.isEmpty());
        check("empty contains() == false", !a.contains(5));
        expectException("empty get(0) throws", () -> a.get(0));
        expectException("empty remove(0) throws", () -> a.remove(0));

        // one element
        a.add(10);
        check("one element size == 1", a.size() == 1);
        check("one element get(0) == 10", a.get(0) == 10);
        check("one element contains(10)", a.contains(10));

        // multiple elements
        a.add(20);
        a.add(30);
        a.add(0, 5); // [5, 10, 20, 30]
        check("multi elements after add(index,x)", a.toString().equals("[5, 10, 20, 30]"));

        // duplicate values
        a.add(10);
        check("duplicate values contains still true", a.contains(10));
        check("size counts duplicates", a.size() == 5);

        // boundary indices
        a.add(a.size(), 99); // append at size == valid boundary
        check("insert at index == size (append)", a.get(a.size() - 1) == 99);
        expectException("insert at index == size+1 throws", () -> a.add(a.size() + 1, 1));
        expectException("get(-1) throws", () -> a.get(-1));
        expectException("get(size) throws", () -> a.get(a.size()));

        // removal correctness
        int removed = a.remove(0);
        check("remove(0) returns correct value", removed == 5);
        check("size decreases after remove", a.size() == 5);

        // large input + comparison to expected content
        DynamicArray<Integer> big = new DynamicArray<>();
        for (int i = 0; i < 100000; i++) big.add(i);
        check("large input size", big.size() == 100000);
        check("large input get(50000)", big.get(50000) == 50000);
        check("large input contains(99999)", big.contains(99999));
        check("large input !contains(100000)", !big.contains(100000));
    }

    // ------------------------------------------------------------------
    // MyLinkedList
    // ------------------------------------------------------------------
    private static void testLinkedList() {
        System.out.println("--- MyLinkedList ---");
        MyLinkedList<Integer> l = new MyLinkedList<>();

        check("empty size == 0", l.size() == 0);
        check("empty isEmpty()", l.isEmpty());
        expectException("empty get(0) throws", () -> l.get(0));
        expectException("empty remove(0) throws", () -> l.remove(0));

        l.add(10);
        check("one element get(0) == 10", l.get(0) == 10);

        l.add(20);
        l.add(30);
        l.add(0, 5); // [5, 10, 20, 30]
        check("multi elements after add(index,x)", l.toString().equals("[5, 10, 20, 30]"));

        l.add(10);
        check("duplicate values contains still true", l.contains(10));

        l.add(l.size(), 99);
        check("insert at index == size (append)", l.get(l.size() - 1) == 99);
        expectException("insert at index == size+1 throws", () -> l.add(l.size() + 1, 1));
        expectException("get(-1) throws", () -> l.get(-1));

        int removed = l.remove(0);
        check("remove(0) returns correct value", removed == 5);

        // remove tail, check tail pointer stays consistent
        MyLinkedList<Integer> tailTest = new MyLinkedList<>();
        tailTest.add(1);
        tailTest.add(2);
        tailTest.add(3);
        tailTest.remove(2); // remove tail element
        tailTest.add(4);    // append should still work correctly
        check("tail pointer consistent after removing tail", tailTest.toString().equals("[1, 2, 4]"));

        MyLinkedList<Integer> big = new MyLinkedList<>();
        for (int i = 0; i < 100000; i++) big.add(i);
        check("large input size", big.size() == 100000);
        check("large input get(50000)", big.get(50000) == 50000);
        check("large input contains(99999)", big.contains(99999));
    }

    // ------------------------------------------------------------------
    // MinHeap
    // ------------------------------------------------------------------
    private static void testMinHeap() {
        System.out.println("--- MinHeap ---");
        MinHeap<Integer> h = new MinHeap<>();

        check("empty size == 0", h.isEmpty());
        expectException("empty peekMin throws", () -> h.peekMin());
        expectException("empty extractMin throws", () -> h.extractMin());

        h.insert(10);
        check("one element peekMin == 10", h.peekMin() == 10);
        check("one element isValidHeap", h.isValidHeap());

        h.insert(5);
        h.insert(20);
        h.insert(1);
        h.insert(15);
        check("heap property maintained after inserts", h.isValidHeap());
        check("peekMin returns global min", h.peekMin() == 1);

        // duplicates
        h.insert(1);
        check("duplicate min handled", h.peekMin() == 1);

        // extraction order must be non-decreasing
        Random rnd = new Random(42);
        MinHeap<Integer> h2 = new MinHeap<>();
        int n = 2000;
        for (int i = 0; i < n; i++) {
            h2.insert(rnd.nextInt(1_000_000));
        }
        int prev = Integer.MIN_VALUE;
        boolean nonDecreasing = true;
        boolean validAfterEachExtract = true;
        for (int i = 0; i < n; i++) {
            if (!h2.isValidHeap()) validAfterEachExtract = false;
            int cur = h2.extractMin();
            if (cur < prev) nonDecreasing = false;
            prev = cur;
        }
        check("extractMin returns non-decreasing sequence (n=2000)", nonDecreasing);
        check("heap property maintained after every extraction", validAfterEachExtract);
        check("heap empty after extracting all elements", h2.isEmpty());
        expectException("extractMin on now-empty heap throws", () -> h2.extractMin());

        // large input
        MinHeap<Integer> big = new MinHeap<>();
        for (int i = 100000; i > 0; i--) big.insert(i);
        check("large input min after inserts", big.peekMin() == 1);
        check("large input isValidHeap", big.isValidHeap());
    }

    // ------------------------------------------------------------------
    // Cross-check against java.util collections
    // ------------------------------------------------------------------
    private static void testAgainstJavaCollections() {
        System.out.println("--- Cross-check vs java.util collections ---");
        Random rnd = new Random(42);

        // DynamicArray vs ArrayList
        DynamicArray<Integer> da = new DynamicArray<>();
        java.util.ArrayList<Integer> al = new java.util.ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            int op = rnd.nextInt(3);
            int val = rnd.nextInt(10000);
            if (op == 0 || al.isEmpty()) {
                da.add(val); al.add(val);
            } else if (op == 1) {
                int idx = rnd.nextInt(al.size());
                da.remove(idx); al.remove(idx);
            } else {
                int idx = rnd.nextInt(al.size());
                da.add(idx, val); al.add(idx, val);
            }
        }
        boolean daMatches = da.size() == al.size();
        if (daMatches) {
            for (int i = 0; i < al.size(); i++) {
                if (!da.get(i).equals(al.get(i))) { daMatches = false; break; }
            }
        }
        check("DynamicArray matches ArrayList after random ops", daMatches);

        // MinHeap vs PriorityQueue
        MinHeap<Integer> mh = new MinHeap<>();
        java.util.PriorityQueue<Integer> pq = new java.util.PriorityQueue<>();
        for (int i = 0; i < 5000; i++) {
            int v = rnd.nextInt(100000);
            mh.insert(v);
            pq.add(v);
        }
        boolean heapMatches = true;
        for (int i = 0; i < 5000; i++) {
            if (!mh.extractMin().equals(pq.poll())) { heapMatches = false; break; }
        }
        check("MinHeap extraction order matches PriorityQueue", heapMatches);
    }
}
