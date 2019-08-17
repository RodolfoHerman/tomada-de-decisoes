package br.com.rodolfo.trabalho.algorithms;

import java.util.List;

import br.com.rodolfo.trabalho.models.FuncaoObjetivo;
import br.com.rodolfo.trabalho.utils.Metodos;

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

    private static boolean isPassoMelhor(List<FuncaoObjetivo> funcoes, Double[] posicaoOrigem, Double[] posicaoDestino) {
        
        return Metodos.getMu_D(funcoes, posicaoDestino, true) > Metodos.getMu_D(funcoes, posicaoOrigem, true);
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