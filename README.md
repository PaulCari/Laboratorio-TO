# Laboratorio 04: Programación Paralela y Cálculo de Integrales

Este repositorio contiene la solución para el Laboratorio 04 del curso Tecnología de Objetos. El objetivo es calcular una integral definida utilizando el método del trapecio de forma paralela en Java, C++ y Go.

**Integrantes:**
*   Cari Lipe Paul Andree
*   Quispe Arratea Alexandra Raquel

## Descripción del Problema

Se calcula la integral de la función `f(x) = 2x² + 3x + 0.5` en el intervalo `[2, 20]`. La solución se implementa utilizando un enfoque de paralelismo con threads y un enfoque con pool de hilos (o su equivalente idiomático en cada lenguaje) para comparar rendimientos.

El cálculo se realiza iterativamente, aumentando el número de trapecios (N) hasta que el resultado converge con una tolerancia predefinida.

## Estructura del Repositorio

-   `/cpp`: Contiene las implementaciones en C++.
-   `/java`: Contiene las implementaciones en Java.
-   `/go`: Contiene las implementaciones en Go.
-   `/docs`: Contiene el informe del laboratorio en formato PDF.

## Cómo Compilar y Ejecutar

### C++
Navega a la carpeta `cpp`.
```bash
# Versión con threads básicos
g++ -std=c++17 -o trapecio trapecio.cpp -pthread && ./trapecio

# Versión con Thread Pool (std::async)
g++ -std=c++17 -o trapecioPool trapecioPool.cpp -pthread && ./trapecioPool
```

### Java
Navega a la carpeta `java`. (Asegúrate de tener el JDK instalado).
```bash
# Versión con Thread Pool (se deja la versión con threads básicos como ejercicio)
javac TrapecioPool.java && java TrapecioPool
```

### Go
Navega a la carpeta `go`. (Asegúrate de tener Go instalado).
```bash
# Versión con Worker Pool
go run trapecioPool.go
```


