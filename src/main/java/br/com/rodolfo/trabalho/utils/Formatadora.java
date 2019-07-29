package br.com.rodolfo.trabalho.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import br.com.rodolfo.trabalho.models.FuncaoObjetivo;
import br.com.rodolfo.trabalho.models.Matriz;
import br.com.rodolfo.trabalho.models.Objetivo;
import br.com.rodolfo.trabalho.models.Projeto;
import br.com.rodolfo.trabalho.models.Restricao;

/**
 * Formatadora
 * Código adaptado de:
 * https://stackoverflow.com/questions/52319352/formatting-a-string-in-a-textarea-javafx-using-escape-sequences
 */
public class Formatadora {
    
    private Formatadora() {}

    public static String getTexto(Object object, ImpressaoTipo tipo) {
        
        StringBuilder resp = new StringBuilder("");
        
        switch (tipo) {
            
            case RESTRICAO:

                List<Restricao> restricoes = (List<Restricao>) object;
                resp.append(getTextoRestricao(restricoes, "Restrições."));
                
            break;

            case OBJETIVOS:

                List<Objetivo> objetivos = (List<Objetivo>) object;

                resp.append(criarDescricao("Informações iniciais para construção das funções objetivo (intervalos dos coeficientes)."));
                resp.append(objetivos.stream().map(obj -> getTextoObjetivo(obj)).collect(Collectors.joining(System.lineSeparator())));

            break;

            case ESTADOS_NATUREZA:

                double[][] estados = (double[][]) object;
                resp.append(getTextoMatriz("Representação dos estados da Natureza.", "s", "t=", "", estados));

            break;

            case PROBLEMAS_MULTIOBJETIVO:

                List<List<FuncaoObjetivo>> funcoes = (List<List<FuncaoObjetivo>>) object;
                resp.append(getTextoFuncao("Problemas Multiobjetivo.", funcoes));

            break;

            case SOLUCOES:

                List<Map<String,Double[]>> solucoes = (List<Map<String,Double[]>>) object;
                resp.append(getTextoSolucao("Possíveis soluções.", solucoes));
                
            break;

            case PAYOFF:

                List<Matriz> listaMatrizesPayoff = (List<Matriz>) object;
                resp.append(criarDescricao("Matrizes payoff."));
                resp.append(listaMatrizesPayoff.stream().map(matriz -> 
                    "- Payoff para : ".concat(matriz.getDescricao()).concat(System.lineSeparator()).concat(System.lineSeparator()).concat(getTextoMatriz("", "", "Y", "X", matriz.getPayoff()))
                ).collect(Collectors.joining(System.lineSeparator())));

            break;

            case CRITERIOS_ESCOLHA:

                List<Matriz> listaMatrizesCriterios = (List<Matriz>) object;
                resp.append(criarDescricao("Matrizes com critérios de escolha."));
                resp.append(listaMatrizesCriterios.stream().map(matriz -> 
                    getTextoCriterios(matriz.getDescricao(), "- Critérios de escolha para : ", matriz.getMatrizCriteriosDeEscolha(), matriz.getMinMaxCriterios())
                ).collect(Collectors.joining(System.lineSeparator())));

            break;

            case CRITERIOS_ESCOLHA_MOD:

                List<Matriz> listaMatrizesCriteriosMod = (List<Matriz>) object;
                resp.append(criarDescricao("Matrizes modificadas com critérios de escolha."));
                resp.append(listaMatrizesCriteriosMod.stream().map(matriz -> 
                    getTextoCriterios(matriz.getDescricao(), "- Critérios de escolha modificados para : ", matriz.getMatrizCriteriosDeEscolhaModificada(), null)
                ).collect(Collectors.joining(System.lineSeparator())));

            break;
        
            default:

                resp.append(criarDescricao("Impressão não disponível para : ")).append(tipo.name());

            break;
        }


        return resp.toString();
    }

