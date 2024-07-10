package com.aluracursos.screenmatch.service;

public interface IConveirteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);
}
