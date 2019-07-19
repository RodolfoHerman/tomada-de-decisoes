package br.com.rodolfo.trabalho.models;

import java.util.List;

/**
 * Objetivo
 */
public class Objetivo {

    private String nome;
    private String maximizar;
    private List<Projeto> projetos;

    public Objetivo() {}

    public Objetivo(String nome, String maximizar, List<Projeto> projetos) {
        this.nome = nome;
        this.maximizar = maximizar;
        this.projetos = projetos;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMaximizar() {
        return this.maximizar;
    }

    public void setMaximizar(String maximizar) {
        this.maximizar = maximizar;
    }

    public List<Projeto> getProjetos() {
        return this.projetos;
    }

    public void setProjetos(List<Projeto> projetos) {
        this.projetos = projetos;
    }

    @Override
    public String toString() {
        return "{" +
            " nome='" + getNome() + "'" +
            ", maximizar='" + getMaximizar() + "'" +
            ", projetos='" + getProjetos() + "'" +
            "}";
    }

}