package br.com.rodolfo.trabalho.models;

import it.ssc.pl.milp.ConsType;

/**
 * Restricao
 */
public class Restricao {

    private String nome;
    private double[] coeficientes;
    private String sinal;
    private double valor;
    
    public Restricao() {}

    public Restricao(String nome, double[] coeficientes, String sinal, double valor) {
        this.nome = nome;
        this.coeficientes = coeficientes;
        this.sinal = sinal;
        this.valor = valor;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double[] getCoeficientes() {
        return this.coeficientes;
    }

    public void setCoeficientes(double[] coeficientes) {
        this.coeficientes = coeficientes;
    }

    public String getSinal() {
        return this.sinal;
    }

    public void setSinal(String sinal) {
        this.sinal = sinal;
    }

    public double getValor() {
        return this.valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public ConsType transformarSinal() {
        
        String formatado = this.sinal.trim();

        if(formatado.equals("=")) {

            return ConsType.EQ;
        }

        if(formatado.equals("<=") || formatado.equals("<")) {

            return ConsType.LE;
        }

        if(formatado.equals(">=") || formatado.equals(">")) {

            return ConsType.GE;
        }

        return ConsType.LE;
    }


    @Override
    public String toString() {
        return "{" +
            " nome='" + getNome() + "'" +
            ", coeficientes='" + getCoeficientes() + "'" +
            ", sinal='" + getSinal() + "'" +
            ", valor='" + getValor() + "'" +
            "}";
    }

}