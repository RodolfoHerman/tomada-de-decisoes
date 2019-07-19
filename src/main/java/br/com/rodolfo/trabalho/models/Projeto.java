package br.com.rodolfo.trabalho.models;

/**
 * Projeto
 */
public class Projeto {

    private String nome;
    private double lower_c;
    private double upper_c;

    public Projeto() {}

    public Projeto(String nome, double lower_c, double upper_c) {
        this.nome = nome;
        this.lower_c = lower_c;
        this.upper_c = upper_c;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLower_c() {
        return this.lower_c;
    }

    public void setLower_c(double lower_c) {
        this.lower_c = lower_c;
    }

    public double getUpper_c() {
        return this.upper_c;
    }

    public void setUpper_c(double upper_c) {
        this.upper_c = upper_c;
    }

    @Override
    public String toString() {
        return "{" +
            " nome='" + getNome() + "'" +
            ", lower_c='" + getLower_c() + "'" +
            ", upper_c='" + getUpper_c() + "'" +
            "}";
    }

}