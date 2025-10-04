import java.util.ArrayList;
import java.util.List;

public class Trapecio {

    // La función a integrar: f(x) = 2x^2 + 3x + 0.5
    private static double f(double x) {
        return 2 * x * x + 3 * x + 0.5;
    }

    // Clase interna para el trabajador (worker thread)
    // Basada en tu clase `trapecio` original.
    private static class Worker implements Runnable {
        private final long startN;
        private final long endN;
        private final double a;
        private final double h;
        private double partialSum;

        public Worker(long startN, long endN, double a, double h) {
            this.startN = startN;
            this.endN = endN;
            this.a = a;
            this.h = h;
            this.partialSum = 0.0;
        }

        @Override
        public void run() {
            // Este método es más directo: cada worker suma el área de los trapecios que le tocan.
            for (long i = startN; i < endN; i++) {
                double xi = a + i * h;
                double xj = a + (i + 1) * h;
                partialSum += (f(xi) + f(xj)) / 2.0 * h;
            }
        }

        public double getPartialSum() {
            return partialSum;
        }
    }

    public static void main(String[] args) {
        double a = 2.0;
        double b = 20.0;
        double tolerancia = 1e-7;
        int numThreads = Runtime.getRuntime().availableProcessors();

        System.out.println("Usando hilos basicos (Threads) con " + numThreads + " hilos.");

        double resultadoAnterior = 0.0;
        double resultadoActual = 0.0;

        // Bucle de convergencia
        for (long N = 1000; N < 100000000; N *= 2) {
            long startTime = System.currentTimeMillis();
            
            double h = (b - a) / N;
            
            List<Thread> threads = new ArrayList<>();
            List<Worker> workers = new ArrayList<>();
            
            long trapeciosPorThread = N / numThreads;
            
            for (int i = 0; i < numThreads; i++) {
                long start = i * trapeciosPorThread;
                long end = (i == numThreads - 1) ? N : (i + 1) * trapeciosPorThread;
                
                Worker worker = new Worker(start, end, a, h);
                workers.add(worker);
                Thread thread = new Thread(worker);
                threads.add(thread);
                thread.start();
            }

            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            resultadoActual = 0.0;
            for (Worker worker : workers) {
                resultadoActual += worker.getPartialSum();
            }

            long endTime = System.currentTimeMillis();
            
            System.out.printf("N = %d, Resultado = %.15f, Tiempo = %d ms\n", N, resultadoActual, (endTime - startTime));

            if (Math.abs(resultadoActual - resultadoAnterior) < tolerancia && N > 1000) {
                System.out.println("\nConvergencia alcanzada.");
                break;
            }
            resultadoAnterior = resultadoActual;
        }
        
        System.out.printf("\nResultado final aproximado de la integral: %.15f\n", resultadoActual);
    }
}