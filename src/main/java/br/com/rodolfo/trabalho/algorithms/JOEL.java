package br.com.rodolfo.trabalho.algorithms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.concurrent.Task;

/**
 * JOEL
 */
public class JOEL extends Task<Void> {

    private final List<Objetivo> objetivos;
    private final List<Restricao> restricoes;
    private final int cenarios;

    public JOEL(int cenarios, List<Objetivo> objetivos, List<Restricao> restricoes) {

        this.cenarios   = cenarios; 
        this.objetivos  = objetivos;
        this.restricoes = restricoes;
    }

    @Override
    protected Void call() throws Exception {
        

        return null;
    }
    
    public String execute() {
        
        StringBuilder imprimir = new StringBuilder();

        int tamanhoParticao = restricoes.get(0).getCoeficientes().length;
        double[][] estadosNatureza = representarEstadosDaNatureza();
        List<GoalType> listaDeTipos = getListaDeTipos();
        List<List<FuncaoObjetivo>> problemasMultiobjetivo = criarProblemasMultiobjetivo(estadosNatureza, tamanhoParticao, listaDeTipos);
        List<Map<String,Double[]>> solucoes = criarSolucoes(problemasMultiobjetivo);
        Map<String,List<FuncaoObjetivo>> mapaFuncoes = extrairFuncoesObjetivos(problemasMultiobjetivo);
        List<Map<String,Double[]>> solucoesUnicas = extrairSolucoesUnicas(solucoes);
        List<Double[]> solucoesExtraidas = extrairSolucoes(solucoesUnicas);
        List<Matriz> listaMatrizes = mapaFuncoes.entrySet().stream().map(entry -> {
            return new Matriz(entry.getKey(), entry.getValue(), solucoesExtraidas);
        }).collect(Collectors.toList());

        imprimir.append(Formatadora.getTexto(restricoes, ImpressaoTipo.RESTRICAO));
        imprimir.append(Formatadora.getTexto(objetivos, ImpressaoTipo.OBJETIVOS));
        imprimir.append(Formatadora.getTexto(estadosNatureza, ImpressaoTipo.ESTADOS_NATUREZA));
        imprimir.append(Formatadora.getTexto(problemasMultiobjetivo, ImpressaoTipo.PROBLEMAS_MULTIOBJETIVO));
        imprimir.append(Formatadora.getTexto(solucoes, ImpressaoTipo.SOLUCOES));
        imprimir.append(Formatadora.getTexto(solucoesUnicas, ImpressaoTipo.SOLUCOES_UNICAS));
        imprimir.append(Formatadora.getTexto(listaMatrizes, ImpressaoTipo.PAYOFF));
        imprimir.append(Formatadora.getTexto(listaMatrizes, ImpressaoTipo.CRITERIOS_ESCOLHA));
        imprimir.append(Formatadora.getTexto(listaMatrizes, ImpressaoTipo.CRITERIOS_ESCOLHA_MOD));
        imprimir.append(Formatadora.getTexto(criarMatrizAgregada(listaMatrizes), ImpressaoTipo.MATRIZ_AGREGADA));


        return imprimir.toString();
    }

    private double[][] criarMatrizAgregada(List<Matriz> listaMatrizes) {
        
        List<double[][]> matrizes = listaMatrizes.stream().map(matriz -> matriz.getMatrizCriteriosDeEscolhaModificada()).collect(Collectors.toList());
        
        int qtdLinhas  = matrizes.get(0).length;
        int qtdColunas = matrizes.get(0)[0].length;
        
        double[][] resposta = criarMatrizNeutra(qtdLinhas, qtdColunas);

        matrizes.stream().forEach(matriz -> {

            for(int x = 0; x < qtdLinhas; x++) {
                for(int y = 0; y < qtdColunas; y++) {

                    resposta[x][y] = resposta[x][y] < matriz[x][y] ? resposta[x][y] : matriz[x][y];
                }
            }

        });

        return resposta;
    }

