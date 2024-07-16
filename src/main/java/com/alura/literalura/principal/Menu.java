package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.DatosLibro;
import com.alura.literalura.model.DatosLibros;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ApiRequest;
import com.alura.literalura.service.DatosConversion;

import java.util.*;
import java.util.stream.Collectors;

public class Menu {
    private Scanner teclado = new Scanner(System.in);
    private final String BASE_URL = "https://gutendex.com/books";
    private List<Libro> libroBuscado = new ArrayList<>();
    private List<Autor> autorBuscado = new ArrayList<>();
    private Mensaje print = new Mensaje();

    //Inyeccion de dependencias
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Menu(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        int option = 0;
        do {
            print.menu();
            //Me aseguro que la opcion sea un numero
            option = opcionUsuario();

            switch (option) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    obtenerTodosLosLibros();
                    break;
                case 3:
                    obtenerTodosLosAutores();
                    break;
                case 4:
                    obtenerAutoresVivosPorAnio();
                    break;
                case 5:
                    obtenerlibrosPorIdioma();
                    break;
                case 6:
                    System.out.println("¡Adios!");
                    break;
                case 7:
                    obtenerestadisticas();
                    break;
                case 8:
                    obtenerTop10Libros();
                    break;
                case 9:
                    obtenerautorPorNombre();
                    break;
                default:
                    System.out.println("Opcion Inválida");
                    break;
            }
        } while (option != 6);

    }

    //Valida que la opcion sea un numero
    public int opcionUsuario() {
        int number = 0;
        while (true) {
            try {
                number = teclado.nextInt();
                teclado.nextLine();
                if(number > 0){
                    return number;
                }
                System.out.println("Por favor, introduce un número válido.");
            } catch (InputMismatchException e) {
                System.out.println("Por favor, introduce un número válido.");
                teclado.nextLine(); // consume el input invalido
            }
        }
    }

    private String getStringFromUser(String message) {
        String data = "";
        while (true) {
            System.out.println(message);
            data = teclado.nextLine();
            if (!data.isEmpty()) {
                return data;
            }
        }

    }

    public String getWebData(String title) {
        ApiRequest request = new ApiRequest();
        var url = BASE_URL + "/?search=" + title.replace(" ", "+");
        return request.getData(url);
    }

    public DatosLibros jsonToDatosLibros(String datos) {
        DatosConversion dataConversion = new DatosConversion();
        return dataConversion.convertirDatos(datos, DatosLibros.class);
    }

    public DatosLibro obtenerLibrosPorAutor(List<DatosLibro> libros) {
        return libros.stream()
                .filter(libro -> !libro.autor().isEmpty())
                .findFirst()
                .orElse(null);
    }

    public Libro buscarOguardarLibro(Autor author, DatosLibro libro) {
        Libro libroAguardar = null;
        List <Libro> libros = author.getLibros();

        Optional<Libro> libroPorAutor = libros.stream()
                .filter(libro1 -> libro1.getTitulo().equals(libro.titulo()))
                .findFirst();

        if (libroPorAutor.isPresent()) {
            System.out.println("El libro ya registrado!");
            libroAguardar = libroPorAutor.get();
        } else {

            libroAguardar = new Libro(libro.titulo(), author,
                    libro.idioma().get(0), libro.numeroDeDescargas());

            author.setLibros(libroAguardar);
            libroRepository.save(libroAguardar);

            System.out.println("Libro guardado!");
        }
        return libroAguardar;
    }

    public Autor searchOrSaveAuthor(DatosLibro libro) {
        Optional<Autor> autorBuscado = autorRepository.findByNombre(libro.autor().get(0).nombre());
        Autor autorAguardar = null;


        if (!autorBuscado.isPresent()) {
            autorAguardar = new Autor(libro.autor().get(0).nombre(),
                    libro.autor().get(0).nacimiento(), libro.autor().get(0).muerte());
            autorRepository.save(autorAguardar);
            System.out.println("Autor guardado!");
        } else {
            autorAguardar = autorBuscado.get();
            System.out.println("Autor ya registrado!");
        }
        return autorAguardar;

    }

    //Buscar un libro en la web y lo guarda en la base de datos
    //en caso de que no este registrado
    public void buscarLibroPorTitulo() {

        String mensaje = "Introduce el titulo del libro a buscar: ";
        var titulo = getStringFromUser(mensaje);

        String datos = getWebData(titulo);
        DatosLibros libros = jsonToDatosLibros(datos);

        if (!libros.libros().isEmpty()) {
            DatosLibro libro = obtenerLibrosPorAutor(libros.libros());

            Autor autor = searchOrSaveAuthor(libro);
            Libro book = buscarOguardarLibro(autor, libro);
            System.out.println(autor);
            System.out.println(book);

        } else {
            System.out.println("No se encontraron resultados");
        }
    }

    private void obtenerTodosLosLibros() {
        // findAll() retorna una lista de libros o
        //retorna un lista vacia si no encuentra nada
        var libroBuscado = libroRepository.findAll();
        if (libroBuscado.isEmpty()) {
            System.out.println("No se encontraron libros registrados ");
        }
        libroBuscado.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(libro -> {
                    System.out.println(libro.toString());
                });
    }

    private void obtenerTodosLosAutores() {
        var autorBuscado = autorRepository.findAll();
        if (autorBuscado.isEmpty()) {
            System.out.println("No se encontraron autores registrados");
        }
        autorBuscado.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(autor -> {
                    System.out.println(autor.toString() /*+ autor.getLibros()*/);
                    //System.out.println("Libros: ");
                    System.out.println(autor.getLibros());
                });
    }

    private void obtenerAutoresVivosPorAnio() {
        System.out.println("Ingrese año: ");

        var year = opcionUsuario();
        List<Autor> autoresVivos = autorRepository.getAliveAuthors(year);
        if (autoresVivos.isEmpty()) {
            System.out.println("No hay autores vivos registrados del año: " + year);
        } else {
            autoresVivos.stream()
                    .forEach(autor -> {
                        System.out.println(autor.toString());
                    });
        }

    }

    private void obtenerlibrosPorIdioma() {

        //Imprimir los idiomas disponibles
        print.menuIdioma();

        String message= "Introduce el idioma: ";
        String language = getStringFromUser(message);

        List<Libro> librosPorIdioma = libroRepository.findBookByLanguage(language);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + language);
        } else {
            librosPorIdioma.stream()
                    .forEach(libro -> {
                        System.out.println(libro.toString());
                    });
        }
    }

    //Ejercicios adicionales

    private void obtenerestadisticas() {
        List<Libro> booksDownloads = libroRepository.findAll();
        DoubleSummaryStatistics stats = booksDownloads.stream()
                .filter(libro -> libro.getNumeroDeDescargas() >= 0)
                .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));

        String msj = """
                -------------------------
                Estadisticas de descargas:
                    Promedio total de libros: %.2f
                    Libro menos descargado: %.2f
                    Libro mas descargado: %.2f
                    Total libros: %d
                -------------------------
                """.formatted(stats.getAverage(), stats.getMin(), stats.getMax(), stats.getCount());
        System.out.println(msj);
    }

    private void obtenerTop10Libros() {
        System.out.println("Top 10 libros mas descargados:");
        List<String> top10Books = libroRepository.findTop10Books();
        if (top10Books.isEmpty()) {
            System.out.println("No se encontraron libros");
        } else {
            int i = 0;
            for (String libro : top10Books) {
                System.out.println(++i + ". " + libro);
            }
        }

    }

    private Autor selectAuthor(List<Autor> autores) {
        int i = 0;

        System.out.println("Autores encontrados: ");

        for (Autor autor : autores) {
            System.out.println(++i + ". \n" + autor.toString());
        }

        System.out.println("Seleccione un autor: ");
        int option = opcionUsuario();

        return autores.get(option - 1);
    }


    private void obtenerautorPorNombre() {
        String apellido = "";

        String message = "Introduce el nombre del autor: ";
        String data = getStringFromUser(message);

        String[] names = data.split(" ");
        apellido = names[names.length - 1];

        List<Autor> autores = autorRepository.findAutorByName(apellido);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores con el apellido: " + apellido);
        } else {
            Autor autor = autores.size() > 1 ? selectAuthor(autores) : autores.get(0);
            System.out.println(autor.toString());
        }

    }

}
