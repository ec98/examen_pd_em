package com.distribuida;

import com.distribuida.database.Book;
import com.distribuida.services.BookService;
import com.google.gson.Gson;
import io.helidon.webserver.WebServer;
import jakarta.enterprise.inject.spi.CDI;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;

import java.util.List;

public class Main {

    private static ContainerLifecycle lifecycle = null;
    static BookService container;

    public static void main(String[] args) {

        lifecycle = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
        lifecycle.startApplication(null);

        container = CDI.current().select(BookService.class).get();

        Gson gson = new Gson();

        WebServer webServer = WebServer.builder()
                .port(8081)
                .routing(builder -> builder
                        .get("/books/{id}", (req, res) -> {
                            String my_id = req.path().pathParameters().get("id");
                            if (my_id.isEmpty())
                                throw new RuntimeException("No existe el id de libro para obtener -> " + my_id);
                            res.send(gson.toJson(container.findByIdM(Integer.valueOf(my_id))));
                        })
                        .get("/books", (req, res) -> {
                            List<Book> lsbooks = container.getAllM();
                            if (lsbooks.isEmpty()) throw new RuntimeException("Estan vacio los libros en las listas");
                            res.send(gson.toJson(lsbooks));
                        })
                        .post("/books", (req, res) -> {
                            String str = req.content().as(String.class);
                            if (str.isEmpty())
                                throw new RuntimeException("Se debe agregar el objeto correspondente a la base de datos");
                            Book my_book = gson.fromJson(str, Book.class);
                            if (my_book == null) throw new RuntimeException("No se ha encontrado el objeto");
                            container.createM(my_book);
                            res.send(gson.toJson(my_book));
                        })
                        .put("/books/{id}", (req, res) -> {
                            String my_id = req.path().pathParameters().get("id");
                            if (my_id.isEmpty())
                                throw new RuntimeException("El id libro no existe para actualizar " + my_id);
                            Book my_book = gson.fromJson(req.content().as(String.class), Book.class);
                            if (my_book == null) throw new RuntimeException("No se ha encontrado el objeto");
                            container.updateM(my_book);
                            res.send(gson.toJson(my_book));
                        })
                        .delete("/books/{id}", (req, res) -> {
                            String my_id = req.path().pathParameters().get("id");
                            if (my_id.isEmpty())
                                throw new RuntimeException("El id libro no existe para eliminar -> " + my_id);
                            container.deleteM(Integer.parseInt(my_id));
                            res.send("Libro id eliminado -> " + gson.toJson(my_id));
                        })
                ).build();

        webServer.start();
        container.getAllM().stream().forEach(x -> {
            System.out.printf("[%s] %s%n", x.getId(), x.getTitle());
        });
        shutdown();
    }

    public static void shutdown() {
        lifecycle.stopApplication(null);
    }
}
