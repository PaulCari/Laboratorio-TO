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

// Worker que calcula una porción y envía el resultado a un canal

func worker(startN, endN int64, a, h float64, results chan<- float64, wg *sync.WaitGroup) {
	defer wg.Done() 

	// avisa al waitGroup que esta goroutine ha terminado

	partialSum := 0.0
	for i := startN; i < endN; i++ {
		xi := a + float64(i)*h
		xj := a + float64(i+1)*h
		partialSum += (funcion(xi) + funcion(xj)) / 2.0 * h
	}

	// Envía el resultado al canal

	results <- partialSum 
}

func main() {
	const a = 2.0
	const b = 20.0
	const tolerancia = 1e-7
	numGoroutines := runtime.NumCPU()

	fmt.Printf("Usando goroutines basicas con %d goroutines.\n", numGoroutines)

	resultadoAnterior := 0.0
	resultadoActual := 0.0

	for N := int64(1000); N < 100000000; N *= 2 {
		startTime := time.Now()

		h := (b - a) / float64(N)

		// waitGroup para esperar a que todas las goroutines terminen

		var wg sync.WaitGroup

		// canal para recibir los resultados parciales

		results := make(chan float64, numGoroutines)

		trapeciosPorGroutine := N / int64(numGoroutines)

		for i := 0; i < numGoroutines; i++ {
			start := int64(i) * trapeciosPorGroutine
			end := (int64(i) + 1) * trapeciosPorGroutine
			if i == numGoroutines-1 {
				end = N
			}

			// incrementa el contador del WaitGroup

			wg.Add(1) 
			go worker(start, end, a, h, results, &wg)
		}
		

		// Lanza una goroutine para cerrar el canal una vez que todos los workers terminen
        // esto es común en go para saber cuándo hemos terminado de recibir resultados

		go func() {
			wg.Wait()
			close(results)
		}()
		
		resultadoActual = 0.0

		// Lee del canal hasta que se cierre

		for partialSum := range results {
			resultadoActual += partialSum
		}

		elapsed := time.Since(startTime)
		fmt.Printf("N = %d, Resultado = %.15f, Tiempo = %s\n", N, resultadoActual, elapsed)

		if math.Abs(resultadoActual-resultadoAnterior) < tolerancia && N > 1000 {
			fmt.Println("\nConvergencia alcanzada.")
			break
		}
		resultadoAnterior = resultadoActual
	}

	fmt.Printf("\nResultado final aproximado de la integral: %.15f\n", resultadoActual)
}