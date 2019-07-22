package br.com.rodolfo.trabalho.utils;

import java.util.List;

/**
 * Metodos
 */
public class Metodos {

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

    public static String getMaxMinNominal(String nome) {

        return nome.toLowerCase().equals("sim") ? "Maximizar" : "Minimizar";
    }

    public static String formatarNumero(double numero) {
        
        return String.format("%.2f", numero);
    }

    private Metodos() {}
}