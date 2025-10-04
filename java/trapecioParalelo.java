import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class trapecioParalelo {

    private static double f(double x) {
        return 2 * x * x + 3 * x + 0.5;
    }

    public static double computeSequential(double a, double b, int n) {
        double deltaX = (b - a) / n;
        double sum = f(a) + f(b);
        for (int i = 1; i < n; i++) {
            double xi = a + i * deltaX;
            sum += 2 * f(xi);
        }
        return (deltaX / 2.0) * sum;
    }

    public static double computeParallel(double a, double b, int n, int numThreads) {
        double deltaX = (b - a) / n;
        double sum = f(a) + f(b);

        List<Thread> threads = new ArrayList<>();
        List<trapecio> calculators = new ArrayList<>();

        int stepsPerThread = (n - 1) / numThreads;
        int start = 1;

        for (int i = 0; i < numThreads; i++) {
            int end = (i == numThreads - 1) ? n - 1 : start + stepsPerThread - 1;

            if (start <= end) {
                trapecio calculator = new trapecio(start, end, n, a, deltaX);
                calculators.add(calculator);
                Thread thread = new Thread(calculator);
                threads.add(thread);
                thread.start();
            }
            start = end + 1;
        }

        try {
            for (Thread thread : threads) thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Hilo interrumpido", e);
        }

        for (trapecio calculator : calculators) sum += calculator.getPartialSum();

        return (deltaX / 2.0) * sum;
    }

    public static void main(String[] args) {
        double a = 2.0, b = 20.0;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Introduce el numero de trapecios a utilizar (N): ");
            int n = scanner.nextInt();
            if (n <= 0) {
                System.out.println("El numero de trapecios debe ser un entero positivo.");
                return;
            }

            System.out.print("Introduce numero de hilos (0 = usar núcleos disponibles): ");
            int hilos = scanner.nextInt();
            if (hilos < 0) {
                System.out.println("Numero de hilos invalido.");
                return;
            }
            int numThreads = (hilos == 0) ? Runtime.getRuntime().availableProcessors() : hilos;

            long t1 = System.nanoTime();
            double seq = computeSequential(a, b, n);
            long t2 = System.nanoTime();
            double par = computeParallel(a, b, n, numThreads);
            long t3 = System.nanoTime();

            System.out.printf("\nResultado secuencial: %.10f (%.3f ms)%n", seq, (t2 - t1) / 1e6);
            System.out.printf("Resultado paralelo (%d hilos): %.10f (%.3f ms)%n", numThreads, par, (t3 - t2) / 1e6);

        } catch (InputMismatchException ime) {
            System.out.println("Entrada invalida. Asegurate de introducir enteros.");
        } finally {
            scanner.close();
        }
    }
}