    private static String getTextoCriterios(String descricao, String textoInicial, double[][] criterios, double[][] minMaxCriterios) {
        
        List<List<String>> dados = new ArrayList<>();
        StringBuilder texto  = new StringBuilder(textoInicial);

        StringBuilder linhaForma = new StringBuilder();

        texto.append(descricao).append(System.lineSeparator()).append(System.lineSeparator());

        linhaForma.append(" ").append("WALD").append(" ").append("LAPLACE").append(" ").append("SAVEGE").append(" ").append("HURWICZ").append(System.lineSeparator());

        for(int x = 0; x < criterios.length; x++) {

            linhaForma.append("X").append(x + 1).append(" ");

            for(int y = 0; y < criterios[0].length; y++) {

                linhaForma.append(Metodos.formatarNumero(criterios[x][y])).append(" ");
            }

            linhaForma.append(System.lineSeparator());
        }

        if(minMaxCriterios != null) {

            linhaForma.append(System.lineSeparator()).append("MIN").append(" ");
            
            for(int x = 0; x < minMaxCriterios.length; x++) {
    
                linhaForma.append(Metodos.formatarNumero(minMaxCriterios[x][0])).append(" ");
            }
    
            linhaForma.append(System.lineSeparator()).append("MAX").append(" ");
            
            for(int x = 0; x < minMaxCriterios.length; x++) {
    
                linhaForma.append(Metodos.formatarNumero(minMaxCriterios[x][1])).append(" ");
            }
    
            linhaForma.append(System.lineSeparator());
        }
        
        for (String linha : Arrays.asList(linhaForma.toString().split(System.lineSeparator()))) {
            dados.add(Arrays.asList(linha.split(" ")));
        }

        return texto.append(criarTabulacao(dados)).toString();
    }

    private static String getTextoSolucao(String descricao, List<Map<String,Double[]>> solucoes) {
        
        List<List<String>> dados = new ArrayList<>();
        StringBuilder texto  = new StringBuilder(criarDescricao(descricao));
        AtomicInteger indice = new AtomicInteger(1);
        
        String imprimir = solucoes.stream().map(solucao -> {

            StringBuilder temp1 = new StringBuilder("->");
            temp1.append(" S").append(indice.getAndIncrement()).append(": ");

            temp1.append(solucao.entrySet().stream().map(entry -> {
                
                StringBuilder temp2 = new StringBuilder();

                temp2.append(IntStream.range(0, entry.getValue().length)
                    .boxed()
                    .map(i -> (new StringBuilder("X")).append(i+1).append(" = ").append(Metodos.formatarNumero(entry.getValue()[i])).append(" ").toString())
                    .collect(Collectors.joining(" ")));

                return temp2.append(entry.getKey()).toString();

            }).collect(Collectors.joining("")));
            
            return temp1.toString();
        }).collect(Collectors.joining(System.lineSeparator()));

        for (String linha : Arrays.asList(imprimir.split(System.lineSeparator()))) {
            dados.add(Arrays.asList(linha.split(" ")));
        }

        return texto.append(criarTabulacao(dados)).toString();
    }
    
    private static String getTextoFuncao(String descricao, List<List<FuncaoObjetivo>> funcoes) {
        
        List<List<String>> dados = new ArrayList<>();
        StringBuilder texto = new StringBuilder(criarDescricao(descricao));

        String imprimir = funcoes.stream().map(elemento -> 

            IntStream.range(0, elemento.size()).boxed().map(indice -> 
                
                (new StringBuilder(elemento.get(indice).getDescricao())
                    .append(" ")
                    .append("F")
                    .append(indice+1).append("(X)")
                    .append(" = ")
                    .append(elemento.get(indice).toString())
                )

            ).collect(Collectors.joining(System.lineSeparator()))

        ).map(func -> func.concat(System.lineSeparator()))
        .collect(Collectors.joining(System.lineSeparator()));

        // StringBuilder linhaForma = new StringBuilder();

        // for(List<FuncaoObjetivo> funObjetivos : funcoes) {

        //     for(int x = 0; x < funObjetivos.size(); x++) {

        //         linhaForma.append(funObjetivos.get(x).getDescricao())
        //             .append(" ")
        //             .append("F")
        //             .append(x+1).append("(X)")
        //             .append(" = ")
        //             .append(funObjetivos.get(x).toString())
        //             .append(System.lineSeparator());
        //     }

        //     linhaForma.append(System.lineSeparator());
        // }

        for (String linha : Arrays.asList(imprimir.split(System.lineSeparator()))) {
            dados.add(Arrays.asList(linha.split(" ")));
        }

        return texto.append(criarTabulacao(dados)).toString();
    }

    private static String getTextoMatriz(String descricao, String simbolo, String eixoX, String eixoY, double[][] estados) {
        
        List<List<String>> dados = new ArrayList<>();
        StringBuilder texto = new StringBuilder(criarDescricao(descricao));

        StringBuilder linhaForma = new StringBuilder();

        linhaForma.append(simbolo).append(" ");

        for(int x = 1; x <= estados[0].length; x++) {

            linhaForma.append(eixoX).append(x).append(" ");
        }

        linhaForma.append(System.lineSeparator());

        for(int x = 0; x < estados.length; x++) {

            linhaForma.append(eixoY).append(x + 1).append(" ");

            for(int y = 0; y < estados[0].length; y++) {

                linhaForma.append(Metodos.formatarNumero(estados[x][y])).append(" ");
            }

            linhaForma.append(System.lineSeparator());
        }

        for (String linha : Arrays.asList(linhaForma.toString().split(System.lineSeparator()))) {
            dados.add(Arrays.asList(linha.split(" ")));
        }

        return texto.append(criarTabulacao(dados)).toString();
    }
    
