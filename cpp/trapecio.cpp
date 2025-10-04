#include <iostream>
#include <cmath>
#include <iomanip>

//funcion para integrar : f(x) = 2x^2 + 3x + 0.5
double funcion(double x) {
    return 2 * x * x + 3 * x + 0.5;
}

int main() {
    double a = 2.0;
    double b = 20.0;
    long long N = 1000000; // número de trapecios
    double h = (b - a) / N;
    double resultado = 0.0;

    for (long long i = 0; i < N; ++i) {
        double x_i = a + i * h;
        double x_j = a + (i + 1) * h;
        resultado += (funcion(x_i) + funcion(x_j)) / 2.0 * h;
    }

    std::cout << std::setprecision(15);
    std::cout << "Resultado aproximado de la integral: " << resultado << std::endl;

    return 0;
}
