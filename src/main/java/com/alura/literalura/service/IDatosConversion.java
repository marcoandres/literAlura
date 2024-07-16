package com.alura.literalura.service;

public interface IDatosConversion {
    <T> T convertirDatos(String data, Class<T> classType);
}
