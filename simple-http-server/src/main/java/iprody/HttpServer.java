package iprody;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected");

            var reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream(),
                            StandardCharsets.UTF_8));

            var writer = new PrintWriter(
                    new OutputStreamWriter(
                            clientSocket.getOutputStream()
            ));

            while (!reader.ready()) ;

            while (reader.ready()) {
                System.out.println(reader.readLine());
            }

            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println();
            writer.println("<h1>Hello from server!</h1>");
            writer.println("<p>It works</p>");
            writer.flush();

            serverSocket.close();
        }
    }
}


