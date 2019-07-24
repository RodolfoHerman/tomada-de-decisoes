package br.com.rodolfo.trabalho.models;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.rodolfo.trabalho.utils.Metodos;
import it.ssc.pl.milp.GoalType;

/**
 * FuncaoObjetivo
 */
public class FuncaoObjetivo {

    private final String descricao;
    private final Double[] coeficientes;
    private final GoalType tipo;
    public List<Restricao> restricoes;

    public FuncaoObjetivo(String descricao, Double[] coeficientes, GoalType tipo) {

        this.descricao = descricao;
        this.coeficientes = coeficientes;
        this.tipo = tipo;
    }

    public String getDescricao() {
        
        return this.descricao;
    }

    public double[] getCoeficientes() {

        return Stream.of(coeficientes).mapToDouble(valor -> valor.doubleValue()).toArray();
    }

    public GoalType getTipo() {

        return this.tipo;
    }

    public double resultado(Double[] solucao) {
        
        AtomicInteger posicao = new AtomicInteger(0);

        return Stream.of(coeficientes).mapToDouble(valor -> valor.doubleValue() * solucao[posicao.getAndIncrement()].doubleValue()).sum();
    }
    
    @Override
    public String toString() {

        AtomicInteger contador = new AtomicInteger(1);
        
        String textual = Stream.of(coeficientes)
            .map(coeficiente -> coeficiente + "*X" + contador.getAndIncrement())
            .collect(Collectors.joining(" + "));

        return textual.concat("  ---> ").concat(Metodos.getMinMaxNominal(tipo));
    }
    
}