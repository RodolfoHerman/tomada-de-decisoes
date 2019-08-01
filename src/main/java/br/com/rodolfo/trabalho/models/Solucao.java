package br.com.rodolfo.trabalho.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Solucao(List<Restricao> restricoes) {

        this.restricoesSinais = restricoes.stream().map(restricao -> restricao.transformarSinal()).toArray(ConsType[]::new);
        this.restricoesValores = restricoes.stream().mapToDouble(restricao -> restricao.getValor()).toArray();
        this.restricoesCoeficientes = restricoes.stream().map(restricao -> restricao.getCoeficientes()).toArray(tamanho -> new double[tamanho][]);
    }

    public void solucao(List<FuncaoObjetivo> funcoes) throws Exception {

        for(int x = 0; x < funcoes.size(); x++) {

            // Resolucao MIN
            resolverSimplex(funcoes.get(x), GoalType.MIN);
            // Resolucao MIN
            resolverSimplex(funcoes.get(x), GoalType.MAX);

            System.out.println(funcoes.get(x).simplexResolucao());
        }

        List<List<Double>> muD_min = new ArrayList<>();
        List<List<Double>> muD_max = new ArrayList<>();

        for(FuncaoObjetivo funcX : funcoes) {

            List<Double> f_muD_min = new ArrayList<>();
            List<Double> f_muD_max = new ArrayList<>();

            for(FuncaoObjetivo funcY : funcoes) {

                f_muD_min.add(funcY.calcularMu(funcX.getX_min()));
                f_muD_max.add(funcY.calcularMu(funcX.getX_max()));
            }

            muD_min.add(f_muD_min);
            muD_max.add(f_muD_max);
        }

        // double min = funcoes.stream().mapToDouble(funcao -> funcao.getZ_min()).min().orElse(0.0);
        // double max = funcoes.stream().mapToDouble(funcao -> funcao.getZ_min()).min().orElse(0.0);

        System.out.println(muD_min);
        System.out.println(muD_max);

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

    private ArrayList<Constraint> getRestricoes() throws SimplexException {
        
        ArrayList<Constraint> constraints = new ArrayList<>();

        for(int x = 0; x < restricoesCoeficientes.length; x++) {

            constraints.add(new Constraint(this.restricoesCoeficientes[x], this.restricoesSinais[x], this.restricoesValores[x]));
        }

        return constraints;
    }

    private void resolverSimplex(FuncaoObjetivo funcao, GoalType tipo) throws Exception {
        
        LinearObjectiveFunction linearFunction = new LinearObjectiveFunction(funcao.getCoeficientes(), tipo);
        LP lp = new LP(linearFunction, getRestricoes());
        SolutionType solutionType = lp.resolve();

        if(solutionType == SolutionType.OPTIMUM) { 

            Solution solution = lp.getSolution();

            if(tipo.equals(GoalType.MIN)) {

                funcao.setX_min(getCoeficientes(solution.getVariables()));
                funcao.setZ_min(solution.getOptimumValue());

            } else {

                funcao.setX_max(getCoeficientes(solution.getVariables()));
                funcao.setZ_max(solution.getOptimumValue());
            }

        } else {

            throw new Exception("Erro, solução ótima não encontrada para : ".concat(funcao.toString()));
        }
    }

    private Double[] getCoeficientes(Variable[] variaveis) {
        
        Map<String,Double> mapa = Stream.of(variaveis).collect(Collectors.toMap(Variable::getName, Variable::getValue));

        Double[] resp = new Double[mapa.size()];

        for(int x = 0; x < resp.length; x++) {

            resp[x] = mapa.get("X"+(x+1));
        }

        return resp;
    }

    

}