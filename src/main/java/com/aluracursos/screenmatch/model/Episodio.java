package com.aluracursos.screenmatch.model;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.zip.DataFormatException;

public class Episodio {
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double evaluacion;
    private LocalDate fechaLanzamiento;

    public Episodio(Integer numero, DatosEpisodio datosEpisodio) {
        temporada = numero;
        titulo = datosEpisodio.Titulo();
        numeroEpisodio = datosEpisodio.numeroEpisodio();
        try{
            evaluacion = Double.parseDouble(datosEpisodio.evaluacion());
        }catch(NumberFormatException e){
            evaluacion = 0.0D;
        }
        try {
            fechaLanzamiento = LocalDate.parse(datosEpisodio.fechaLanzamiento());
        }catch(DateTimeParseException e){
            fechaLanzamiento = null;
        }
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    @Override
    public String toString() {
        return  "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", evaluacion=" + evaluacion +
                ", fechaLanzamiento=" + fechaLanzamiento;
    }
}
