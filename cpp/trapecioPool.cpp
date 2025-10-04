#include <iostream>
#include <cmath>
#include <iomanip>

// función para integrar: f(x) = 2x^2 + 3x + 0.5
double funcion(double x) {
    return 2 * x * x + 3 * x + 0.5;
}

double integrar_trapecio(double a, double b, long long N) {
    double h = (b - a) / N;
    double suma = 0.0;

    for (long long i = 0; i < N; ++i) {
        double x_i = a + i * h;
        double x_j = a + (i + 1) * h;
        suma += (funcion(x_i) + funcion(x_j)) / 2.0 * h;
    }

    return suma;
}

int main() {
    const double a = 2.0;
    const double b = 20.0;
    long long N = 1000000;

    double resultado = integrar_trapecio(a, b, N);

    std::cout << std::setprecision(15);
    std::cout << "Resultado aproximado de la integral: " << resultado << std::endl;

    return 0;
}
