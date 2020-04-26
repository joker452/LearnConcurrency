import java.util.*;
import java.util.concurrent.*;

public class MostDivisorExecutor {
    int nThreads;



    Thread[] threads;

    private static class Task implements Callable<int[]> {
        int x;

        Task(int x) {
            this.x = x;
        }

        @Override
        public int[] call() {
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




    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.print("How many threads do you want to use? ");
            try {
                int numberOfThreads = in.nextInt();
                if (numberOfThreads >= 1 && numberOfThreads <= 1000) {
                    int maxDivisors = 1;  // Start with the fact that 1 has 1 divisor.
                    int numWithMax = 1;
                    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

                    List<Future<int[]>> resList = new ArrayList<>();

                    long startTime = System.currentTimeMillis();

                    for (int i = 1; i <= 1000_00; ++i) {
                        Future<int[]> oneRes = executor.submit(new MostDivisorExecutor.Task(i));
                        resList.add(oneRes);
                    }

                    for (int i = 1; i <= 1000_00; ++i) {
                        int[] res = resList.get(i - 1).get();
                        if (res[1] > maxDivisors) {
                            maxDivisors = res[1];
                            numWithMax = res[0];
                        }
                    }

                    long elapsedTime = System.currentTimeMillis() - startTime;

                    executor.shutdown();
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
