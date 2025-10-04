
import java.util.Scanner;

public class trapecio implements Runnable{

    private final int startN;
    private final int endN;
    private final int totalTrapezoids;
    private final double a;
    private final double deltaX;
    private double partialSum;

    public trapecio(int startN, int endN, int totalTrapecios, double a, double deltaX) {
        this.startN = startN;
        this.endN = endN;
        this.totalTrapezoids = totalTrapecios;
        this.a = a;
        this.deltaX = deltaX;
        this.partialSum = 0.0;
    }

    private double f(double x) {
        return 2 * x * x + 3 * x + 0.5;
    }

    @Override
    public void run() {
        for (int i = startN; i <= endN; i++) {
            if (i > 0 && i < totalTrapezoids) {
                double xi = a + i * deltaX;
                partialSum += 2 * f(xi);
            }
        }
    }

    public double getPartialSum() {
        return partialSum;
    }
}