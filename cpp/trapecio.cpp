#include <iostream>
#include <vector>
#include <thread>
#include <cmath>
#include <iomanip>
#include <chrono>

double funcion(double x) {
    return 2 * x * x + 3 * x + 0.5;
}

void calcular_parcial(long long inicio, long long fin, double a, double h, double& resultado) {
    resultado = 0.0;
    for (long long i = inicio; i < fin; ++i) {
        double x_i = a + i * h;
        double x_j = a + (i + 1) * h;
        resultado += (funcion(x_i) + funcion(x_j)) / 2.0 * h;
    }
}

int main() {
    double a = 2.0, b = 20.0;
    long long N = 1000000;
    double h = (b - a) / N;

    unsigned int num_threads = std::thread::hardware_concurrency();
    std::cout << "Usando " << num_threads << " threads.\n";

    auto inicio_tiempo = std::chrono::high_resolution_clock::now();

    std::vector<std::thread> threads;
    std::vector<double> resultados(num_threads, 0.0);
    long long porcion = N / num_threads;

    for (unsigned int i = 0; i < num_threads; ++i) {
        long long ini = i * porcion;
        long long fin = (i == num_threads - 1) ? N : ini + porcion;
        threads.emplace_back(calcular_parcial, ini, fin, a, h, std::ref(resultados[i]));
    }

    double resultado_final = 0.0;
    for (auto& t : threads) t.join();
    for (double parcial : resultados) resultado_final += parcial;

    auto fin_tiempo = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double, std::milli> duracion = fin_tiempo - inicio_tiempo;

    std::cout << std::setprecision(15);
    std::cout << "Resultado: " << resultado_final << " | Tiempo: " << duracion.count() << " ms\n";

    return 0;
}
