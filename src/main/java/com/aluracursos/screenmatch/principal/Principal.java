package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Principal {

    private Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private static final String URL = "http://www.omdbapi.com/?apikey=d082f63f&t=";

    public void muestraMenu(){
        System.out.println("Por favor elige el nombre de la serie que desea buscar:");
        var nombreSerie = scanner.nextLine();
        var json = consumoAPI.obtenerDatos(URL + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8));
        var datos = convierteDatos.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);
        String newUrl = URL + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8);
        List<DatosTemporada> datosTemporadaList = new ArrayList<>();
        for(int i = 1; i <= datos.totalTemporadas(); i++){
            json = consumoAPI.obtenerDatos(newUrl+"&Season="+i);
            System.out.println(json);
            DatosTemporada datosTemporada = convierteDatos.obtenerDatos(json, DatosTemporada.class);
            datosTemporadaList.add(datosTemporada);
        }
        datosTemporadaList.forEach(t -> t.episodios().forEach(e -> System.out.println(e.Titulo())));

        System.out.println("Top 5 episodios");

        List<DatosEpisodio> datosEpisodios = datosTemporadaList.stream().flatMap(t -> t.episodios().stream()).collect(Collectors.toList());
        datosEpisodios.stream().filter(e -> !e.evaluacion().equalsIgnoreCase("N/A")).sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed()).limit(5).forEach(System.out::println);

        List<Episodio> episodioList = datosTemporadaList.stream().flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d))).collect(Collectors.toList());
        episodioList.forEach(System.out::println);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        System.out.println("Indica el año desde el que quieres ver los episodios");
        var fecha = scanner.nextInt();
        scanner.nextLine();
        LocalDate localDate = LocalDate.of(fecha, 1 ,1);
        episodioList.stream()
                .filter(episodio -> episodio.getFechaLanzamiento() != null && episodio.getFechaLanzamiento().isAfter(localDate))
                .forEach(episodio -> System.out.println("Temporada: " + episodio.getTemporada() + " Episodioo: " + episodio.getNumeroEpisodio() + " Fecha de Lanzamiento: " + dtf.format(episodio.getFechaLanzamiento())));

        System.out.println("Escriba el nombre del titulo o una parte de el:");
        var searchTitle = scanner.nextLine();
        Optional<Episodio> episodioOptional = episodioList.stream()
                .filter(episodio -> episodio.getTitulo().toUpperCase().contains(searchTitle.toUpperCase()))
                .findFirst();
        if(episodioOptional.isPresent()){
            System.out.println("Los datos del episodio son: " + episodioOptional.get());
        }else{
            System.out.println("Episodio no encontrado.");
        }

        Map<Integer, Double> evaluacionesTemporada = episodioList.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionesTemporada);

        DoubleSummaryStatistics dss = episodioList.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println(dss);
    }
}
