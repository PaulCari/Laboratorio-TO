package main

import (
	"fmt"
	"math"
	"runtime"
	"sync"
	"time"
)

// La función a integrar
func funcion(x float64) float64 {
	return 2*x*x + 3*x + 0.5
}

// Job representa la tarea a realizar
type Job struct {
	inicio int64
	fin    int64
	a      float64
	h      float64
}

// Result almacena el resultado de un Job
type Result struct {
	sumaParcial float64
}

// Worker que lee de un canal de jobs y escribe en un canal de resultados
func worker(jobs <-chan Job, results chan<- Result, wg *sync.WaitGroup) {
	defer wg.Done()
	for job := range jobs {
		sumaParcial := 0.0
		for i := job.inicio; i < job.fin; i++ {
			xi := job.a + float64(i)*job.h
			xj := job.a + float64(i+1)*job.h
			sumaParcial += (funcion(xi) + funcion(xj)) / 2.0 * job.h
		}
		results <- Result{sumaParcial: sumaParcial}
	}
}

func main() {
	const a = 2.0
	const b = 20.0
	const tolerancia = 1e-7
	numWorkers := runtime.NumCPU()

	fmt.Printf("Usando un pool de %d workers (goroutines).\n", numWorkers)

	resultadoAnterior := 0.0
	resultadoActual := 0.0

	for N := int64(100); N < 100000000; N *= 2 {
		startTime := time.Now()

		h := (b - a) / float64(N)

		jobs := make(chan Job, numWorkers)
		results := make(chan Result, numWorkers)

		var wg sync.WaitGroup
		// Iniciar workers
		for i := 0; i < numWorkers; i++ {
			wg.Add(1)
			go worker(jobs, results, &wg)
		}

		// Enviar jobs
		trapeciosPorWorker := N / int64(numWorkers)
		for i := 0; i < numWorkers; i++ {
			inicio := int64(i) * trapeciosPorWorker
			fin := (int64(i) + 1) * trapeciosPorWorker
			if i == numWorkers-1 {
				fin = N
			}
			jobs <- Job{inicio: inicio, fin: fin, a: a, h: h}
		}
		close(jobs)

		// Esperar que los workers terminen
		wg.Wait()
		close(results)

		// Recolectar resultados
		resultadoActual = 0.0
		for result := range results {
			resultadoActual += result.sumaParcial
		}

		elapsed := time.Since(startTime)
		fmt.Printf("N = %d, Resultado = %.15f, Tiempo = %s\n", N, resultadoActual, elapsed)

		if math.Abs(resultadoActual-resultadoAnterior) < tolerancia && N > 100 {
			fmt.Println("\nConvergencia alcanzada.")
			break
		}
		resultadoAnterior = resultadoActual
	}

	fmt.Printf("\nResultado final aproximado de la integral: %.15f\n", resultadoActual)
}