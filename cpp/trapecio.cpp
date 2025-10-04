#include <iostream>
#include <vector>
#include <thread>
#include <functional>
#include <cmath>
#include <iomanip>
#include <chrono>

// funcion para integrar : f(x) = 2x^2 + 3x + 0.5
double funcion(double x) {
    return 2 * x * x + 3 * x + 0.5;
}

//  calcula una parte de la integral
void calcular_porcion(int id, long long inicio, long long fin, double a, double h, double* resultado_parcial) {
    double suma_parcial = 0.0;
    for (long long i = inicio; i < fin; ++i) {
        double x_i = a + i * h;
        double x_j = a + (i + 1) * h;
        suma_parcial += (funcion(x_i) + funcion(x_j)) / 2.0 * h;
    }
    *resultado_parcial = suma_parcial;
}

int main() {
    const double a = 2.0;
    const double b = 20.0;
    const double tolerancia = 1e-7; // Tolerancia para la convergencia
    unsigned int num_threads = std::thread::hardware_concurrency(); // Usar el número de núcleos disponibles

    std::cout << "Usando " << num_threads << " threads." << std::endl;

    double resultado_anterior = 0.0;
    double resultado_actual = 0.0;

    for (long long N = 100; N < 100000000; N *= 2) { // Empezamos con N trapecios y duplicamos
        auto start_time = std::chrono::high_resolution_clock::now();

        double h = (b - a) / N;
        std::vector<std::thread> threads;
        std::vector<double> resultados_parciales(num_threads);

        long long trapecios_por_thread = N / num_threads;

        for (unsigned int i = 0; i < num_threads; ++i) {
            long long inicio = i * trapecios_por_thread;
            long long fin = (i == num_threads - 1) ? N : (i + 1) * trapecios_por_thread;
            threads.emplace_back(calcular_porcion, i, inicio, fin, a, h, &resultados_parciales[i]);
        }

        resultado_actual = 0;
        for (unsigned int i = 0; i < num_threads; ++i) {
            threads[i].join();
            resultado_actual += resultados_parciales[i];
        }

        auto end_time = std::chrono::high_resolution_clock::now();
        std::chrono::duration<double, std::milli> elapsed = end_time - start_time;

        std::cout << "N = " << N << std::setprecision(15)
                  << ", Resultado = " << resultado_actual
                  << ", Tiempo = " << elapsed.count() << " ms" << std::endl;

        if (std::abs(resultado_actual - resultado_anterior) < tolerancia && N > 100) {
            std::cout << "\nConvergencia alcanzada." << std::endl;
            break;
        }
        resultado_anterior = resultado_actual;
    }

    std::cout << "\nResultado final de la integral: " << std::setprecision(15) << resultado_actual << std::endl;

    return 0;
}