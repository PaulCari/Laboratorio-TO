import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TrapecioPool {

    // La función a integrar
    public static double funcion(double x) {
        return 2 * x * x + 3 * x + 0.5;
    }

    // La tarea ahora es un Callable, porque devuelve un valor (el resultado parcial)
    static class PorcionCalculo implements Callable<Double> {
        private final long inicio;
        private final long fin;
        private final double a;
        private final double h;

        public PorcionCalculo(long inicio, long fin, double a, double h) {
            this.inicio = inicio;
            this.fin = fin;
            this.a = a;
            this.h = h;
        }

        @Override
        public Double call() {
            double sumaParcial = 0.0;
            for (long i = inicio; i < fin; i++) {
                double xi = a + i * h;
                double xj = a + (i + 1) * h;
                sumaParcial += (funcion(xi) + funcion(xj)) / 2.0 * h;
            }
            return sumaParcial;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        double a = 2.0;
        double b = 20.0;
        double tolerancia = 1e-7;
        int numThreads = Runtime.getRuntime().availableProcessors();
        // Creamos el Pool de Hilos
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        
        System.out.println("Usando un pool de " + numThreads + " threads.");

        double resultadoAnterior = 0.0;
        double resultadoActual = 0.0;

        for (long N = 1000; N < 100000000; N *= 2) {
            long startTime = System.currentTimeMillis();
            
            double h = (b - a) / N;
            
            // lista para guardar los resultados futuros
            List<Future<Double>> futures = new ArrayList<>();
            long trapeciosPorThread = N / numThreads;

            for (int i = 0; i < numThreads; i++) {
                long inicio = i * trapeciosPorThread;
                long fin = (i == numThreads - 1) ? N : (i + 1) * trapeciosPorThread;
                
                // Enviamos la tarea al pool y guardamos el Future

                futures.add(pool.submit(new PorcionCalculo(inicio, fin, a, h)));
            }

            resultadoActual = 0.0;

            // Recolectamos los resultados, esperando que la tarea termine

            for (Future<Double> future : futures) {
                resultadoActual += future.get();
            }
            
            long endTime = System.currentTimeMillis();

            System.out.printf("N = %d, Resultado = %.15f, Tiempo = %d ms\n", N, resultadoActual, (endTime - startTime));

            if (Math.abs(resultadoActual - resultadoAnterior) < tolerancia && N > 1000) {
                System.out.println("\nConvergencia alcanzada.");
                break;
            }
            resultadoAnterior = resultadoActual;
        }
        
        // Es importante apagar el pool al final
        
        pool.shutdown();
        System.out.printf("\nResultado final de la integral: %.15f\n", resultadoActual);
    }
}