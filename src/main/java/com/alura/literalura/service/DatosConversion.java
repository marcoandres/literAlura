package com.alura.literalura.service;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DatosConversion implements IDatosConversion{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T convertirDatos(String data, Class<T> classType) {
        try {
            return objectMapper.readValue(data, classType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