    private double[][] criarMatrizNeutra(int qtdLinhas, int qtdColunas) {
        
        double[][] neutra = new double[qtdLinhas][qtdColunas];

        for(int x = 0; x < neutra.length; x++) {
            for(int y = 0; y < neutra[0].length; y++) {

                neutra[x][y] = 1.0;
            }
        }

        return neutra;
    }

    
    public List<Map<String,Double[]>> extrairSolucoesUnicas(List<Map<String,Double[]>> solucoes) {
        
        // solucoes.stream().flatMap(elemento ->
        //         elemento.entrySet().stream().map(Map.Entry::getValue)
        //     ).collect(Collectors.toMap(el -> Arrays.hashCode(el), el -> el, (val1, val2) -> val1));
        
        Map<Integer,Map<String,Double[]>> solucoesUnicas = new HashMap<>();

        for(Map<String,Double[]> solucao : solucoes) {

            Double[] t = solucao.entrySet().stream().map(entry -> entry.getValue()).findFirst().get();

            solucoesUnicas.put(Arrays.hashCode(t), solucao);
        }

        return solucoesUnicas.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
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

                List<FuncaoObjetivo> temp = mapaObjetivos.remove(objetivos.get(x).getDescricao());

                if(temp == null) {

                    temp = new ArrayList<>();
                }

                temp.add(lista.get(x));

                mapaObjetivos.put(objetivos.get(x).getDescricao(), temp);
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
                    
                    FuncaoObjetivo fObjetivo = new FuncaoObjetivo(descricao, elemento.get(x), listaDeTipos.get(x), restricoes);

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
            .flatMap(objetivo -> objetivo.getIntervalos_coeficientes()
            .stream()
            .map(intervalos -> new Double[]{intervalos.getLower_c(), intervalos.getUpper_c()}))
            .collect(Collectors.toList());


        // double[][] teste = {{0.06,	0.53,	0.18,	0.18,	0.06,	25,	70,	60,	95,	45,	0,	32.5000000000000,	300,	120,	0,	0.1,	0.1,	0.11,	0.35,	0.33},
        // {0.0590000000000000,	0.529000000000000,	0.176000000000000,	0.176000000000000,	0.0590000000000000,	27.5000000000000,	67.5000000000000,	62.5000000000000,	97.5000000000000,	42.5000000000000,	0,	28.7500000000000,	325,	105,	0,	0.237500000000000,	0.187500000000000,	0.382500000000000,	0.0700000000000000,	0.130000000000000},
        // {0.0590000000000000,	0.529000000000000,	0.176000000000000,	0.176000000000000,	0.0590000000000000,	22.5000000000000,	72.5000000000000,	57.5000000000000,	92.5000000000000,	47.5000000000000,	0,	36.2500000000000,	275,	135,	0,	0.392500000000000,	0.302500000000000,	0.207500000000000,	0.130000000000000,	0.0700000000000000},
        // {0.0590000000000000,	0.529000000000000,	0.176000000000000,	0.176000000000000,	0.0590000000000000,	21.2500000000000,	68.7500000000000,	58.7500000000000,	98.7500000000000,	46.2500000000000,	0,	38.1250000000000,	337.500000000000,	97.5000000000000,	0,	0.276250000000000,	0.331250000000000,	0.338750000000000,	0.0550000000000000,	0.0550000000000000},
        // {0.0590000000000000,	0.529000000000000,	0.176000000000000,	0.176000000000000,	0.0590000000000000,	26.2500000000000,	73.7500000000000,	63.7500000000000,	93.7500000000000,	41.2500000000000,	0,	30.6250000000000,	287.500000000000,	127.500000000000,	0,	0.431250000000000,	0.216250000000000,	0.163750000000000,	0.115000000000000,	0.115000000000000},
        // {0.0590000000000000,	0.529000000000000,	0.176000000000000,	0.176000000000000,	0.0590000000000000,	28.7500000000000,	66.2500000000000,	61.2500000000000,	91.2500000000000,	48.7500000000000,	0,	34.3750000000000,	262.500000000000,	112.500000000000,	0,	0.198750000000000,	0.273750000000000,	0.251250000000000,	0.0850000000000000,	0.145000000000000},
        // {0.0590000000000000,	0.529000000000000,	0.176000000000000,	0.176000000000000,	0.0590000000000000,	23.7500000000000,	71.2500000000000,	56.2500000000000,	96.2500000000000,	43.7500000000000,	0,	26.8750000000000,	312.500000000000,	142.500000000000,	0,	0.353750000000000,	0.158750000000000,	0.426250000000000,	0.145000000000000,	0.0850000000000000}};

        for(int x = 0; x < cenarios; x++) {
            for(int y = 0; y < dimensao; y++) {

                estadosDaNatureza[x][y] = cacularInterpolacao(intervaloCoeficientes.get(y), sobol[x+1][y]);
            }
        }

        return estadosDaNatureza;
        // return teste;
    }

    private int getDimensaoSobol() {

        int qtdFuncoesObjetivo = objetivos.size();
        int qtdCoeficientes    = restricoes.get(0).getCoeficientes().length;

        return qtdFuncoesObjetivo * qtdCoeficientes;
    }

    private double cacularInterpolacao(Double[] intervalo, double valor) {
        
        return ((new BigDecimal(intervalo[0] + ((intervalo[1] - intervalo[0]) * valor)))
            .setScale(4, RoundingMode.HALF_UP))
            .doubleValue();
    }

    private List<GoalType> getListaDeTipos() {

        return objetivos.stream().map(objetivo -> objetivo.getMinMaxTipo()).collect(Collectors.toList());
    }

}