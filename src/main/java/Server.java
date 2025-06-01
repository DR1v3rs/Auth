import org.example.Main;
import org.example.auth.Authorization;
import org.example.auth.SessionManager;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int DEFAULT_PORT = 8000;

    // Обработка ошибки в случае незапуска сервера
    public static void main(String[] args) {
        try {
            startServer();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод запуска сервера
    private static void startServer() throws IOException {
        int port = getServerPort();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> handleHtmlPage(exchange, "/index.html"));
        server.createContext("/hello", exchange -> handleHtmlPage(exchange, "/hello.html"));
        server.createContext("/login", exchange -> handleHtmlPage(exchange, "/login.html"));
        registerHandlers(server);
        server.start();
        System.out.println("Server started on port: " + port);
    }

    // Метод обработки порта для сервера
    private static int getServerPort() {
        try {
            return Integer.parseInt(Main.httpPort);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port in Main.httpPort! Using default port: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    private static void registerHandlers(HttpServer server) {
        server.createContext("/auth", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleLoginRequest(exchange);
            }
        });

        // Защищенная страница профиля
        server.createContext("/profile", exchange -> {
            String sessionId = getSessionIdFromCookies(exchange);

            if (SessionManager.isValidSession(sessionId)) {
                String username = SessionManager.getUsername(sessionId);
                Map<String, String> vars = new HashMap<>();
                vars.put("PORT", Main.httpPort);
                vars.put("USERNAME", username);
                handleDynamicHtmlPage(exchange, "/profile.html", vars);
            } else {
                redirectToLogin(exchange);
            }
        });

        // Выход из системы
        server.createContext("/logout", exchange -> {
            String sessionId = getSessionIdFromCookies(exchange);
            if (sessionId != null) {
                SessionManager.invalidateSession(sessionId);
            }
            exchange.getResponseHeaders().set("Set-Cookie", "SESSIONID=; Max-Age=0");
            redirectToLogin(exchange);
        });
    }

    private static void handleLoginRequest(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String[] params = requestBody.split("&");
        String username = params[0].split("=")[1];
        String password = params[1].split("=")[1];

        String sessionId = Authorization.userLogin(username, password);

        if (sessionId != null) {
            // Успешная авторизация
            exchange.getResponseHeaders().add("Set-Cookie",
                    "SESSIONID=" + sessionId + "; Path=/; HttpOnly");
            exchange.getResponseHeaders().set("Location", "/profile");
            exchange.sendResponseHeaders(302, -1);
        } else {
            // Ошибка авторизации
            Map<String, String> vars = new HashMap<>();
            vars.put("PORT", Main.httpPort);
            vars.put("ERROR_MESSAGE", getAuthErrorMessage());
            handleDynamicHtmlPage(exchange, "/badAuth.html", vars);
        }
    }

    private static void handleHtmlPage(HttpExchange exchange, String htmlFile) throws IOException {
        handleDynamicHtmlPage(exchange, htmlFile, Map.of("PORT", Main.httpPort));
    }

    private static void handleDynamicHtmlPage(HttpExchange exchange, String htmlFile, Map<String, String> variables) throws IOException {
        try (InputStream is = Server.class.getResourceAsStream(htmlFile)) {
            if (is == null) {
                sendError(exchange, 404, "Page not Found! Страница не найдена");
                return;
            }

            String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            sendHtmlResponse(exchange, html);
        } catch (IOException e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal Server Error");
        }
    }

    private static String getSessionIdFromCookies(HttpExchange exchange) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                if (cookie.trim().startsWith("SESSIONID=")) {
                    return cookie.split("=")[1];
                }
            }
        }
        return null;
    }

    private static void redirectToLogin(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Location", "/login.html");
        exchange.sendResponseHeaders(302, -1);
    }

    private static String getAuthErrorMessage() {
        if (Authorization.addUser) {
            return "Пользователь не найден";
        } else if (Authorization.wrongPass) {
            return "Неверный пароль";
        }
        return "Ошибка авторизации";
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
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(code, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}