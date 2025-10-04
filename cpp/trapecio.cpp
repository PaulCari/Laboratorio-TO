#include <iostream>
#include <vector>
#include <thread>
#include <cmath>
#include <iomanip>

double funcion(double x) {
    return 2 * x * x + 3 * x + 0.5;
}

void calcular_parcial(long long inicio, long long fin, double a, double h, double* resultado) {
    double suma = 0.0;
    for (long long i = inicio; i < fin; ++i) {
        double x_i = a + i * h;
        double x_j = a + (i + 1) * h;
        suma += (funcion(x_i) + funcion(x_j)) / 2.0 * h;
    }
    *resultado = suma;
}

int main() {
    double a = 2.0;
    double b = 20.0;
    long long N = 1000000;
    double h = (b - a) / N;

    unsigned int num_threads = 4;
    std::vector<std::thread> threads(num_threads);
    std::vector<double> resultados(num_threads);

    long long porcion = N / num_threads;

    for (unsigned int i = 0; i < num_threads; ++i) {
        long long inicio = i * porcion;
        long long fin = (i == num_threads - 1) ? N : inicio + porcion;
        threads[i] = std::thread(calcular_parcial, inicio, fin, a, h, &resultados[i]);
    }

    double resultado_final = 0.0;
    for (unsigned int i = 0; i < num_threads; ++i) {
        threads[i].join();
        resultado_final += resultados[i];
    }

    std::cout << std::setprecision(15);
    std::cout << "Resultado de la integral (multithread): " << resultado_final << std::endl;

    return 0;
}