import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MostDivisorPool {

    int nThreads;

    ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();
    BlockingQueue<int[]> resQueue = new LinkedBlockingQueue<>();

    Thread[] threads;


    private class Task {
        int x;

        Task(int x) {
            this.x = x;
        }

        public int[] run() {
            int D;                          // A number to be tested to see if it's a divisor of N.
            int divisorCount = 0;               // Number of divisors of N.

            for (D = 1; D <= x; D++) {   // Count the divisors of N.
                if (x % D == 0) {
                    divisorCount++;
                }
            }

            return new int[]{x, divisorCount};
        }

    }

    private class Worker extends Thread {


        @Override
        public void run() {
            while (true) {
                Task task = taskQueue.poll();
                if (task == null) {
                    break;
                }

                int[] res = task.run();
                try {
                    resQueue.put(res);
                } catch (InterruptedException e) {
                    System.err.println(String.format("Interrupted exception in thread %d",
                            Thread.currentThread().getId()));
                }
            }
        }
    }

    public MostDivisorPool(int upper, int nThreads) {

        this.nThreads = nThreads;
        threads = new Worker[nThreads];

        for (int i = 1; i <= upper; ++i) {
            taskQueue.offer(new Task(i));
        }

        for (int i = 0; i < nThreads; ++i) {
            threads[i] = new Worker();
        }
    }

    public void start() {

        for (int i = 0; i < nThreads; ++i) {
            threads[i].start();
        }

    }

    public void end() {
        for (int i = 0; i < nThreads; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.err.println(String.format("Interrupted exception in thread %d",
                        Thread.currentThread().getId()));
            }
        }
    }

    public BlockingQueue<int[]> getBlockingQueue() {
        return resQueue;
    }


    public static void main(String[] args) throws InterruptedException {

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.print("How many threads do you want to use? ");
            try {
                int numberOfThreads = in.nextInt();
                if (numberOfThreads >= 1 && numberOfThreads <= 1000) {
                    int maxDivisors = 1;  // Start with the fact that 1 has 1 divisor.
                    int numWithMax = 1;
                    MostDivisorPool pool = new MostDivisorPool(1000_00, numberOfThreads);

                    BlockingQueue<int[]> resQueue = pool.getBlockingQueue();
                    long startTime = System.currentTimeMillis();

                    pool.start();


                    for (int i = 1; i <= 1000_00; ++i) {
                        int[] res = resQueue.take();
                        if (res[1] > maxDivisors) {
                            maxDivisors = res[1];
                            numWithMax = res[0];
                        }
                    }

                    pool.end();

                    long elapsedTime = System.currentTimeMillis() - startTime;
                    System.out.println("Among integers between 1 and 100000,");
                    System.out.println("The maximum number of divisors is " + maxDivisors);
                    System.out.println("A number with " + maxDivisors + " divisors is " + numWithMax);
                    System.out.println("Total elapsed time:  " + (elapsedTime / 1000.0) + " seconds.\n");
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
