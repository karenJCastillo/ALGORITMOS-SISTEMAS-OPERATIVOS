package org.example;

import java.util.*;

public class Main {


    static char[] referencias = {'D', 'C', 'B', 'A', 'D', 'C', 'E', 'D', 'C', 'B', 'A', 'E'}; //SECUECNIA DE LAS LETRAS
    static int MARCOS = 3;
    static int N = referencias.length;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   ALGORITMOS DE REEMPLAZO DE PÁGINAS");
        System.out.println("   Secuencia: D C B A D C E D C B A E  |  Marcos: " + MARCOS);
        System.out.println("==========================================================\n");

        simulacionOptimo();
        simulacionFIFO();
        simulacionLRU();
    }

    // optimo
    static void simulacionOptimo() {
        System.out.println("----------------------------------------------------------");
        System.out.println(" ALGORITMO ÓPTIMO");
        System.out.println("----------------------------------------------------------");

        List<Character> marcos = new ArrayList<>();
        int fallos = 0;
        boolean[] esFallo = new boolean[N];

        // Matrices para guardar estado de marcos en cada tiempo
        char[][] estadoMarcos = new char[MARCOS][N];
        for (char[] fila : estadoMarcos) Arrays.fill(fila, '-');

        for (int t = 0; t < N; t++) {
            char ref = referencias[t];

            if (!marcos.contains(ref)) {
                fallos++;
                esFallo[t] = true;

                if (marcos.size() < MARCOS) {
                    marcos.add(ref);
                } else {
                    // Encontrar la página que se usará más tarde (o nunca)
                    int victima = encontrarVictimaOptimo(marcos, t + 1);
                    System.out.printf("   [t=%2d] Fallo en '%c' → reemplazar '%c' (uso más lejano)\n",
                            t + 1, ref, marcos.get(victima));
                    marcos.set(victima, ref);
                }
            } else {
                System.out.printf("   [t=%2d] '%c' → HIT (ya está en marcos)\n", t + 1, ref);
            }

            for (int m = 0; m < marcos.size(); m++) {
                estadoMarcos[m][t] = marcos.get(m);
            }
        }

        imprimirTabla(estadoMarcos, esFallo, "ÓPTIMO");
        imprimirResultados(fallos, "ÓPTIMO");
    }

    static int encontrarVictimaOptimo(List<Character> marcos, int desde) {
        int[] proximoUso = new int[marcos.size()];
        Arrays.fill(proximoUso, Integer.MAX_VALUE);

        for (int i = 0; i < marcos.size(); i++) {
            for (int t = desde; t < N; t++) {
                if (referencias[t] == marcos.get(i)) {
                    proximoUso[i] = t;
                    break;
                }
            }
        }

        int victima = 0;
        for (int i = 1; i < proximoUso.length; i++) {
            if (proximoUso[i] > proximoUso[victima]) victima = i;
        }
        return victima;
    }

    //Optimo
    static void simulacionFIFO() {
        System.out.println("----------------------------------------------------------");
        System.out.println(" ALGORITMO FIFO (First In, First Out)");
        System.out.println("----------------------------------------------------------");

        List<Character> marcos = new ArrayList<>();
        Queue<Character> cola = new LinkedList<>();
        int fallos = 0;
        boolean[] esFallo = new boolean[N];
        char[][] estadoMarcos = new char[MARCOS][N];
        for (char[] fila : estadoMarcos) Arrays.fill(fila, '-');

        for (int t = 0; t < N; t++) {
            char ref = referencias[t];

            if (!marcos.contains(ref)) {
                fallos++;
                esFallo[t] = true;

                if (marcos.size() < MARCOS) {
                    marcos.add(ref);
                    cola.add(ref);
                } else {
                    char saliente = cola.poll();
                    System.out.printf("   [t=%2d] Fallo en '%c' → reemplazar '%c' (más antiguo)\n",
                            t + 1, ref, saliente);
                    marcos.set(marcos.indexOf(saliente), ref);
                    cola.add(ref);
                }
            } else {
                System.out.printf("   [t=%2d] '%c' → HIT (ya está en marcos)\n", t + 1, ref);
            }

            for (int m = 0; m < marcos.size(); m++) {
                estadoMarcos[m][t] = marcos.get(m);
            }
        }

        imprimirTabla(estadoMarcos, esFallo, "FIFO");
        imprimirResultados(fallos, "FIFO");
    }

    //LRU
    static void simulacionLRU() {
        System.out.println("----------------------------------------------------------");
        System.out.println(" ALGORITMO LRU (Least Recently Used)");
        System.out.println("----------------------------------------------------------");

        List<Character> marcos = new ArrayList<>();
        LinkedHashMap<Character, Integer> usoReciente = new LinkedHashMap<>();
        int fallos = 0;
        boolean[] esFallo = new boolean[N];
        char[][] estadoMarcos = new char[MARCOS][N];
        for (char[] fila : estadoMarcos) Arrays.fill(fila, '-');

        for (int t = 0; t < N; t++) {
            char ref = referencias[t];

            if (!marcos.contains(ref)) {
                fallos++;
                esFallo[t] = true;

                if (marcos.size() < MARCOS) {
                    marcos.add(ref);
                } else {
                    // Encontrar la menos recientemente usada
                    char lru = encontrarLRU(usoReciente);
                    System.out.printf("   [t=%2d] Fallo en '%c' → reemplazar '%c' (menos reciente)\n",
                            t + 1, ref, lru);
                    marcos.set(marcos.indexOf(lru), ref);
                    usoReciente.remove(lru);
                }
            } else {
                System.out.printf("   [t=%2d] '%c' → HIT (ya está en marcos)\n", t + 1, ref);
                usoReciente.remove(ref);
            }

            usoReciente.put(ref, t);

            for (int m = 0; m < marcos.size(); m++) {
                estadoMarcos[m][t] = marcos.get(m);
            }
        }

        imprimirTabla(estadoMarcos, esFallo, "LRU");
        imprimirResultados(fallos, "LRU");
    }

    static char encontrarLRU(LinkedHashMap<Character, Integer> usoReciente) {
        // El primero del mapa es el menos recientemente usado
        return usoReciente.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .get().getKey();
    }

    //calcular fallos
    static void imprimirTabla(char[][] estadoMarcos, boolean[] esFallo, String algoritmo) {
        System.out.println();
        System.out.println("  TABLA - " + algoritmo);

        // Encabezado de tiempos
        System.out.print("  Tiempo    |");
        for (int t = 0; t < N; t++) System.out.printf(" %2d |", t + 1);
        System.out.println();

        // Referencia
        System.out.print("  Referencia|");
        for (char r : referencias) System.out.printf("  %c |", r);
        System.out.println();

        // Separador
        System.out.print("  ----------+");
        for (int t = 0; t < N; t++) System.out.print("----+");
        System.out.println();

        // Marcos
        for (int m = 0; m < MARCOS; m++) {
            System.out.printf("  Marco  %d  |", m);
            for (int t = 0; t < N; t++) {
                char v = estadoMarcos[m][t];
                System.out.printf("  %c |", v == 0 ? '-' : v);
            }
            System.out.println();
        }

        // Separador
        System.out.print("  ----------+");
        for (int t = 0; t < N; t++) System.out.print("----+");
        System.out.println();

        // Fallos
        System.out.print("  Fallo     |");
        for (boolean f : esFallo) System.out.printf("  %c |", f ? 'X' : ' ');
        System.out.println();

        System.out.println();
    }

    //imprimir resultados
    static void imprimirResultados(int fallos, String algoritmo) {
        int hits = N - fallos;
        double tasaFallo = (double) fallos / N;
        double rendimiento = (double) hits / N;

        System.out.println("  RESULTADOS - " + algoritmo + ":");
        System.out.printf("    Total referencias : %d\n", N);
        System.out.printf("    Fallos de página  : %d  → F = %d/%d = %.2f\n",
                fallos, fallos, N, tasaFallo);
        System.out.printf("    Hits (aciertos)   : %d  → R = %d/%d = %.2f\n",
                hits, hits, N, rendimiento);
        System.out.println();
    }
}