package br.com.rodolfo.trabalho.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.rodolfo.trabalho.algorithms.HeuristicaRelogio;
import it.ssc.pl.milp.ConsType;

/**
 * SolucaoDTO
 */
public class Solucao {
    
    private final double[] LIMITES;
    private final double PASSOS;

    public Solucao(List<Restricao> restricoes, double passos) {

        this.LIMITES = restricoes.stream().filter(restricao -> !restricao.transformarSinal().equals(ConsType.EQ)).mapToDouble(restricao -> restricao.getValor()).toArray();
        this.PASSOS  = passos;
    }
    
    public Map<String,Double[]> getSolucao(List<FuncaoObjetivo> funcoes) throws Exception {

        double muD = -1;
        Double[] coeficientes = null;
        Map<String,Double[]> resposta = new HashMap<>();
        
        for(FuncaoObjetivo funcao : funcoes) {

            funcao.resolverSimplex();
            System.out.println(funcao.simplexResolucao());
        }

        for(FuncaoObjetivo funcao : funcoes) {

            Double[] posicaoInicial = funcao.getX_max();
            Double[] valores = HeuristicaRelogio.executar(funcoes, posicaoInicial, this.LIMITES, this.PASSOS);

            Double temp = getMuD(funcoes, valores, true);

            if(temp > muD) {

                muD = temp;
                coeficientes = valores;
            }
        }

        resposta.put(criarRespostaTextual(funcoes), coeficientes);

        return resposta;
    }

    private Double getMuD(List<FuncaoObjetivo> funcoes, Double[] coeficientesX, boolean max) {
        
        List<Double> muD = new ArrayList<>();

        for(FuncaoObjetivo funcao : funcoes) {

            muD.add(funcao.calcularMu(coeficientesX));
        }

        return max ? muD.stream().min(Double::compareTo).orElse(0.0) : muD.stream().max(Double::compareTo).orElse(1.0);
    }

    private String criarRespostaTextual(List<FuncaoObjetivo> funcoes) {
        
        return funcoes.stream().map(funcao -> funcao.getDescricao()).collect(Collectors.joining(" e ", " para ", "."));
    }
}