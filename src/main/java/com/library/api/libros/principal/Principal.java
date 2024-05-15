package com.library.api.libros.principal;

import com.library.api.libros.Datos;
import com.library.api.libros.model.DatosLibros;
import com.library.api.libros.service.ConsumoAPI;
import com.library.api.libros.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    //Instancia de la Class ConsumoAPI
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    //Instancia de la Class ConvierteDatos
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/";

    private final Scanner teclado = new Scanner(System.in);
    public void muestraElMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
//        System.out.println(json);
        var datos = conversor.obtenerDatos(json,Datos.class);
//        System.out.println(datos);

        //Top 10 libros más descargados
        System.out.println("Top 10 libros más descargados");
        System.out.println("---------------------------------");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);

        //Busqueda de libros por nombre
        System.out.println("---------------------------------");
        System.out.println("Ingrese el nombre del libro que desea buscar");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE+"?search=" + tituloLibro.replace(" ","+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if(libroBuscado.isPresent()){
            System.out.println("Libro Encontrado ");
            System.out.println(libroBuscado.get());
            System.out.println("---------------------------------");

            //Trabajando con estadisticas
            DoubleSummaryStatistics est = datos.resultados().stream()
                    .filter(d -> d.numeroDeDescargas() >0 )
                    .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
            System.out.println("Cantidad media de descargas: " + est.getAverage());
            System.out.println("Cantidad máxima de descargas: "+ est.getMax());
            System.out.println("Cantidad mínima de descargas: " + est.getMin());
            System.out.println("Cantidad de registros evaluados para calcular las estadisticas: " + est.getCount());


        }else {
            System.out.println("Libro no encontrado");
        }



    }

}
