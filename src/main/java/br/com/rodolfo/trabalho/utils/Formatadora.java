package br.com.rodolfo.trabalho.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.rodolfo.trabalho.models.Restricao;

/**
 * Formatadora
 * Código adaptado de:
 * https://stackoverflow.com/questions/52319352/formatting-a-string-in-a-textarea-javafx-using-escape-sequences
 */
public class Formatadora {
    
    private Formatadora() {}

    
    public static String getTexto(String descricao, List<Restricao> restricoes) {
        
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
    
    
    public static String getTexto(String descricao) {

        List<List<String>> fakeData = new ArrayList<>();

        StringBuilder texto = new StringBuilder(criarDescricao(descricao));

        String data = "deptName chairID collegeID deptID\n"
                + "Biology 111221118 SC BIOL\n"
                + "Chemistry 111221119 SC CHEM\n"
                + "Computer_Science 111221115 SC CS\n"
                + "Mathematics 111221116 SC MATH";

        for (String line : Arrays.asList(data.split("\n"))) {
            fakeData.add(Arrays.asList(line.split(" ")));
        }

        return texto.append(criarTabulacao(fakeData)).toString();
    }

    private static String criarDescricao(String descricao) {
        
        if(descricao == null || descricao.equals("")) {

            return "";
        }

        return "############  ".concat(descricao).concat("  ############").concat(System.lineSeparator()).concat(System.lineSeparator());
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
        
        return maiorTamanho - entrada.length() + 4;
    }

}