/*************************************************************************
 *  Compilation:  javac IndexMinPQ.java
 *  Execution:    java IndexMinPQ
 *
 *  Indexed PQ implementation using a binary heap.
 *
 *********************************************************************/

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int N;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Key[] keys;      // keys[i] = priority of i

    public IndexMinPQ(int NMAX) {
        keys = (Key[]) new Comparable[NMAX + 1];    // make this of length NMAX??
        pq   = new int[NMAX + 1];
        qp   = new int[NMAX + 1];                   // make this of length NMAX??
        for (int i = 0; i <= NMAX; i++) qp[i] = -1;
    }

    // is the priority queue empty?
    public boolean isEmpty() { return N == 0; }

    // is k an index on the priority queue?
    public boolean contains(int k) {
        return qp[k] != -1;
    }

    // number of keys in the priority queue
    public int size() {
        return N;
    }

    // associate key with index k
    public void insert(int k, Key key) {
        if (contains(k)) throw new RuntimeException("item is already in pq");
        N++;
        qp[k] = N;
        pq[N] = k;
        keys[k] = key;
        swim(N);
    }

    // return the index associated with a minimal key
    public int min() { 
        if (N == 0) throw new RuntimeException("Priority queue underflow");
        return pq[1];        
    }

    // return a minimal key
    public Key minKey() { 
        if (N == 0) throw new RuntimeException("Priority queue underflow");
        return keys[pq[1]];        
    }

    // delete a minimal key and returns its associated index
    public int delMin() { 
        if (N == 0) throw new RuntimeException("Priority queue underflow");
        int min = pq[1];        
        exch(1, N--); 
        sink(1);
        qp[min] = -1;            // delete
        keys[pq[N+1]] = null;    // to help with garbage collection
        pq[N+1] = -1;            // not needed
        return min; 
    }

/*
    // change key associated with index k; insert if index k is not present
    public void put(int k, Key key) {
        if (!contains(k)) insert(k, key);
        else changeKey(k, key);
    }

    // return key associated with index k
    public Key get(int k) {
        if (!contains(k)) throw new RuntimeException("item is not in pq");
        else return keys[pq[k]];
    }
*/

    // change the key associated with index k
    public void change(int k, Key key) {
        if (!contains(k)) throw new RuntimeException("item is not in pq");
        keys[k] = key;
        swim(qp[k]);
        sink(qp[k]);
    }

    // decrease the key associated with index k
    public void decrease(int k, Key key) {
        if (!contains(k)) throw new RuntimeException("item is not in pq");
        if (keys[k].compareTo(key) <= 0) throw new RuntimeException("illegal decrease");
        keys[k] = key;
        swim(qp[k]);
    }

    // increase the key associated with index k
    public void increase(int k, Key key) {
        if (!contains(k)) throw new RuntimeException("item is not in pq");
        if (keys[k].compareTo(key) >= 0) throw new RuntimeException("illegal decrease");
        keys[k] = key;
        sink(qp[k]);
    }


   /**************************************************************
    * General helper functions
    **************************************************************/
    private boolean greater(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
        int swap = pq[i]; pq[i] = pq[j]; pq[j] = swap;
        qp[pq[i]] = i; qp[pq[j]] = j;
    }


   /**************************************************************
    * Heap helper functions
    **************************************************************/
    private void swim(int k)  {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }


   /***********************************************************************
    * Iterators
    **********************************************************************/

   /**
     * Return an iterator that iterates over all of the elements on the
     * priority queue in ascending order.
     * <p>
     * The iterator doesn't implement <tt>remove()</tt> since it's optional.
     */
    public Iterator<Integer> iterator() { return new HeapIterator(); }

    private class HeapIterator implements Iterator<Integer> {
        // create a new pq
        private IndexMinPQ<Key> copy;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            copy = new IndexMinPQ<Key>(pq.length - 1);
            for (int i = 1; i <= N; i++)
                copy.insert(pq[i], keys[pq[i]]);
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }


    public static void main(String[] args) {
        // insert a bunch of strings
        String[] strings = { "it", "was", "the", "best", "of", "times", "it", "was", "the", "worst" };

        IndexMinPQ<String> pq = new IndexMinPQ<String>(strings.length);
        for (int i = 0; i < strings.length; i++) {
            pq.insert(i, strings[i]);
        }

        // delete and print each key
        while (!pq.isEmpty()) {
            int i = pq.delMin();
            System.out.println(i + " " + strings[i]);
        }
        System.out.println();

        // reinsert the same strings
        for (int i = 0; i < strings.length; i++) {
            pq.insert(i, strings[i]);
        }

        // print each key using the iterator
        for (int i : pq) {
            System.out.println(i + " " + strings[i]);
        }
        while (!pq.isEmpty()) {
            pq.delMin();
        }

    }
}
