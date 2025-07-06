package iprody;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        final Path RELATIVE_PATH = Paths.get("simple-http-server", "static");
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected");

            var reader = new BufferedReader( //накапливает данные пока не считается целая строка
                    new InputStreamReader(clientSocket.getInputStream(),
                            StandardCharsets.UTF_8));

            var writer = new PrintWriter(new OutputStreamWriter(
                    clientSocket.getOutputStream()
            ));

            while (!reader.ready()) ;

            //чтение запроса
            while (reader.ready()) {
                Optional<String> result =  (reader.readLine().lines().findFirst());
                if (result.toString().contains("GET")){
                    String[] myArr = result.get().split(" ");
                    String fileName = myArr[1].substring(1); // берем заправшиваемую страницу (index.html)

                    // ищем файл в директории
                    File directory = new File(String.valueOf(RELATIVE_PATH));
                    FilenameFilter filter = (dir, name) -> name.equals(fileName);
                    File[] files = directory.listFiles(filter);

                    if (files != null && files.length > 0) {
                        writer.println("HTTP/1.1 200OK");
                        writer.println("Content-Type: text/html; charset=UTF-8");

                        byte[] fileContent = Files.readAllBytes(Path.of(files[0].getAbsolutePath()));
                        int contentLength = fileContent.length;
                        writer.println("Content-Length: " + contentLength);

                        String text = new String(fileContent, StandardCharsets.UTF_8);
                        writer.println(text);

                    } else {

                        // Отправка HTTP заголовка с кодом 404
                        writer.println("HTTP/1.1 404 Not Found");
                        writer.println("Content-Type: text/html; charset=UTF-8");
                        writer.println();

                        // Отправка HTML страницы с сообщением об ошибке
                        writer.println("<!DOCTYPE html>");
                        writer.println("<html>");
                        writer.println("<head><title>404 Not Found</title></head>");
                        writer.println("<body>");
                        writer.println("<h1>404 Not Found</h1>");
                        writer.println("</body>");
                        writer.println("</html>");
                    }
                }
            }

            writer.flush(); // Обеспечивает отправку всех данных
            clientSocket.close();
        }
    }
}


