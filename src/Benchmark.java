import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Benchmark {

    private static final int[] N_VALUES = {100, 1_000, 10_000, 100_000};
    private static final int REPEATS = 5;
    private static final long SEED = 42;
    private static final String OUT_DIR = "results/tables/";

    public static void main(String[] args) throws IOException {
        new java.io.File(OUT_DIR).mkdirs();
        workload1RandomAccess();
        workload2Search();
        workload3InsertRemove();
        workload4Heap();
        System.out.println("All benchmarks complete. CSV files written to " + OUT_DIR);
    }
    private static void workload1RandomAccess() throws IOException {
        System.out.println("Running Workload 1 (Random Access)...");
        try (PrintWriter out = new PrintWriter(new FileWriter(OUT_DIR + "workload1_random_access.csv"))) {
            out.println("structure,n,avg_time_ns,accesses");
            for (int n : N_VALUES) {
                Random rnd = new Random(SEED);
                Integer[] input = randomArray(rnd, n);
                int[] indices = randomIndices(rnd, n, 10_000);

                long total = 0;
                for (int r = 0; r < REPEATS; r++) {
                    DynamicArray<Integer> da = new DynamicArray<>();
                    for (Integer v : input) da.add(v);
                    long start = System.nanoTime();
                    for (int idx : indices) {
                        da.get(idx);
                    }
                    total += System.nanoTime() - start;
                }
                out.println("DynamicArray," + n + "," + (total / REPEATS) + "," + 10_000);

                total = 0;
                for (int r = 0; r < REPEATS; r++) {
                    MyLinkedList<Integer> ll = new MyLinkedList<>();
                    for (Integer v : input) ll.add(v);
                    long start = System.nanoTime();
                    for (int idx : indices) {
                        ll.get(idx);
                    }
                    total += System.nanoTime() - start;
                }
                out.println("LinkedList," + n + "," + (total / REPEATS) + "," + 10_000);
            }
        }
    }

    private static void workload2Search() throws IOException {
        System.out.println("Running Workload 2 (Search)...");
        try (PrintWriter out = new PrintWriter(new FileWriter(OUT_DIR + "workload2_search.csv"))) {
            out.println("structure,n,avg_time_ns,avg_comparisons");
            for (int n : N_VALUES) {
                Random rnd = new Random(SEED);
                Integer[] input = randomArray(rnd, n);

                int[] queries = new int[1000];
                for (int i = 0; i < 1000; i++) {
                    queries[i] = rnd.nextInt(2_000_000_000) - 1_000_000_000;
                }

                long total = 0;
                long totalComparisons = 0;
                for (int r = 0; r < REPEATS; r++) {
                    DynamicArray<Integer> da = new DynamicArray<>();
                    for (Integer v : input) da.add(v);
                    long start = System.nanoTime();
                    long comparisons = 0;
                    for (int q : queries) {
                        comparisons += da.containsWithComparisons(q);
                    }
                    total += System.nanoTime() - start;
                    totalComparisons += comparisons;
                }
                out.println("DynamicArray," + n + "," + (total / REPEATS) + "," + (totalComparisons / REPEATS));

                total = 0;
                totalComparisons = 0;
                for (int r = 0; r < REPEATS; r++) {
                    MyLinkedList<Integer> ll = new MyLinkedList<>();
                    for (Integer v : input) ll.add(v);
                    long start = System.nanoTime();
                    long comparisons = 0;
                    for (int q : queries) {
                        comparisons += ll.containsWithComparisons(q);
                    }
                    total += System.nanoTime() - start;
                    totalComparisons += comparisons;
                }
                out.println("LinkedList," + n + "," + (total / REPEATS) + "," + (totalComparisons / REPEATS));
            }
        }
    }


    private static void workload3InsertRemove() throws IOException {
        System.out.println("Running Workload 3 (Insertion/Removal)...");
        try (PrintWriter out = new PrintWriter(new FileWriter(OUT_DIR + "workload3_insert_remove.csv"))) {
            out.println("structure,n,position,operation,avg_time_ns,avg_element_moves");
            for (int n : N_VALUES) {
                Random rnd = new Random(SEED);
                Integer[] input = randomArray(rnd, n);

                runInsertRemove(out, "DynamicArray", n, 0, input, true);
                runInsertRemove(out, "DynamicArray", n, n / 2, input, true);
                runInsertRemove(out, "LinkedList", n, 0, input, false);
                runInsertRemove(out, "LinkedList", n, n / 2, input, false);
            }
        }
    }

    private static void runInsertRemove(PrintWriter out, String structureName, int n,
                                         int index, Integer[] input, boolean isArray) {
        Random rnd = new Random(SEED + 1);
        String posLabel = (index == 0) ? "begin" : "middle";
        long total = 0;
        long totalMoves = 0;
        for (int r = 0; r < REPEATS; r++) {
            Object structure = buildStructure(isArray, input);
            long start = System.nanoTime();
            long moves = 0;
            for (int i = 0; i < 1000; i++) {
                int val = rnd.nextInt(1_000_000);
                insertAt(structure, isArray, index, val);

                moves += movesForInsert(isArray, sizeOf(structure, isArray), index);
            }
            total += System.nanoTime() - start;
            totalMoves += moves;
        }
        out.println(structureName + "," + n + "," + posLabel + ",insert," + (total / REPEATS) + "," + (totalMoves / REPEATS));

        total = 0;
        totalMoves = 0;
        for (int r = 0; r < REPEATS; r++) {
            Object structure = buildStructure(isArray, input);
            long start = System.nanoTime();
            long moves = 0;
            int curSize = n;
            for (int i = 0; i < 1000 && curSize > 0; i++) {
                int removeIdx = Math.min(index, curSize - 1);
                removeAt(structure, isArray, removeIdx);
                moves += movesForInsert(isArray, curSize, removeIdx);
                curSize--;
            }
            total += System.nanoTime() - start;
            totalMoves += moves;
        }
        out.println(structureName + "," + n + "," + posLabel + ",remove," + (total / REPEATS) + "," + (totalMoves / REPEATS));
    }

    @SuppressWarnings("unchecked")
    private static Object buildStructure(boolean isArray, Integer[] input) {
        if (isArray) {
            DynamicArray<Integer> da = new DynamicArray<>();
            for (Integer v : input) da.add(v);
            return da;
        } else {
            MyLinkedList<Integer> ll = new MyLinkedList<>();
            for (Integer v : input) ll.add(v);
            return ll;
        }
    }

    @SuppressWarnings("unchecked")
    private static void insertAt(Object structure, boolean isArray, int index, int val) {
        if (isArray) ((DynamicArray<Integer>) structure).add(index, val);
        else ((MyLinkedList<Integer>) structure).add(index, val);
    }

    @SuppressWarnings("unchecked")
    private static void removeAt(Object structure, boolean isArray, int index) {
        if (isArray) ((DynamicArray<Integer>) structure).remove(index);
        else ((MyLinkedList<Integer>) structure).remove(index);
    }

    @SuppressWarnings("unchecked")
    private static int sizeOf(Object structure, boolean isArray) {
        if (isArray) return ((DynamicArray<Integer>) structure).size();
        else return ((MyLinkedList<Integer>) structure).size();
    }

    private static long movesForInsert(boolean isArray, int currentSize, int index) {
        if (isArray) {
            return Math.max(0, currentSize - index); // elements shifted
        } else {
            return index; // predecessor hops from head
        }
    }

    private static void workload4Heap() throws IOException {
        System.out.println("Running Workload 4 (Priority Processing)...");
        try (PrintWriter out = new PrintWriter(new FileWriter(OUT_DIR + "workload4_heap.csv"))) {
            out.println("n,avg_insert_time_ns,avg_extract_time_ns,avg_comparisons,order_correct");
            for (int n : N_VALUES) {
                Random rnd = new Random(SEED);
                Integer[] input = randomArray(rnd, n);

                long totalInsertTime = 0;
                long totalExtractTime = 0;
                long totalComparisons = 0;
                boolean orderCorrect = true;

                for (int r = 0; r < REPEATS; r++) {
                    MinHeap<Integer> heap = new MinHeap<>();
                    heap.resetComparisonCount();

                    long startInsert = System.nanoTime();
                    for (Integer v : input) heap.insert(v);
                    totalInsertTime += System.nanoTime() - startInsert;

                    long startExtract = System.nanoTime();
                    int prev = Integer.MIN_VALUE;
                    for (int i = 0; i < n; i++) {
                        int cur = heap.extractMin();
                        if (cur < prev) orderCorrect = false;
                        prev = cur;
                    }
                    totalExtractTime += System.nanoTime() - startExtract;
                    totalComparisons += heap.getComparisonCount();
                }
                out.println(n + "," + (totalInsertTime / REPEATS) + "," + (totalExtractTime / REPEATS)
                        + "," + (totalComparisons / REPEATS) + "," + orderCorrect);
            }
        }
    }

    private static Integer[] randomArray(Random rnd, int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(1_000_000_000);
        return arr;
    }

    private static int[] randomIndices(Random rnd, int n, int count) {
        int[] idx = new int[count];
        for (int i = 0; i < count; i++) idx[i] = rnd.nextInt(n);
        return idx;
    }
}
