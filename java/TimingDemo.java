public class TimingDemo {
    public static void main(String[] args) {
        double a = 2.0, b = 20.0;
        int n = 10_000_000; 
        int numThreads = Runtime.getRuntime().availableProcessors();

        long t1 = System.nanoTime();
        double seq = trapecioParalelo.computeSequential(a, b, n);
        long t2 = System.nanoTime();
        double par = trapecioParalelo.computeParallel(a, b, n, numThreads);
        long t3 = System.nanoTime();

        System.out.printf("N=%d, sec: %.5f ms, par(%d): %.5f ms, seqRes=%.10f, parRes=%.10f%n",
            n, (t2 - t1) / 1e6, numThreads, (t3 - t2) / 1e6, seq, par);
    }
}