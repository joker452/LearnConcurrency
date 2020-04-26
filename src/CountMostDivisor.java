import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class CountMostDivisor {


    static int UPPER_LIMIT = 100_000;


    int maxDivisors;                              // Maximum number of divisors seen so far.
    int numWithMax;                              // A value of N that had the given number of divisors.
    Semaphore mutex = new Semaphore(1);   // mutex for updating maxDivisor and numWithMax


    private class Worker extends Thread {
        int lo, hi;

        Worker(int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        public void run() {
            calculateMaxDivisorInRange(lo, hi);
        }
    }

    private void calculateMaxDivisorInRange(int lo, int hi) {

        int localMaxDivisors = -1;
        int localnumWithMax = -1;
        for (int i = lo; i <= hi; ++i) {
            int D;                          // A number to be tested to see if it's a divisor of N.
            int divisorCount;               // Number of divisors of N.

            divisorCount = 0;

            for (D = 1; D <= i; D++) {   // Count the divisors of N.
                if (i % D == 0) {
                    divisorCount++;
                }
            }

            if (divisorCount > localMaxDivisors) {
                localMaxDivisors = divisorCount;
                localnumWithMax = i;
            }
        }

        try {
            mutex.acquire();
            if (localMaxDivisors > maxDivisors) {
                maxDivisors = localMaxDivisors;
                numWithMax = localnumWithMax;
            }
        } catch (InterruptedException e) {
            System.err.println(String.format("InterruptedException in thread %d",
                    Thread.currentThread().getId()));
        } finally {
            mutex.release();
        }
    }




    public void calculateMaxDivisor(int nThread) throws InterruptedException {
        maxDivisors = 1;  // Start with the fact that 1 has 1 divisor.
        numWithMax = 1;

       /* Process all the remaining values of N from 2 to 10000, and
          update the values of maxDivisors and numWithMax whenever we
          find a value of N that has more divisors than the current value
          of maxDivisors.
       */
        int numPerThread = (UPPER_LIMIT - 1) / nThread;

        int remain = (UPPER_LIMIT - 1) - numPerThread * nThread;


        Thread[] threads;

        if (remain > 0) {
            threads = new Thread[nThread + 1];
            ++nThread;
        } else {
            threads = new Thread[nThread];
        }


        for (int lo = 2, i = 1; i <= nThread; ++i) {
            threads[i - 1] = new Worker(lo, lo + numPerThread - 1);
            lo += numPerThread;
        }

        long startTime = System.currentTimeMillis();

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Among integers between 1 and 100000,");
        System.out.println("The maximum number of divisors is " + maxDivisors);
        System.out.println("A number with " + maxDivisors + " divisors is " + numWithMax);
        System.out.println("Total elapsed time:  " + (elapsedTime / 1000.0) + " seconds.\n");
    }


    public static void main(String[] args) throws InterruptedException {

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.print("How many threads do you want to use? ");
            try {
                int numberOfThreads = in.nextInt();
                if (numberOfThreads >= 1 && numberOfThreads <= 1000) {
                    CountMostDivisor counter = new CountMostDivisor();
                    counter.calculateMaxDivisor(numberOfThreads);
                    break;
                } else {
                    System.err.println("Please enter a 1 <= number <= 1000");
                }
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid integer");
            }
        }

        in.close();
    }

}
