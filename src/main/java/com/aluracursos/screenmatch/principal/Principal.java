package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
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
    private List<DatosSerie> datosSerieList = new ArrayList<>();
    private SerieRepository serieRepository;
    private List<Serie> serieList = new ArrayList<>();

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void muestraMenu() {
        int opcion = 0;
        do {
            System.out.println("""
                Elige una opción del menú.
                1.- Buscar Series
                2.- Buscar Episodios
                3.- Mostrar Series buscadas
                4.- Buscar Serie por Titulo
                5.- Top 5 de Series
                6.- Buscar Serie por Categoría
                7.- Buscar por cantidad de temporadas y evaluación
                8.- Buscar episodio por nombre
                9.- Top 5 Episodios de serie
                                
                0.- Salir.
                """);
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
            }catch(Exception e) {
                System.out.println("Valor no válido");
                continue;
            }
            switch (opcion) {
                case 1: {
                    buscarSerieWeb();
                    break;
                }
                case 2: {
                    buscarEpisodioPorSerie();
                    break;
                }
                case 3: {
                    mostrarSeriesBuscadas();
                    break;
                }
                case 4:{
                    buscarSeriePorTitulo();
                    break;
                }
                case 5:{
                    mostrarTop5Serie();
                    break;
                }
                case 6:{
                    buscarPorCategoria();
                    break;
                }
                case 7:{
                    buscarPorTemporadaEvaluacion();
                    break;
                }
                case 8:{
                    buscarEpisodioPorNombre();
                    break;
                }
                case 9:{
                    mostrarTop5EpisodioSerie();
                    break;
                }
                default: {
                    System.out.println("Opción invalida.");
                }
            }
        }while(opcion != 0);
    }
    private void mostrarTop5EpisodioSerie(){
        System.out.println("Escribe el nombre de la serie que deseas buscar.");
        String nombreSerie = scanner.nextLine();
        Optional<Serie> serieOptional = serieRepository.findByTituloContainsIgnoreCase(nombreSerie);
        if(serieOptional.isPresent()){
            Serie serie = serieOptional.get();
            List<Episodio> episodioList = serieRepository.top5Episodio(serie);
            episodioList.forEach(e ->
                    System.out.printf("Serie: %s, Nombre Episodio: %s, Numero: %s, Temporada: %s, Evaluación: %s\n",
                            e.getSerie().getTitulo(), e.getTitulo(), e.getNumeroEpisodio(), e.getTemporada(), e.getEvaluacion()));
        }else{
            System.out.println("No se encontró esa serie en la base de datos");
        }
    }
    private void buscarEpisodioPorNombre(){
        System.out.println("¿Cual es el nombre del episodio que deseas buscar?");
        String nombreEpisodio = scanner.nextLine();
        List<Episodio> episodioList = serieRepository.buscarSeriePorNombreEpisodio(nombreEpisodio);
        episodioList.forEach(e ->
                System.out.printf("Serie: %s, Nombre Episodio: %s, Numero: %s, Temporada: %s, Evaluación: %s\n",
                        e.getSerie().getTitulo(), e.getTitulo(), e.getNumeroEpisodio(), e.getTemporada(), e.getEvaluacion()));
    }

    private void buscarPorTemporadaEvaluacion(){
        System.out.println("¿Cuanto es el máximo de temporadas que deseas?");
        int temporadas = scanner.nextInt();
        scanner.nextLine();
        System.out.println("¿Con cuanta evaluación mínima debe cumplir?");
        double evaluacion = scanner.nextDouble();
        List<Serie> serieList1 = serieRepository.buscarSeriePorTemporadayEvaluacion(temporadas, evaluacion);
        System.out.println("Las series que cumplen con " + temporadas + " temporadas y una evaluación mínima de " + evaluacion + " son:");
        serieList1.forEach(s -> System.out.println(s.getTitulo() + " -> " + s.getEvaluacion()));
    }

    private void buscarPorCategoria(){
        System.out.println("Escriba el genero/categoría de la serie que desea buscar");
        String genero = scanner.nextLine();
        Categoria categoria = Categoria.fromEspaniol(genero);
        List<Serie> serieListCategoria = serieRepository.findByGenero(categoria);
        System.out.println("Las series por categoría: " + genero);
        serieListCategoria.forEach(System.out::println);
    }

    private void mostrarTop5Serie(){
        List<Serie> topSeries = serieRepository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + " evaluado: " + s.getEvaluacion()));
    }

    private void buscarSeriePorTitulo(){
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie que desea buscar");
        String nombreSerie = scanner.nextLine();
        Optional<Serie> serieOptional = serieRepository.findByTituloContainsIgnoreCase(nombreSerie);
        if(serieOptional.isPresent()){
            System.out.println("La serie buscada es: " + serieOptional.get());
        }else {
            System.out.println("Serie no encontrada.");
        }
    }

    private void mostrarSeriesBuscadas() {
        serieList = serieRepository.findAll();
        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        String newUrl = getSerieUrl();
        DatosSerie datosSerie = getDatosSerie(newUrl);
        Serie serie = new Serie(datosSerie);
        serieRepository.save(serie);
//        datosSerieList.add(datosSerie);
        System.out.println(datosSerie);
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cual quieres ver los episodios");
        String nombreSerie = scanner.nextLine();
        Optional<Serie> serieOptional = serieList.stream().filter(s->s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase())).findFirst();
        if(serieOptional.isPresent()){
            Serie serie = serieOptional.get();
            List<DatosTemporada> datosTemporadaList = new ArrayList<>();
            for(int i = 1; i <= serie.getTotalTemporadas(); i++){
                DatosTemporada datosTemporada = getDatosTemporada(URL+URLEncoder.encode(serie.getTitulo(), StandardCharsets.UTF_8)+"&Season="+i);
                datosTemporadaList.add(datosTemporada);
            }
            datosTemporadaList.forEach(System.out::println);
            List<Episodio> episodioList = datosTemporadaList.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numero(), e)))
                    .toList();
            serie.setEpisodioList(episodioList);
            serieRepository.save(serie);
        }
    }

    private String getSerieUrl() {
        System.out.println("Por favor elige el nombre de la serie que desea buscar:");
        String nombreSerie = scanner.nextLine();
        return URL + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8);
    }

    private DatosSerie getDatosSerie(String url) {
        String json = consumoAPI.obtenerDatos(url);
        return convierteDatos.obtenerDatos(json, DatosSerie.class);
    }

    private DatosTemporada getDatosTemporada(String url) {
        String json = consumoAPI.obtenerDatos(url);
        return convierteDatos.obtenerDatos(json, DatosTemporada.class);
    }



