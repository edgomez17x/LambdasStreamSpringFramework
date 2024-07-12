package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);
    List<Serie> findTop5ByOrderByEvaluacionDesc();
    List<Serie> findByGenero(Categoria categoria);
    //List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, double evaluacion);
    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion")
    List<Serie> buscarSeriePorTemporadayEvaluacion(int totalTemporadas, double evaluacion);
    @Query("SELECT e FROM Serie s JOIN s.episodioList e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> buscarSeriePorNombreEpisodio(String nombreEpisodio);
    @Query("SELECT e FROM Serie s JOIN s.episodioList e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
    List<Episodio> top5Episodio(Serie serie);
    @Query("SELECT s FROM Serie s JOIN s.episodioList e GROUP BY s ORDER BY MAX(e.fechaLanzamiento) DESC LIMIT 5")
    List<Serie> lanzamientosRecientes();
    @Query("SELECT e FROM Serie s JOIN s.episodioList e WHERE s.id = :id AND e.temporada = :numeroTemporada")
    List<Episodio> buscarTemporadaPorNumero(Long id, Long numeroTemporada);
}