    private static String getTextoObjetivo(Objetivo objetivo) {
        
        List<List<String>> dados = new ArrayList<>();
        StringBuilder texto = new StringBuilder();
        StringBuilder desc  = new StringBuilder();

        desc.append("(").append(objetivo.getNome()).append(")").append(" ").append(objetivo.getMinMaxNominal()).append(System.lineSeparator()).append(System.lineSeparator());

        texto.append("Coef.").append(" ").append("C'").append(" ").append("C''").append(System.lineSeparator());
        
        for(Projeto projeto : objetivo.getProjetos()) {

            texto.append(projeto.getNome()).append(" ")
                 .append(Metodos.formatarNumero(projeto.getLower_c()))
                 .append(" ")
                 .append(Metodos.formatarNumero(projeto.getUpper_c()))
                 .append(System.lineSeparator());
        }

        for (String linha : Arrays.asList(texto.toString().split(System.lineSeparator()))) {
            dados.add(Arrays.asList(linha.split(" ")));
        }

        return desc.append(criarTabulacao(dados)).toString();
    }
    
    private static String getTextoRestricao(List<Restricao> restricoes, String descricao) {
        
        List<List<String>> dados = new ArrayList<>();
        StringBuilder texto = new StringBuilder(criarDescricao(descricao));

        StringBuilder linhaForma = new StringBuilder();
        List<String>  expressao  = new ArrayList<>();

        for(Restricao restricao : restricoes) {

            linhaForma.append("(").append(restricao.getNome()).append(") ");
            double[] valores = restricao.getCoeficientes();

            for(Integer x = 0; x < valores.length; x++) {

                expressao.add(Metodos.formatarNumero(valores[x]).concat("*X").concat(x.toString()));
            }

            linhaForma.append(String.join(" + ", expressao)).append(" ").append(restricao.getSinal()).append(" ").append(restricao.getValor()).append(System.lineSeparator());

            expressao.clear();
        }

        for (String linha : Arrays.asList(linhaForma.toString().split(System.lineSeparator()))) {
            dados.add(Arrays.asList(linha.split(" ")));
        }

        return texto.append(criarTabulacao(dados)).toString();
    }

    private static String criarDescricao(String descricao) {
        
        if(descricao == null || descricao.equals("")) {

            return "";
        }

        return "################  ".concat(descricao).concat("  ################").concat(System.lineSeparator()).concat(System.lineSeparator());
    }

    private static String criarTabulacao(List<List<String>> dados) {
        
        List<Integer> qtdLetrasMaiorPalavraColunas = buscarTamanhoPalavra(dados);
        StringBuilder tabulacao = new StringBuilder();

        for(List<String> linha : dados) {

            for(int x = 0; x < linha.size(); x++) {

                tabulacao.append(linha.get(x) + criarEspacos(getNumeroEspacosNecessarios(qtdLetrasMaiorPalavraColunas.get(x), linha.get(x))));
            }

            tabulacao.append(System.lineSeparator());
        }

        return tabulacao.append(System.lineSeparator()).toString();
    }

    private static List<Integer> buscarTamanhoPalavra(List<List<String>> dados) {
        
        List<Integer> qtdLetrasMaiorPalavraColunas = new ArrayList<>();

        for(int x = 0; x < dados.size(); x++) {
            for(int y = 0; y < dados.get(x).size(); y++) {

                if(x == 0) {

                    qtdLetrasMaiorPalavraColunas.add(dados.get(x).get(y).length());

                } else if(dados.get(x).get(y).length() > qtdLetrasMaiorPalavraColunas.get(y)) {

                    qtdLetrasMaiorPalavraColunas.set(y, dados.get(x).get(y).length());
                }
            }
        }

        return qtdLetrasMaiorPalavraColunas;
    }

    private static String criarEspacos(int qtdEspacos) {
        
        StringBuilder espacos = new StringBuilder();

        for(int x = 0; x < qtdEspacos; x++) {

            espacos.append(" ");
        }

        return espacos.toString();
    }

    private static int getNumeroEspacosNecessarios(int maiorTamanho, String entrada) {
        
        return maiorTamanho - entrada.length() + 2;
    }

}