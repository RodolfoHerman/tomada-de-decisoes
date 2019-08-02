package br.com.rodolfo.trabalho.algorithms;

import java.util.ArrayList;
import java.util.List;

import br.com.rodolfo.trabalho.models.FuncaoObjetivo;

/**
 * HeuristicaRelogio
 */
public class HeuristicaRelogio {

    private HeuristicaRelogio() {}

    private static boolean isSubtrair(double posicao, double limite, double passo) {
            
        return (posicao - passo) >= 0;
    }

    private static boolean isAdicionar(double posicao, double limite, double passo) {
        
        return (posicao + passo) <= limite;
    }

    public static Double getMuD_max(List<FuncaoObjetivo> funcoes, Double[] coeficientesX) {
        
        List<Double> muD = new ArrayList<>();

        for(FuncaoObjetivo funcao : funcoes) {

            muD.add(funcao.calcularMu(coeficientesX));
        }

        return muD.stream().min(Double::compareTo).orElse(0.0);
    }

    private static boolean isPassoMelhor(List<FuncaoObjetivo> funcoes, Double[] posicaoOrigem, Double[] posicaoDestino) {
        
        return getMuD_max(funcoes, posicaoDestino) > getMuD_max(funcoes, posicaoOrigem) ? true : false;
    }

    private static Double[] getCaminha(Double[] posicaoOrigem, double passo, int i, int j) {
        
        Double[] posicaoDestino = posicaoOrigem.clone(); 

        posicaoDestino[i] -= passo;
        posicaoDestino[j] += passo;

        return posicaoDestino;
    }

    public static Double[] executar(List<FuncaoObjetivo> funcoes, Double[] posicaoInicial, double[] limites, double passo) {
    
        Double[] posicaoAtual = posicaoInicial.clone();
        int qtdVars = posicaoAtual.length;
        int i = 0;

        while (i < qtdVars) {
            
            int j = 0;

            while(j < qtdVars) {

                if(i != j) {
                    
                    if(isSubtrair(posicaoAtual[i], limites[i], passo)) {

                        if(isAdicionar(posicaoAtual[j], limites[j], passo)) {

                            if(isPassoMelhor(funcoes, posicaoAtual, getCaminha(posicaoAtual, passo, i, j))) {

                                posicaoAtual = getCaminha(posicaoAtual, passo, i, j);
                            } else {

                                j++;
                            }
                        } else {

                            j++;
                        }
                    } else {

                        i++;
                        break;
                    }
                } else {

                    j++;
                }
            }

            i++;
        }

        return posicaoAtual;
    }
    
}