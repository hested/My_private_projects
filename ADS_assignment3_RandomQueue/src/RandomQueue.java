import java.util.Iterator;

/**
 * Created by Johnni on 18-02-2017.
 */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class RandomQueue<Item> implements Iterable<Item> {

    private Item[] mQueue;
    private int mQueueSize;
    private int mCapacity;

    // create an empty random mQueue
    public RandomQueue() {
        mCapacity = 2;
        mQueueSize = 0;
        mQueue = (Item[]) new Object[mCapacity];
    }

    private void capacityDouble() {
        mCapacity *= 2;
        Item[] tempQueue = (Item[]) new Object[mCapacity];
        for(int i = 0; i < mQueueSize; i++){
            tempQueue[i] = mQueue[i];
        }
        mQueue = tempQueue;
    }

    private void capacityHalve() {
        mCapacity /= 2;
        Item[] tempQueue = (Item[]) new Object[mCapacity];
        for(int i = 0; i < mQueueSize; i++){
            tempQueue[i] = mQueue[i];
        }
        mQueue = tempQueue;
    }

    private int getRandomIndex(int queueSize){
        if(queueSize == 0){
            return 0;
        } else {
            return(StdRandom.uniform(queueSize));
        }
    }

    // is it empty?
    public boolean isEmpty() {
        return mQueueSize == 0;
    }

    // return the number of elements
    public int size() {
        return mQueueSize;
    }

    // return (but do not remove) a random item
    public Item sample() {
        if (isEmpty()) throw new RuntimeException("Can not return a sample because the queue is empty.");
        int sampleIndex = getRandomIndex(mQueueSize);
        return mQueue[sampleIndex];
    }

    // add an item
    public void enqueue(Item item) {
        if (item == null) throw new RuntimeException("Can not enqueue because the item is NULL.");
        if (mQueueSize + 1 > mCapacity) {
            capacityDouble();
        }
        mQueue[mQueueSize] = item;
        mQueueSize++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new RuntimeException("Can not dequeue a sample because the queue is empty.");
        int indexNumber = getRandomIndex(mQueueSize);
        Item item = mQueue[indexNumber];
        mQueue[indexNumber] = mQueue[mQueueSize - 1];
        mQueue[mQueueSize - 1] = null;
        mQueueSize--;
        if (mQueueSize < mCapacity / 4) {
            capacityHalve();
        }
        return item;
    }

    // return an iterator over the items in random order
    public Iterator<Item> iterator() {
        return new queueIterator();
    }

    // queue iterator private class
    private class queueIterator implements Iterator<Item> {
        private int current = 0;
        private int[] shuffledIndex = new int[mQueueSize];

        private void shuffle() {
            if (current == 0) {
                for (int i = 0; i < mQueueSize; i++) {
                    shuffledIndex[i] = i;
                }
                StdRandom.shuffle(shuffledIndex);
            }
        }

        public boolean hasNext() {
            shuffle();
            return current < mQueueSize;
        }

        public Item next() {
            shuffle();
            if (current >= mQueueSize || size() == 0) {
                throw new RuntimeException();
            }
            return mQueue[shuffledIndex[current++]];
        }

        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }

    // The main method below tests your implementation. Do not change it.
    public static void main(String args[]) {
        // Build a mQueue containing the Integers 1,2,...,6:
        RandomQueue<Integer> Q = new RandomQueue<Integer>();
        for (int i = 1; i < 7; ++i) Q.enqueue(i); // autoboxing! cool!
        // Print 30 die rolls to standard output
        StdOut.print("Some die rolls: ");
        for (int i = 1; i < 30; ++i) StdOut.print(Q.sample() + " ");
        StdOut.println();
        // Let’s be more serious: do they really behave like die rolls?
        int[] rolls = new int[10000];
        for (int i = 0; i < 10000; ++i)
            rolls[i] = Q.sample(); // autounboxing! Also cool!
        StdOut.printf("Mean (should be around 3.5): %5.4f\n", StdStats.mean(rolls));
        StdOut.printf("Standard deviation (should be around 1.7): %5.4f\n",
                StdStats.stddev(rolls));
        // Now remove 3 random values
        StdOut.printf("Removing %d %d %d\n", Q.dequeue(), Q.dequeue(), Q.dequeue());
        // Add 7,8,9
        for (int i = 7; i < 10; ++i) Q.enqueue(i);
        // Empty the mQueue in random order
        while (!Q.isEmpty()) StdOut.print(Q.dequeue() + " ");
        StdOut.println();
        // Let’s look at the iterator. First, we make a mQueue of colours:
        RandomQueue<String> C = new RandomQueue<String>();
        C.enqueue("red");
        C.enqueue("blue");
        C.enqueue("green");
        C.enqueue("yellow");
        Iterator I = C.iterator();
        Iterator J = C.iterator();
        StdOut.print("Two colours from first shuffle: " + I.next() + " " + I.next() + " ");
        StdOut.print("\nEntire second shuffle: ");
        while (J.hasNext()) StdOut.print(J.next() + " ");
        StdOut.print("\nRemaining two colours from first shuffle: " + I.next() + " " + I.next());
    }
}
