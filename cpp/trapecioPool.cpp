#include <iostream>
#include <vector>
#include <future>
#include <functional>
#include <cmath>
#include <iomanip>
#include <chrono>

// función a integrar: f(x) = 2x^2 + 3x + 0.5
double funcion(double x) {
    return 2 * x * x + 3 * x + 0.5;
}

// calcula una porción de la integral y devuelve el resultado
double calcular_porcion_future(long long inicio, long long fin, double a, double h) {
    double suma_parcial = 0.0;
    for (long long i = inicio; i < fin; ++i) {
        double x_i = a + i * h;
        double x_j = a + (i + 1) * h;
        suma_parcial += (funcion(x_i) + funcion(x_j)) / 2.0 * h;
    }
    return suma_parcial;
}

int main() {
    const double a = 2.0;
    const double b = 20.0;
    const double tolerancia = 1e-7;
    unsigned int num_threads = std::thread::hardware_concurrency();

    std::cout << "Usando un enfoque de pool de hilos (std::async) con " << num_threads << " tareas concurrentes." << std::endl;

    double resultado_anterior = 0.0;
    double resultado_actual = 0.0;

    for (long long N = 100; N < 100000000; N *= 2) {
        auto start_time = std::chrono::high_resolution_clock::now();

        double h = (b - a) / N;
        std::vector<std::future<double>> futures;

        long long trapecios_por_thread = N / num_threads;

        for (unsigned int i = 0; i < num_threads; ++i) {
            long long inicio = i * trapecios_por_thread;
            long long fin = (i == num_threads - 1) ? N : (i + 1) * trapecios_por_thread;
            // std::launch::async asegura que se ejecute en un hilo separado
            futures.push_back(std::async(std::launch::async, calcular_porcion_future, inicio, fin, a, h));
        }

        resultado_actual = 0;
        for (auto& f : futures) {
            resultado_actual += f.get(); // .get() espera a que el futuro esté listo y obtiene el valor
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