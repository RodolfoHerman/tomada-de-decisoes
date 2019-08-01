package br.com.rodolfo.trabalho.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.ssc.pl.milp.GoalType;

/**
 * Metodos
 */
public class Metodos {

    public static List<Double> getMaxColunas(double[][] matriz) {
        
        return IntStream.range(0, matriz[0].length).boxed()
            .map(indice -> Stream.of(matriz).map(elementos -> elementos[indice]).mapToDouble(d -> d).max().orElse(0.0))        
            .collect(Collectors.toList());
    }
    
    public static boolean verificarArquivos(List<String> arquivos, String[] lista) {
        
        int qtd = arquivos.size();

        for(String arq : lista) {

            if(arquivos.contains(arq)) {

                qtd--;
            }
        }

        return qtd == 0 ? true : false;
    }

    public static void imprimirMatriz(double[][] matriz) {

        for (int x = 0; x < matriz.length; x++) {
            for (int y = 0; y < matriz[0].length; y++) {

                System.out.print(matriz[x][y] + "\t\t");
            }
            System.out.println();
        }
    }

    public static List<List<Double[]>> transformarArray2DParaListaArrayAgrupada(double[][] matriz, int tamanho) {

        AtomicInteger contador = new AtomicInteger();

        return Arrays.stream(matriz)
        .map(linha -> DoubleStream.of(linha).boxed().collect(Collectors.toList()))
        .map(elem -> elem.stream()
                .collect(Collectors.groupingBy(it -> contador.getAndIncrement() / tamanho))
                .values()
                .stream()
                .map(valores -> valores.stream().toArray(Double[]::new)
            ).collect(Collectors.toList())
        ).collect(Collectors.toList());
    }
    
    public static List<List<List<Double>>> transformarArray2DParaListasAgrupada(double[][] matriz, int tamanho) {
        
        List<List<Double>> lista = transformarArray2DParaLista(matriz);
        AtomicInteger contador   = new AtomicInteger();

        return lista.stream().map(
                linha -> linha.stream().collect(Collectors.groupingBy(it -> contador.getAndIncrement()/tamanho))
                    .values()
                    .stream()
                    .collect(Collectors.toList())
            ).collect(Collectors.toList());
    }

    public static List<List<Double>> transformarArray2DParaLista(double[][] matriz) {

        return Arrays.stream(matriz).map(linha -> DoubleStream.of(linha).boxed().collect(Collectors.toList())).collect(Collectors.toList());
    }

    public static String getMinMaxNominal(GoalType tipo) {

        return tipo.name().equals("MAX") ? "Maximizar" : "Minimizar";
    }

    public static String formatarNumero(double numero) {

        return String.format("%.4f", numero);
    }

    public static Double trucarNumero(Double numero) {
        
        return ((new BigDecimal(numero))
            .setScale(4, RoundingMode.HALF_UP))
            .doubleValue();
    }

    private Metodos() {}
}