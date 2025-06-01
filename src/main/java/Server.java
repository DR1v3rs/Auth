import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.example.Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Server {

    public static void main(String[] args) throws IOException {
        // Получаем порт из класса Main
        int port;
        try {
            port = Integer.parseInt(Main.httpPort);
        } catch (NumberFormatException e) {
            System.err.println("Uncorrected port in Main.httpPort! Used standard port: 8000");
            port = 8000;
        }

        // Создаем HTTP-сервер на указанном порту
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Обработчик страниц
        server.createContext("/", exchange -> handleHtmlPage(exchange, "/index.html"));
        server.createContext("/hello", exchange -> handleHtmlPage(exchange, "/hello.html"));
        server.createContext("/login", exchange -> handleHtmlPage(exchange, "/login.html"));

        // Запускаем сервер
        server.start();
        System.out.println("Server start at port: " + port);
    }

    private static void sendHtmlResponse(HttpExchange exchange, String html) throws IOException {
        byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private static void sendError(HttpExchange exchange, int code, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private static void handleHtmlPage(HttpExchange exchange, String htmlFile) throws IOException {
        try (InputStream is = Server.class.getResourceAsStream(htmlFile)) {
            if (is == null) {
                sendError(exchange, 404, "Page not Found! Страница не найдена");
                return;
            }
            String html = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("{{PORT}}", String.valueOf(Main.httpPort));
            sendHtmlResponse(exchange, html);
        } catch (IOException e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal Server Error");
        }
    }
}
