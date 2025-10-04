
import java.util.Scanner;

public class trapecio{

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

        double deltaX = (b - a) / n;
        double sum = f(a) + f(b);

        for (int i = 1; i < n; i++) {
            double xi = a + i * deltaX;
            sum += 2 * f(xi);
        }

        double finalArea = (deltaX / 2.0) * sum;
        System.out.printf("\n Para N = %d trapecios, el area aproximada es: %.10f%n", n, finalArea);
    }
}
