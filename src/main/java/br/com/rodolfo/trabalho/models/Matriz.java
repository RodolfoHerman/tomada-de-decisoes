package br.com.rodolfo.trabalho.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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
    private final double hurwicz;
    private final List<FuncaoObjetivo> funcoesObjetivo;
    private final List<Double[]> solucoes;
    private final GoalType tipo;
    private final double[][] payoff;
    private final List<List<Double>> criteriosDeEscolha;

    public Matriz(String descricao, double hurwicz, List<FuncaoObjetivo> funcoesObjetivo, List<Double[]> solucoes) {

        this.descricao = descricao;
        this.hurwicz = hurwicz;
        this.funcoesObjetivo = funcoesObjetivo;
        this.solucoes = solucoes;
        this.tipo = funcoesObjetivo.get(0).getTipo();

        this.payoff = criarPayoff();
        this.criteriosDeEscolha = Arrays.asList(calcularCriterioWald(), calcularCriterioLaplace(), calcularCriterioSavage(), calcularCriterioHurwicz());
    }

    
    public double[][] getMatrizCriteriosDeEscolhaModificada() {
        
        double[][] resposta= new double[this.criteriosDeEscolha.get(0).size()][this.criteriosDeEscolha.size()];
        
        for(int x = 0; x < this.criteriosDeEscolha.size(); x++) {

            double max = this.criteriosDeEscolha.get(x).stream().max(Double::compareTo).orElse(0.0);
            double min = this.criteriosDeEscolha.get(x).stream().min(Double::compareTo).orElse(0.0);

            double divisor = (max - min) == 0.0 ? 1.0 : (max - min);

            List<Double> valores = this.criteriosDeEscolha.get(x);

            if(this.tipo == GoalType.MAX) {

                for(int y = 0; y < valores.size(); y++){

                    resposta[y][x] = (valores.get(y) - min)/divisor;
                }

            } else {

                for(int y = 0; y < valores.size(); y++){

                    resposta[y][x] = (max - valores.get(y))/divisor;
                }
            }
        }

        return resposta;
    }
    
    // public double[][] getMatrizCriteriosDeEscolhaModificada() {
        
    //     double max = this.criteriosDeEscolha.stream().map(elementos -> elementos.stream().mapToDouble(d -> d).max().orElse(0.0)).collect(Collectors.toList()).stream().mapToDouble(d -> d).max().orElse(0.0);
    //     double min = this.criteriosDeEscolha.stream().map(elementos -> elementos.stream().mapToDouble(d -> d).min().orElse(0.0)).collect(Collectors.toList()).stream().mapToDouble(d -> d).min().orElse(0.0);

    //     double divisor = (max - min) == 0.0 ? 1.0 : (max - min);

    //     if(this.tipo == GoalType.MAX) {

    //         return IntStream.range(0, this.criteriosDeEscolha.get(0).size())
    //             .boxed()
    //             .map(indice -> this.criteriosDeEscolha.stream().mapToDouble(ele -> ((ele.get(indice).doubleValue() - min)/divisor)).toArray())
    //             .toArray(double[][]::new);


    //     } else {

    //         return IntStream.range(0, this.criteriosDeEscolha.get(0).size())
    //             .boxed()
    //             .map(indice -> this.criteriosDeEscolha.stream().mapToDouble(ele -> ((max - ele.get(indice).doubleValue())/divisor)).toArray())
    //             .toArray(double[][]::new);
    //     }

    // }

    public double[][] getMinMaxCriterios() {
        
        // Sem transposta do List<List<Double>> criteriosDeEscolha
        return this.criteriosDeEscolha.stream()
            .map(elementos -> elementos.stream().collect(Collectors.summarizingDouble(Double::doubleValue)))
            .map(sumario -> new double[]{sumario.getMin(), sumario.getMax()})
            .toArray(double[][]::new);
    }

    public String getDescricao() {
        
        return this.descricao;
    }

    public double[][] getPayoff() {

        return this.payoff;
    }

    public double[][] getMatrizCriteriosDeEscolha() {

        // Fazer a transposta do List<List<Double>> criteriosDeEscolha
        return IntStream.range(0, this.criteriosDeEscolha.get(0).size())
            .boxed()
            .map(indice -> this.criteriosDeEscolha.stream().mapToDouble(ele -> ele.get(indice).doubleValue()).toArray())
            .toArray(double[][]::new);
            //.toArray(i -> new double[i][]);
            
        // int linhas  = this.criteriosDeEscolha.get(0).size();
        // int colunas = this.criteriosDeEscolha.size();

        // double[][] resp = new double[linhas][colunas];

        // for(int x = 0; x < linhas; x++) {
        //     for(int y = 0; y < colunas; y++) {

        //         resp[x][y] = this.criteriosDeEscolha.get(y).get(x).doubleValue();
        //     }
        // }

        // return criteriosDeEscolha
        //     .stream()
        //     .map(elementos -> elementos.stream().mapToDouble(d -> d).toArray())
        //     .toArray(tamanho -> new double[tamanho][]);
    }

    private double[][] criarPayoff() {
        
        double[][] temp = new double[this.solucoes.size()][this.funcoesObjetivo.size()];

        for(int x = 0; x < temp.length; x++) {

            for(int y = 0; y < temp[0].length; y++) {

                temp[x][y] = this.funcoesObjetivo.get(y).resultado(this.solucoes.get(x));
            }
        }

        return temp;
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

                    return (new BigDecimal(sumariosColunas.get(x).getMax() - row[x])).setScale(4, RoundingMode.HALF_UP).doubleValue();

                } else {

                    return (new BigDecimal(row[x] - sumariosColunas.get(x).getMin())).setScale(4, RoundingMode.HALF_UP).doubleValue();    
                }
            }).toArray(Double[]::new);
        }).map(elementos -> Stream.of(elementos).mapToDouble(d -> d.doubleValue()))
        .map(el -> DoubleStream.of(el.toArray()).max().orElse(0.0))
        .collect(Collectors.toList());
    }

    private List<Double> calcularCriterioHurwicz() {
        
        return Stream.of(this.payoff).map(row -> {

            DoubleSummaryStatistics sumario =  DoubleStream.of(row).boxed().collect(Collectors.summarizingDouble(Double::doubleValue));

            return (new BigDecimal(((this.hurwicz * sumario.getMax()) + ((1.0 - this.hurwicz) * sumario.getMin())))).setScale(4, RoundingMode.HALF_UP).doubleValue();
        }).collect(Collectors.toList());
    }
}