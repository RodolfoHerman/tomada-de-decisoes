package br.com.rodolfo.trabalho.services;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rodolfo.trabalho.models.Objetivo;

/**
 * ObjetivoService
 */
public class ObjetivoService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ObjetivoService() {}

    public List<Objetivo> buscarInstancias(String caminho) throws JsonParseException, JsonMappingException, IOException {

        return Arrays.asList(objectMapper.readValue(new File(caminho), Objetivo[].class));
    }
}