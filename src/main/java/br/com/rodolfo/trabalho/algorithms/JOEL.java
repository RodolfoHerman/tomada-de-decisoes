package br.com.rodolfo.trabalho.algorithms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import br.com.rodolfo.trabalho.algorithms.sequence.Sobol;
import br.com.rodolfo.trabalho.models.FuncaoObjetivo;
import br.com.rodolfo.trabalho.models.Objetivo;
import br.com.rodolfo.trabalho.models.Projeto;
import br.com.rodolfo.trabalho.models.Restricao;
import br.com.rodolfo.trabalho.models.Solucao;
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
        
        imprimir.append(imprimirRestricoes());
        imprimir.append(System.lineSeparator()).append(System.lineSeparator());
        imprimir.append(imprimirObjetivos());
        imprimir.append(imprimirEstadosDaNatureza(estadosNatureza));
        imprimir.append(System.lineSeparator()).append(System.lineSeparator());
        imprimir.append(imprimirProblemasMultiobjetivo(problemasMultiobjetivo));
        imprimir.append(System.lineSeparator()).append(System.lineSeparator());
        imprimir.append(imprimirSolucoes(solucoes));

        return imprimir.toString();
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

    public double[][] representarEstadosDaNatureza() {
        
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

    private String imprimirRestricoes() {
        
        StringBuilder imprimir = new StringBuilder();
        List<String> temp = new ArrayList<>();

        imprimir.append("######").append(" Restrições ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        this.restricoes.stream().forEach(restricao -> {

            imprimir.append("(").append(restricao.getNome()).append(")").append("\t");
            Integer posicao = 1;
            temp.clear();

            for(Double valor : restricao.getCoeficientes()) {

                temp.add("".concat(valor.toString()).concat("*X").concat(posicao.toString()));
                posicao++;
            }

            imprimir.append(String.join(" + ", temp)).append(" ").append(restricao.getSinal()).append(" ").append(restricao.getValor());
            imprimir.append(System.lineSeparator());

        });

        return imprimir.toString();
    }

    private String imprimirObjetivos() {
        
        StringBuilder imprimir = new StringBuilder();

        imprimir.append("######").append(" Informações iniciais para construção das funções objetivo (intervalos dos coeficientes). ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        this.objetivos.stream().forEach(objetivo -> {

            imprimir.append("(").append(objetivo.getNome()).append(")").append("\t")
                .append(objetivo.getMinMaxNominal())
                .append(System.lineSeparator()).append(System.lineSeparator())
                .append("Coef")
                .append("\t\t")
                .append("C'")
                .append("\t\t\t\t")
                .append("C''")
                .append(System.lineSeparator());

            for(Projeto projeto : objetivo.getProjetos()) {

                imprimir.append(" ").append(projeto.getNome()).append("\t\t").append(Metodos.formatarNumero(projeto.getLower_c()))
                    .append("\t\t\t").append(Metodos.formatarNumero(projeto.getUpper_c()))
                    .append(System.lineSeparator());
            }

            imprimir.append(System.lineSeparator()).append(System.lineSeparator());
        });

        return imprimir.toString();
    }

    private String imprimirEstadosDaNatureza(double[][] estados) {
        
        StringBuilder imprimir = new StringBuilder();

        imprimir.append("######").append(" Representação dos estados da Natureza ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        imprimir.append("s").append("\t\t");

        for(int x = 0; x < estados[0].length; x++) {
            
            imprimir.append(" t = ").append(x+1).append(" \t\t");
        }

        imprimir.append(System.lineSeparator());

        for(int x = 0; x < estados.length; x++) {

            imprimir.append(x + 1).append("\t\t");

            for(int y = 0; y < estados[0].length; y++) {

                imprimir.append(Metodos.formatarNumero(estados[x][y])).append("\t\t");
            }

            imprimir.append(System.lineSeparator());
        }

        return imprimir.toString();
    }

    private String imprimirProblemasMultiobjetivo(List<List<FuncaoObjetivo>> funcoesObjetivo) {
        
        StringBuilder imprimir = new StringBuilder();

        imprimir.append("######").append(" Problemas Multiobjetivo ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        funcoesObjetivo.stream().forEach(funcao -> {
            
            for(int x = 0; x < funcao.size(); x++) {

                imprimir.append(funcao.get(x).getDescricao()).append(" F").append(x+1).append("(X) = ").append(funcao.get(x).toString()).append(System.lineSeparator());
            }

            imprimir.append(System.lineSeparator());
        });

        return imprimir.toString();
    }

    private String imprimirSolucoes(List<Map<String,Double[]>> solucoes) {
        
        StringBuilder imprimir = new StringBuilder();
        AtomicInteger contador = new AtomicInteger(1);

        imprimir.append("######").append(" Possíveis soluções. ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        imprimir.append(solucoes.stream().map(solucao -> {

            StringBuilder temp = new StringBuilder();
            Iterator<Map.Entry<String,Double[]>> entries = solucao.entrySet().iterator();
            Map.Entry<String,Double[]> entry = entries.next();

            temp.append("-> ").append("s = ").append(contador.getAndIncrement()).append(" : \t");
            
            for(int x = 0; x < entry.getValue().length; x++) {

                temp.append("X").append(x+1).append(" = ").append(Metodos.formatarNumero(entry.getValue()[x])).append("  \t");
            }

            temp.append(entry.getKey());

            return temp.toString();

        }).collect(Collectors.joining(System.lineSeparator())));

        imprimir.append(System.lineSeparator());

        return imprimir.toString();
    }

}