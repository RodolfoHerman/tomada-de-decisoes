package br.com.rodolfo.trabalho.models;

import java.util.List;

import it.ssc.pl.milp.GoalType;

/**
 * Objetivo
 */
public class Objetivo {

    private String descricao;
    private String maximizar;
    private List<IntervalosCoeficiente> intervalos_coeficientes;

    public Objetivo() {}

    public Objetivo(String descricao, String maximizar, List<IntervalosCoeficiente> intervalos_coeficientes) {
        this.descricao = descricao;
        this.maximizar = maximizar;
        this.intervalos_coeficientes = intervalos_coeficientes;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMaximizar() {
        return this.maximizar;
    }

    public void setMaximizar(String maximizar) {
        this.maximizar = maximizar;
    }

    public List<IntervalosCoeficiente> getIntervalos_coeficientes() {
        return this.intervalos_coeficientes;
    }

    public void setIntervalos_coeficientes(List<IntervalosCoeficiente> intervalos_coeficientes) {
        this.intervalos_coeficientes = intervalos_coeficientes;
    }

    public String getMinMaxNominal() {

        return this.maximizar.toLowerCase().trim().equals("sim") ? "Maximizar" : "Minimizar";
    }

    public GoalType getMinMaxTipo() {
        
        return this.maximizar.toLowerCase().trim().equals("sim") ? GoalType.MAX : GoalType.MIN;
    }

    @Override
    public String toString() {
        return "{" +
            " descricao='" + getDescricao() + "'" +
            ", maximizar='" + getMaximizar() + "'" +
            ", intervalos_coeficientes='" + getIntervalos_coeficientes() + "'" +
            "}";
    }

}