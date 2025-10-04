import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class trapecioParalelo {

    private static double f(double x) {
        return 2 * x * x + 3 * x + 0.5;
    }

    public static void main(String[] args) {
        double a = 2.0;
        double b = 20.0;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce el numero de trapecios a utilizar (N): ");
        int n = scanner.nextInt();
        scanner.close();

        if (n <= 0) {
            System.out.println("El numero de trapecios debe ser un entero positivo.");
            return;
        }

        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("\nUsando " + numThreads + " hilos para la simulacion paralela.");

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
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (trapecio calculator : calculators) {
            sum += calculator.getPartialSum();
        }

        double finalArea = (deltaX / 2.0) * sum;
        System.out.printf("\n(Paralelo) Para N = %d trapecios, el area aproximada es: %.10f%n", n, finalArea);
    }
}

