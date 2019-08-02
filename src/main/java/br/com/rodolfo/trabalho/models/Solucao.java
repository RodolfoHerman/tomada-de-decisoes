package br.com.rodolfo.trabalho.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.rodolfo.trabalho.algorithms.HeuristicaRelogio;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.SimplexException;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;

/**
 * SolucaoDTO
 */
public class Solucao {
    
    private ConsType[] restricoesSinais;
    private double[] restricoesValores;
    private double[][] restricoesCoeficientes;
    private double[] limites;

    public Solucao(List<Restricao> restricoes) {

        this.restricoesSinais = restricoes.stream().map(restricao -> restricao.transformarSinal()).toArray(ConsType[]::new);
        this.restricoesValores = restricoes.stream().mapToDouble(restricao -> restricao.getValor()).toArray();
        this.restricoesCoeficientes = restricoes.stream().map(restricao -> restricao.getCoeficientes()).toArray(tamanho -> new double[tamanho][]);
        this.limites = restricoes.stream().filter(restricao -> !restricao.transformarSinal().equals(ConsType.EQ)).mapToDouble(restricao -> restricao.getValor()).toArray();
    }


    private Double getMuD(List<FuncaoObjetivo> funcoes, Double[] coeficientesX, boolean max) {
        
        List<Double> muD = new ArrayList<>();

        for(FuncaoObjetivo funcao : funcoes) {

            muD.add(funcao.calcularMu(coeficientesX));
        }

        return max ? muD.stream().min(Double::compareTo).orElse(0.0) : muD.stream().max(Double::compareTo).orElse(1.0);
    }
    
    public void solucao(List<FuncaoObjetivo> funcoes) throws Exception {

        for(FuncaoObjetivo funcao : funcoes) {

            funcao.resolverSimplex();
            System.out.println(funcao.simplexResolucao());
        }

        System.out.println("LIMITES");
        for(double val : limites) {

            System.out.println(val);
        }
        System.out.println("###########");

        Double[] posicaoInicial = funcoes.get(1).getX_max();
        Double[] valores = HeuristicaRelogio.executar(funcoes, posicaoInicial, limites, 0.5);


        for(Double val : valores) {

            System.out.println(val);
        }

        System.out.println("MU");
        System.out.println(getMuD(funcoes, valores, false));
        System.out.println(getMuD(funcoes, valores, true));

        // List<List<Double>> muD_min = new ArrayList<>();
        // List<List<Double>> muD_max = new ArrayList<>();

        // for(FuncaoObjetivo funcX : funcoes) {

        //     List<Double> f_muD_min = new ArrayList<>();
        //     List<Double> f_muD_max = new ArrayList<>();

        //     for(FuncaoObjetivo funcY : funcoes) {

        //         f_muD_min.add(funcY.calcularMu(funcX.getX_min()));
        //         f_muD_max.add(funcY.calcularMu(funcX.getX_max()));
        //     }

        //     System.out.println(f_muD_min.stream().max(Double::compareTo).orElse(1.0));
        //     System.out.println(f_muD_max.stream().min(Double::compareTo).orElse(0.0));
        //     System.out.println();

        //     muD_min.add(f_muD_min);
        //     muD_max.add(f_muD_max);
        // }

        // double min = funcoes.stream().mapToDouble(funcao -> funcao.getZ_min()).min().orElse(0.0);
        // double max = funcoes.stream().mapToDouble(funcao -> funcao.getZ_min()).min().orElse(0.0);

        // System.out.println(muD_min);
        // System.out.println(muD_max);

    }
    
    
    public Map<String,Double[]> getSolucao(List<FuncaoObjetivo> funcoes) throws Exception {

        solucao(funcoes);
        
        double[] funcaoCoeficientes = funcoes.get(0).getCoeficientes();
        GoalType tipo = funcoes.get(0).getTipo();

        LinearObjectiveFunction linearFunction = new LinearObjectiveFunction(funcaoCoeficientes, tipo);
        ArrayList<Constraint> constraints = new ArrayList<>();

        for(int x = 0; x < restricoesCoeficientes.length; x++) {

            constraints.add(new Constraint(restricoesCoeficientes[x], restricoesSinais[x], restricoesValores[x]));
        }

        LP lp = new LP(linearFunction, constraints);

        SolutionType solutionType = lp.resolve();

        return criarResposta(lp.getSolution(), criarRespostaTextual(funcoes));
    }

    private Map<String,Double[]> criarResposta(Solution solution, String descricao) {
        
        Map<String,Double> mapa = Stream.of(solution.getVariables()).collect(Collectors.toMap(Variable::getName, Variable::getValue));
        Map<String,Double[]> resposta = new HashMap<>();

        Double[] temp = new Double[mapa.size()];

        for(int x = 0; x < temp.length; x++) {

            temp[x] = mapa.get("X"+(x+1));
        }

        resposta.put(descricao, temp);

        return resposta;
    }

    private String criarRespostaTextual(List<FuncaoObjetivo> funcoes) {
        
        return funcoes.stream().map(funcao -> funcao.getDescricao()).collect(Collectors.joining(" e ", " para ", "."));
    }

    

}