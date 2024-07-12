package com.aluracursos.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
@Entity
@Table(name = "SERIES")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double evaluacion;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String sinopsis;
    private String actores;
    private String poster;
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodioList;

    public Serie(){

    }

    public Serie (DatosSerie datosSerie){
        titulo = datosSerie.titulo();
        totalTemporadas = datosSerie.totalTemporadas();
        evaluacion = OptionalDouble.of(Double.parseDouble(datosSerie.evaluacion())).orElse(0);
        sinopsis = datosSerie.sinopsis();
        actores = datosSerie.actores();
        genero = Categoria.fromString(datosSerie.genero().split(",")[0].trim());
        poster = datosSerie.poster();
    }

    @Override
    public String toString() {
        return "titulo='" + titulo + '\'' +
                ", genero=" + genero +
                ", totalTemporadas=" + totalTemporadas +
                ", evaluacion=" + evaluacion +
                ", sinopsis='" + sinopsis + '\'' +
                ", actores='" + actores + '\'' +
                ", poster='" + poster + '\'';
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Episodio> getEpisodioList() {
        return episodioList;
    }

    public void setEpisodioList(List<Episodio> episodioList) {
        episodioList.forEach(e -> e.setSerie(this));
        this.episodioList = episodioList;
    }
}
