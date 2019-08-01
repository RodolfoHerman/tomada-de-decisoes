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

    // SIMPLEX
    private double z_min;
    private double z_max;
    private Double[] x_min;
    private Double[] x_max;

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

    public double getZ_min() {
        return this.z_min;
    }

    public void setZ_min(double z_min) {
        this.z_min = z_min;
    }

    public double getZ_max() {
        return this.z_max;
    }

    public void setZ_max(double z_max) {
        this.z_max = z_max;
    }

    public Double[] getX_min() {
        return this.x_min;
    }

    public void setX_min(Double[] x_min) {
        this.x_min = x_min;
    }

    public Double[] getX_max() {
        return this.x_max;
    }

    public void setX_max(Double[] x_max) {
        this.x_max = x_max;
    }

    public Double calcularMu(Double[] coeficientesX) {
        
        double divisor = (this.z_max - this.z_min);

        if(this.tipo.equals(GoalType.MIN)) {

            return Metodos.trucarNumero((this.z_max - resultado(coeficientesX))/divisor);
        }

        return Metodos.trucarNumero((resultado(coeficientesX) - this.z_min)/divisor);
    }
    
    @Override
    public String toString() {

        AtomicInteger contador = new AtomicInteger(1);
        
        String textual = Stream.of(coeficientes)
            .map(coeficiente -> Metodos.formatarNumero(coeficiente.doubleValue()) + "*X" + contador.getAndIncrement())
            .collect(Collectors.joining(" + "));

        return textual.concat("  ---> ").concat(Metodos.getMinMaxNominal(tipo));
    }

    public String simplexResolucao() {
        
        String min = "min = [" + Stream.of(x_min).map(coeficiente -> coeficiente.toString()).collect(Collectors.joining(", ")) + "] = " + z_min; 
        String max = "max = [" + Stream.of(x_max).map(coeficiente -> coeficiente.toString()).collect(Collectors.joining(", ")) + "] = " + z_max;

        return String.join(System.lineSeparator(), min, max);
    }
    
}