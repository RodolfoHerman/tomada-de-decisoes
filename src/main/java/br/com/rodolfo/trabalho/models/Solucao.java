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

    public Map<String,Double[]> getSolucao(List<FuncaoObjetivo> funcoes) throws Exception {

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