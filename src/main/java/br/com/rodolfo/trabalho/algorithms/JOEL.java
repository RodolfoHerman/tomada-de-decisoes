package br.com.rodolfo.trabalho.algorithms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import br.com.rodolfo.trabalho.algorithms.sequence.Sobol;
import br.com.rodolfo.trabalho.models.FuncaoObjetivo;
import br.com.rodolfo.trabalho.models.Matriz;
import br.com.rodolfo.trabalho.models.Objetivo;
import br.com.rodolfo.trabalho.models.Restricao;
import br.com.rodolfo.trabalho.models.Solucao;
import br.com.rodolfo.trabalho.utils.Formatadora;
import br.com.rodolfo.trabalho.utils.ImpressaoTipo;
import br.com.rodolfo.trabalho.utils.Metodos;
import it.ssc.pl.milp.GoalType;

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

        int tamanhoParticao = restricoes.get(0).getCoeficientes().length;
        double[][] estadosNatureza = representarEstadosDaNatureza();
        List<GoalType> listaDeTipos = getListaDeTipos();
        List<List<FuncaoObjetivo>> problemasMultiobjetivo = criarProblemasMultiobjetivo(estadosNatureza, tamanhoParticao, listaDeTipos);
        List<Map<String,Double[]>> solucoes = criarSolucoes(problemasMultiobjetivo);
        Map<String,List<FuncaoObjetivo>> mapaFuncoes = extrairFuncoesObjetivos(problemasMultiobjetivo);
        List<Double[]> solucoesExtraidas = extrairSolucoes(solucoes);
        List<Matriz> listaMatrizes = mapaFuncoes.entrySet().stream().map(entry -> {
            return new Matriz(entry.getKey(), entry.getValue(), solucoesExtraidas);
        }).collect(Collectors.toList());

        imprimir.append(Formatadora.getTexto(restricoes, ImpressaoTipo.RESTRICAO));
        imprimir.append(Formatadora.getTexto(objetivos, ImpressaoTipo.OBJETIVOS));
        imprimir.append(Formatadora.getTexto(estadosNatureza, ImpressaoTipo.ESTADOS_NATUREZA));
        imprimir.append(Formatadora.getTexto(problemasMultiobjetivo, ImpressaoTipo.PROBLEMAS_MULTIOBJETIVO));
        imprimir.append(Formatadora.getTexto(solucoes, ImpressaoTipo.SOLUCOES));
        imprimir.append(Formatadora.getTexto(listaMatrizes, ImpressaoTipo.PAYOFF));
        imprimir.append(Formatadora.getTexto(listaMatrizes, ImpressaoTipo.CRITERIOS_ESCOLHA));


        return imprimir.toString();
    }

    private List<Double[]> extrairSolucoes(List<Map<String,Double[]>> solucoes) {

        return solucoes.stream().flatMap(elemento ->
            elemento.entrySet().stream().map(Map.Entry::getValue)
        ).collect(Collectors.toList());
    }

    private Map<String,List<FuncaoObjetivo>> extrairFuncoesObjetivos(List<List<FuncaoObjetivo>> problemasMultiobjetivo) {
        
        Map<String,List<FuncaoObjetivo>> mapaObjetivos = new HashMap<>();

        for(List<FuncaoObjetivo> lista : problemasMultiobjetivo) {

            for(int x = 0; x < lista.size(); x++) {

                List<FuncaoObjetivo> temp = mapaObjetivos.remove(objetivos.get(x).getNome());

                if(temp == null) {

                    temp = new ArrayList<>();
                }

                temp.add(lista.get(x));

                mapaObjetivos.put(objetivos.get(x).getNome(), temp);
            }
        }

        return mapaObjetivos;
    }

    private List<Map<String,Double[]>> criarSolucoes(List<List<FuncaoObjetivo>> problemasMultiobjetivo) {
        
        Solucao solucao = new Solucao(this.restricoes);

        return problemasMultiobjetivo.stream().map(problemas -> {

            try {

                return solucao.getSolucao(problemas);

            } catch (Exception e) {
                
                return null;
            }
        }).collect(Collectors.toList());
    }

    private List<List<FuncaoObjetivo>> criarProblemasMultiobjetivo(double[][] estadosDaNatureza, int tamanhoParticao, List<GoalType> listaDeTipos) {
        
        AtomicInteger contador = new AtomicInteger(1);
        List<List<Double[]>> listaArrayAgrupado = Metodos.transformarArray2DParaListaArrayAgrupada(estadosDaNatureza, tamanhoParticao);
        List<List<FuncaoObjetivo>> funcoesObjetivo = listaArrayAgrupado.stream().map(elemento -> {

                List<FuncaoObjetivo> array = new ArrayList<>();

                for(int x = 0; x < listaDeTipos.size(); x++) {

                    String descricao = "(" + contador.getAndIncrement() + ")";
                    
                    FuncaoObjetivo fObjetivo = new FuncaoObjetivo(descricao, elemento.get(x), listaDeTipos.get(x));

                    array.add(fObjetivo);
                }

                return array;
            }
        // // ).flatMap(List::stream)
        // ).flatMap(lista -> lista.stream())
        // .collect(Collectors.groupingBy(it -> contador.getAndIncrement()/listaDeTipos.size()))
        // .values()
        // .stream()
        ).collect(Collectors.toList());

        return funcoesObjetivo;
    }

    private double[][] representarEstadosDaNatureza() {
        
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

    private List<GoalType> getListaDeTipos() {

        return objetivos.stream().map(objetivo -> objetivo.getMinMaxTipo()).collect(Collectors.toList());
    }

}