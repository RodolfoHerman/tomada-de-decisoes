package br.com.rodolfo.trabalho.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.rodolfo.trabalho.utils.Metodos;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.SimplexException;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;

/**
 * FuncaoObjetivo
 */
public class FuncaoObjetivo {

    private final String descricao;
    private final Double[] coeficientes;
    private final GoalType tipo;
    
    // Restrições
    private final ConsType[] rSinais;
    private final double[] rValores;
    private final double[][] rCoeficientes;

    // SIMPLEX
    private double z_min;
    private double z_max;
    private Double[] x_min;
    private Double[] x_max;

    public FuncaoObjetivo(String descricao, Double[] coeficientes, GoalType tipo, List<Restricao> restricoes) {

        this.descricao = descricao;
        this.coeficientes = coeficientes;
        this.tipo = tipo;
        this.rSinais = restricoes.stream().map(restricao -> restricao.transformarSinal()).toArray(ConsType[]::new);
        this.rValores = restricoes.stream().mapToDouble(restricao -> restricao.getValor()).toArray();
        this.rCoeficientes = restricoes.stream().map(restricao -> restricao.getCoeficientes()).toArray(tamanho -> new double[tamanho][]);
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

    public double getZ_min() {
        return this.z_min;
    }

    public double getZ_max() {
        return this.z_max;
    }

    public Double[] getX_min() {
        return this.x_min;
    }

    public Double[] getX_max() {
        return this.x_max;
    }

    public ConsType[] getRSinais() {
        return this.rSinais;
    }

    public double[] getRValores() {
        return this.rValores;
    }

    public double[][] getRCoeficientes() {
        return this.rCoeficientes;
    }

    public double resultado(Double[] solucao) {
        
        AtomicInteger posicao = new AtomicInteger(0);

        return Stream.of(coeficientes).mapToDouble(valor -> valor.doubleValue() * solucao[posicao.getAndIncrement()].doubleValue()).sum();
    }

    public void resolverSimplex() throws Exception {
        
        // Resolver simplex MIN
        resolverSimplex(GoalType.MIN);

        // Resolver simplex MAX
        resolverSimplex(GoalType.MAX);
    }
    
    private void resolverSimplex(GoalType tipo) throws Exception {
        
        LinearObjectiveFunction linear = new LinearObjectiveFunction(getCoeficientes(), tipo);

        LP lp = new LP(linear, criarRestricoes());

        SolutionType solutionType = lp.resolve();

        if(solutionType == SolutionType.OPTIMUM) {

            Solution solution = lp.getSolution();

            if(tipo.equals(GoalType.MIN)) {

                this.x_min = getCoeficientes(solution.getVariables());
                this.z_min = solution.getOptimumValue();

            } else {

                this.x_max = getCoeficientes(solution.getVariables());
                this.z_max = solution.getOptimumValue();
            }

        } else {

            throw new Exception("Erro, solução ótima não encontrada para : ".concat(toString()));
        }
    }

    private Double[] getCoeficientes(Variable[] variaveis) {
        
        Map<String,Double> mapa = Stream.of(variaveis).collect(Collectors.toMap(Variable::getName, Variable::getValue));

        Double[] resp = new Double[mapa.size()];

        for(int x = 0; x < resp.length; x++) {

            resp[x] = mapa.get("X"+(x+1));
        }

        return resp;
    }

    private ArrayList<Constraint> criarRestricoes() throws SimplexException {
        
        ArrayList<Constraint> constraints = new ArrayList<Constraint>();

        for(int x = 0; x < this.rCoeficientes.length; x++) {

            constraints.add(new Constraint(this.rCoeficientes[x], this.rSinais[x], this.rValores[x]));
        }

        return constraints;
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