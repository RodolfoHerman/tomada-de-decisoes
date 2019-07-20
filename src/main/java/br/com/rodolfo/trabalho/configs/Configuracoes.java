package br.com.rodolfo.trabalho.configs;

import java.util.Arrays;
import java.util.List;

/**
 * Configuracoes
 */
public class Configuracoes {

    public String objetivoInstancias;
    public String restricaoInstancias;

    public Configuracoes() {}

    public List<String> arquivosParaLista() {

        return Arrays.asList(objetivoInstancias, restricaoInstancias);
    }
}