package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.EpisodioDTO;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> obtenerTodasLasSeries(){
        return convertirSerie(serieRepository.findAll());
    }

    public List<SerieDTO> getTop5() {
        return convertirSerie(serieRepository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerLanzamientosRecientes(){
        return convertirSerie(serieRepository.lanzamientosRecientes());
    }

    public SerieDTO obtenerPorId(Long id) {
        Optional<Serie> serieOptional = serieRepository.findById(id);
        return serieOptional.map(this::convertirSerie).orElse(null);
    }

    private SerieDTO convertirSerie(Serie serie){
        return new SerieDTO(
                serie.getId(),
                serie.getTitulo(),
                serie.getTotalTemporadas(),
                serie.getEvaluacion(),
                serie.getGenero(),
                serie.getSinopsis(),
                serie.getActores(),
                serie.getPoster()
                );
    }

    private List<SerieDTO> convertirSerie(List<Serie> serieList){
        return serieList.stream()
                .map(s-> new SerieDTO(
                        s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getEvaluacion(),
                        s.getGenero(),
                        s.getSinopsis(),
                        s.getActores(),
                        s.getPoster()
                )).toList();
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
        Optional<Serie> serieOptional = serieRepository.findById(id);
        if(serieOptional.isPresent()){
            Serie serie = serieOptional.get();
            return convertirEpisodio(serie.getEpisodioList());
        }
        return null;
    }

    private List<EpisodioDTO> convertirEpisodio(List<Episodio> episodioList){
        List<EpisodioDTO> episodioListDTO = episodioList.stream().map(e -> new EpisodioDTO(
                e.getTemporada(),
                e.getTitulo(),
                e.getNumeroEpisodio()
        )).toList();
        return episodioListDTO;
    }

    public List<EpisodioDTO> obtenerTemporadaPorNumero(Long id, Long numeroTemporada) {
        return convertirEpisodio(serieRepository.buscarTemporadaPorNumero(id, numeroTemporada));
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String genero) {
        Categoria categoria = Categoria.fromEspaniol(genero);
        return convertirSerie(serieRepository.findByGenero(categoria));
    }
}
