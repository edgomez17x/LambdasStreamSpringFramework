package com.aluracursos.screenmatch.dto;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import jakarta.persistence.*;

import java.util.List;

public record SerieDTO(
        Long id,
        String titulo,
        Integer totalTemporadas,
        Double evaluacion,
        Categoria genero,
        String sinopsis,
        String actores,
        String poster
) {
}
