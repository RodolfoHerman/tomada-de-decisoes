package br.com.rodolfo.trabalho.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
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

    private List<Double> calcularCriterioSavage() {

        List<DoubleSummaryStatistics> sumariosColunas = IntStream.range(0, this.payoff[0].length).boxed().map(x -> 
            Stream.of(this.payoff).map(elementos -> elementos[x])
            .collect(Collectors.summarizingDouble(Double::doubleValue))
        ).collect(Collectors.toList());

        return Stream.of(this.payoff).map(row -> {
            return IntStream.range(0, row.length).boxed().map(x -> {
                
                if(this.tipo == GoalType.MAX) {

                    return (new BigDecimal(sumariosColunas.get(x).getMax() - row[x])).setScale(2, RoundingMode.HALF_UP).doubleValue();

                } else {

                    return (new BigDecimal(row[x] - sumariosColunas.get(x).getMin())).setScale(2, RoundingMode.HALF_UP).doubleValue();    
                }
            }).toArray(Double[]::new);
        }).map(elementos -> Stream.of(elementos).mapToDouble(d -> d.doubleValue()))
        .map(el -> DoubleStream.of(el.toArray()).max().orElse(0.0))
        .collect(Collectors.toList());
    }

    private List<Double> calcularCriterioHurwicz() {
        
        return Stream.of(this.payoff).map(row -> {

            DoubleSummaryStatistics sumario =  DoubleStream.of(row).boxed().collect(Collectors.summarizingDouble(Double::doubleValue));

            return (new BigDecimal(((0.75 * sumario.getMax()) + (0.25 * sumario.getMin())))).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }).collect(Collectors.toList());
    }

}