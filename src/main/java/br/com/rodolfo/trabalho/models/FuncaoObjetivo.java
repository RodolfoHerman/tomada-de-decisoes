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

    public Double[] coeficientes;
    public GoalType tipo;
    public List<Restricao> restricoes;

    public String getFuncaoTextual() {

        AtomicInteger contador = new AtomicInteger(1);
        
        String textual = Stream.of(coeficientes)
            .map(coeficiente -> coeficiente + "*X" + contador.getAndIncrement())
            .collect(Collectors.joining(" + "));

        return textual.concat("  ---> ").concat(Metodos.getMinMaxNominal(tipo));
    }
    
}