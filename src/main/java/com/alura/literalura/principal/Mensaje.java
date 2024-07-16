package com.alura.literalura.principal;

public class Mensaje {
    public void menu() {
        String menu = """
                ******************************
                \tBIENVENIDO a LiterAlura
       
                1. Buscar libro por título
                2. Listar libros registrados
                3. Listar autores registrados
                4. Listar autores vivos por año
                5. Listar libros por idioma
                6. Salir del programa
                ------------------------------
                7. Ver estadisticas
                8. Top 10 libros más descargados
                9. Buscar autor por nombre
                ******************************
                Elige una opción:
                """;
        System.out.println(menu);
    }

    public void menuIdioma(){
        String menuIdioma = """
                ---------------------
                Idiomas disponibles:
                
                  -en  (Inglés)
                  -es  (Español)
                  -fr  (Francés)
                  -de  (Alemán)
                  -it  (Italiano)
                  -pt  (Portugués)
                  -ja  (Japonés)
                --------------------
                """;
        System.out.println(menuIdioma);
    }
}
