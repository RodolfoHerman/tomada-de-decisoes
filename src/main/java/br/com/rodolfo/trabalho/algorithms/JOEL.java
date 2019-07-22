package br.com.rodolfo.trabalho.algorithms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.rodolfo.trabalho.algorithms.sequence.Sobol;
import br.com.rodolfo.trabalho.models.Objetivo;
import br.com.rodolfo.trabalho.models.Projeto;
import br.com.rodolfo.trabalho.models.Restricao;
import br.com.rodolfo.trabalho.utils.Metodos;

/**
 * JOEL
 */
public class JOEL implements Execute {

    private final List<Objetivo> objetivos;
    private final List<Restricao> restricoes;
    private final int cenarios;

    public JOEL(int cenarios, List<Objetivo> objetivos, List<Restricao> restricoes) {

        this.cenarios   = cenarios; 
        this.objetivos  = objetivos;
        this.restricoes = restricoes;
    }

    @Override
    public String execute() {
        
        StringBuilder imprimir = new StringBuilder();

        imprimir.append(imprimirRestricoes());
        imprimir.append(System.lineSeparator()).append(System.lineSeparator());
        imprimir.append(imprimirObjetivos());
        imprimir.append(System.lineSeparator()).append(System.lineSeparator());
        imprimir.append(imprimirEstadosDaNatureza(representarEstadosDaNatureza()));

        return imprimir.toString();
    }

    public double[][] representarEstadosDaNatureza() {
        
        int dimensao = getDimensaoSobol();
        double[][] sobol  = (new Sobol()).generate((cenarios + 1), dimensao);
        double[][] estadosDaNatureza = new double[cenarios][dimensao];

        List<Double[]> intervaloCoeficientes = objetivos.stream()
            .flatMap(objetivo -> objetivo.getProjetos()
            .stream()
            .map(projeto -> new Double[]{projeto.getLower_c(), projeto.getUpper_c()}))
            .collect(Collectors.toList());

        for(int x = 0; x < cenarios; x++) {
            for(int y = 0; y < dimensao; y++) {

                estadosDaNatureza[x][y] = cacularInterpolacao(intervaloCoeficientes.get(y), sobol[x+1][y]);
            }
        }

        return estadosDaNatureza;
    }

    private int getDimensaoSobol() {

        int qtdFuncoesObjetivo = objetivos.size();
        int qtdCoeficientes    = restricoes.get(0).getCoeficientes().length;

        return qtdFuncoesObjetivo * qtdCoeficientes;
    }

    private double cacularInterpolacao(Double[] intervalo, double valor) {
        
        return ((new BigDecimal(intervalo[0] + ((intervalo[1] - intervalo[0]) * valor)))
            .setScale(2, RoundingMode.HALF_UP))
            .doubleValue();
    }

    private String imprimirRestricoes() {
        
        StringBuilder imprimir = new StringBuilder();
        List<String> temp = new ArrayList<>();

        imprimir.append("######").append(" Restrições ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        this.restricoes.stream().forEach(restricao -> {

            imprimir.append("(").append(restricao.getNome()).append(")").append("\t");
            Integer posicao = 1;
            temp.clear();

            for(Double valor : restricao.getCoeficientes()) {

                temp.add("".concat(valor.toString()).concat("*X").concat(posicao.toString()));
                posicao++;
            }

            imprimir.append(String.join(" + ", temp)).append(" ").append(restricao.getSinal()).append(" ").append(restricao.getValor());
            imprimir.append(System.lineSeparator());

        });

        return imprimir.toString();
    }

    private String imprimirObjetivos() {
        
        StringBuilder imprimir = new StringBuilder();

        imprimir.append("######").append(" Informações iniciais para construção das funções objetivo (intervalos dos coeficientes). ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        this.objetivos.stream().forEach(objetivo -> {

            imprimir.append("(").append(objetivo.getNome()).append(")").append("\t")
                .append(Metodos.getMaxMinNominal(objetivo.getMaximizar()))
                .append(System.lineSeparator()).append(System.lineSeparator())
                .append("Coef")
                .append("\t\t")
                .append("C'")
                .append("\t\t\t\t")
                .append("C''")
                .append(System.lineSeparator());

            for(Projeto projeto : objetivo.getProjetos()) {

                imprimir.append(" ").append(projeto.getNome()).append("\t\t").append(Metodos.formatarNumero(projeto.getLower_c()))
                    .append("\t\t\t").append(Metodos.formatarNumero(projeto.getUpper_c()))
                    .append(System.lineSeparator());
            }

            imprimir.append(System.lineSeparator()).append(System.lineSeparator());
        });

        return imprimir.toString();
    }

    private String imprimirEstadosDaNatureza(double[][] estados) {
        
        StringBuilder imprimir = new StringBuilder();

        imprimir.append("######").append(" Representação dos estados da Natureza ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        imprimir.append("s").append("\t\t");

        for(int x = 0; x < estados[0].length; x++) {
            
            imprimir.append(" t = ").append(x+1).append(" \t\t");
        }

        imprimir.append(System.lineSeparator());

        for(int x = 0; x < estados.length; x++) {

            imprimir.append(x + 1).append("\t\t");

            for(int y = 0; y < estados[0].length; y++) {

                imprimir.append(Metodos.formatarNumero(estados[x][y])).append("\t\t");
            }

            imprimir.append(System.lineSeparator());
        }

        return imprimir.toString();
    }

}