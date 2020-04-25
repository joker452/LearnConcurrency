import static org.junit.Assert.assertEquals;

public class UnsafeCounter {

    static class Counter {
        int count;

        void inc() {
            count = count + 1;
        }

        int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        if (args.length == 2) {
            try {
                int nThread = Integer.parseInt(args[0]);
                int nTimesPerThread = Integer.parseInt(args[1]);

                if (nThread < 2 || nTimesPerThread < 1) {
                    System.err.println("Argument value wrong");
                    System.err.println("Unsafe coutner usage: <nThread> <nTimesPerThread>");
                    System.exit(1);
                }

                Counter counter = new Counter();

                Thread[] threads = new Thread[nThread];

                for (int i = 0; i < nThread; ++i) {
                    threads[i] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = 0; k < nTimesPerThread; ++k) {
                                counter.inc();
                            }
                        }
                    };
                }

                for (Thread t : threads) {
                    t.start();
                }

                for (Thread t : threads) {
                    t.join();
                }

                assertEquals(nThread * nTimesPerThread, counter.getCount());
            } catch (NumberFormatException e) {
                System.err.println("Argument format wrong");
                System.err.println("Unsafe coutner usage: <nThread> <nTimesPerThread>");
            }
        } else {
            System.err.println("Number of argument wrong");
            System.err.println("Unsafe coutner usage: <nThread> <nTimesPerThread>");
        }

    }
}
