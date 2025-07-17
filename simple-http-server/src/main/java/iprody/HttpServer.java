package iprody;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class HttpServer {

    /**
     * Порт сервера.
     */
    private static final int SERVER_PORT = 8080;

    private HttpServer() {
    }

    /**
     * Точка входа в приложение.
     *
     * @param args входные параметры командной строки
     */
    public static void main(final String[] args) throws IOException {
        final Path relativePath = Paths.get("simple-http-server", "static");
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server started at http://localhost:8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected");

            var reader = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );

            var writer = new PrintWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream())
            );

            while (!reader.ready()) {
                Thread.onSpinWait();
            }

            // Чтение запроса
            while (reader.ready()) {
                Optional<String> result = reader.readLine()
                        .lines()
                        .findFirst();

                if (result.toString().contains("GET")) {
                    String[] myArr = result.get().split(" ");
                    String fileName = myArr[1].substring(1);
                    File directory = new File(String.valueOf(relativePath));
                    FilenameFilter filter =
                            (dir, name) -> name.equals(fileName);
                    File[] files = directory.listFiles(filter);

                    if (files != null && files.length > 0) {
                        writer.println("HTTP/1.1 200 OK");
                        writer.println(
                                "Content-Type: text/html; charset=UTF-8"
                        );

                        byte[] fileContent =
                                Files.readAllBytes(
                                        Path.of(files[0].getAbsolutePath())
                                );
                        int contentLength = fileContent.length;
                        writer.println("Content-Length: " + contentLength);

                        String text = new String(
                                fileContent, StandardCharsets.UTF_8
                        );
                        writer.println(text);

                    } else {
                        writer.println("HTTP/1.1 404 Not Found");
                        writer.println(
                                "Content-Type: text/html; charset=UTF-8"
                        );
                        writer.println();
                        writer.println("<!DOCTYPE html>");
                        writer.println("<html>");
                        writer.println(
                                "<head><title>404 Not Found</title></head>"
                        );
                        writer.println("<body>");
                        writer.println("<h1>404 Not Found</h1>");
                        writer.println("</body>");
                        writer.println("</html>");
                    }
                }
            }

            writer.flush();
            clientSocket.close();
        }
    }
}