//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//        System.out.println("Indica el año desde el que quieres ver los episodios");
//        var fecha = scanner.nextInt();
//        scanner.nextLine();
//        LocalDate localDate = LocalDate.of(fecha, 1 ,1);
//        episodioList.stream()
//                .filter(episodio -> episodio.getFechaLanzamiento() != null && episodio.getFechaLanzamiento().isAfter(localDate))
//                .forEach(episodio -> System.out.println("Temporada: " + episodio.getTemporada() + " Episodioo: " + episodio.getNumeroEpisodio() + " Fecha de Lanzamiento: " + dtf.format(episodio.getFechaLanzamiento())));
//
//        System.out.println("Escriba el nombre del titulo o una parte de el:");
//        var searchTitle = scanner.nextLine();
//        Optional<Episodio> episodioOptional = episodioList.stream()
//                .filter(episodio -> episodio.getTitulo().toUpperCase().contains(searchTitle.toUpperCase()))
//                .findFirst();
//        if(episodioOptional.isPresent()){
//            System.out.println("Los datos del episodio son: " + episodioOptional.get());
//        }else{
//            System.out.println("Episodio no encontrado.");
//        }
//
//        Map<Integer, Double> evaluacionesTemporada = episodioList.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getEvaluacion)));
//        System.out.println(evaluacionesTemporada);
//
//        DoubleSummaryStatistics dss = episodioList.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//        System.out.println(dss);
//    }
}
