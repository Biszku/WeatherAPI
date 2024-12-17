package com.WeatherAPI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WeatherAPI {
    private static final HttpServer server;

    static {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.createContext("/weather", new WeatherHandler());

        server.setExecutor(null);
        System.out.println("Server is running on http://localhost:8080...");
        server.start();
    }
}

class WeatherHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Jedis jedis = new Jedis(System.getenv("REDIS_CONN_STRING"));
        RedisRateLimiter rateLimiter = new RedisRateLimiter(jedis, 5, 60);
        String clientId = exchange.getRemoteAddress().getAddress().getHostAddress();

        if (!rateLimiter.isAllowed(clientId)) {
            ErrorResponse errorResponse = new ErrorResponse("Rate limit exceeded");
            String response = Json.stringify(errorResponse);
            exchange.sendResponseHeaders(429, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            jedis.close();
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().toString();
            String[] params = query.split("/");

            if (params.length != 3) {
                exchange.sendResponseHeaders(405, -1);
                jedis.close();
                return;
            }

            String city = params[params.length - 1];
            if (jedis.exists(city)) {
                String response = jedis.get(city);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                jedis.close();
                return;
            }

            WeatherEntity weatherEntity;
            try {
                weatherEntity = WeatherData.getWeather(city);
            } catch (Exception e) {
                ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
                String response = Json.stringify(errorResponse);
                exchange.sendResponseHeaders(500, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                jedis.close();
                return;
            }

            String response = Json.stringify(weatherEntity);
            jedis.set(city, response);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
        jedis.close();
    }
}