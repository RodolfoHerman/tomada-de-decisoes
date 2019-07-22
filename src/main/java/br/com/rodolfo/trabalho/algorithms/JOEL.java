package br.com.rodolfo.trabalho.algorithms;

import java.util.ArrayList;
import java.util.List;

import br.com.rodolfo.trabalho.models.Objetivo;
import br.com.rodolfo.trabalho.models.Projeto;
import br.com.rodolfo.trabalho.models.Restricao;
import br.com.rodolfo.trabalho.utils.Metodos;

/**
 * JOEL
 */
public class JOEL implements Execute {

    private final List<Objetivo> objetivos;
    private final List<Restricao> restricoes;

    public JOEL(List<Objetivo> objetivos, List<Restricao> restricoes) {

        this.objetivos  = objetivos;
        this.restricoes = restricoes;
    }

    @Override
    public String execute() {
        return null;
    }

    public String imprimirRestricoes() {
        
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

    public String imprimirObjetivos() {
        
        StringBuilder imprimir = new StringBuilder();

        imprimir.append("######").append(" Informações iniciais para construção das funções objetivo (intervalos dos coeficientes). ").append("######").append(System.lineSeparator()).append(System.lineSeparator());

        this.objetivos.stream().forEach(objetivo -> {

            imprimir.append("(").append(objetivo.getNome()).append(")").append("\t")
                .append(Metodos.getMaxMinNominal(objetivo.getMaximizar()))
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
}