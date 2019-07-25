package br.com.rodolfo.trabalho.models;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import it.ssc.pl.milp.GoalType;

/**
 * Matriz
 */
public class Matriz {

    private final String descricao;
    private final List<FuncaoObjetivo> funcoesObjetivo;
    private final List<Double[]> solucoes;
    private final GoalType tipo;
    private double[][] payoff;

    public Matriz(String descricao, List<FuncaoObjetivo> funcoesObjetivo, List<Double[]> solucoes) {

        this.descricao = descricao;
        this.funcoesObjetivo = funcoesObjetivo;
        this.solucoes = solucoes;
        this.tipo = funcoesObjetivo.get(0).getTipo();

        criarPayoff();
    }

    private void criarPayoff() {
        
        this.payoff = new double[solucoes.size()][funcoesObjetivo.size()];

        for(int x = 0; x < this.payoff.length; x++) {

            for(int y = 0; y < this.payoff[0].length; y++) {

                this.payoff[x][y] = funcoesObjetivo.get(y).resultado(solucoes.get(x));
            }
        }
    }

    private List<Double> calcularCriterioWald() {

        return Stream.of(this.payoff).map(row -> DoubleStream.of(row).max().orElse(0.0)).collect(Collectors.toList());
    }

    private List<Double> calcularCriterioLaplace() {

        return Stream.of(this.payoff).map(row -> DoubleStream.of(row).average().orElse(0.0)).collect(Collectors.toList());
    }
}