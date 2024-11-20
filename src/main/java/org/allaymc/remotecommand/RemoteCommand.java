package org.allaymc.remotecommand;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.okaeri.configs.ConfigManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author daoge_cmd
 */
@Slf4j
public class RemoteCommand extends Plugin {

    public static final String ARG_TOKEN = "token";
    public static final String ARG_COMMAND = "command";

    private Config config;

    @Override
    public void onLoad() {
        log.info("RemoteCommand loaded!");
    }

    @Override
    @SneakyThrows
    public void onEnable() {
        this.config = ConfigManager.create(Config.class, Utils.createConfigInitializer(pluginContainer.dataFolder().resolve("config.yml")));
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(this.config.httpServerPort()), 0);
        httpServer.createContext("/remotecommand", new RemoteCommandHttpHandler());
        httpServer.start();
        log.info("Http server is listening on port {}", this.config.httpServerPort());
        log.info("RemoteCommand enabled!");
    }

    @Override
    public void onDisable() {
        log.info("RemoteCommand disabled!");
    }

    class RemoteCommandHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                log.warn("Receive http request with invalid method. IP: {}", exchange.getRemoteAddress());
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            if (query == null || query.isEmpty()) {
                log.warn("Receive http request with missing query parameters. IP: {}", exchange.getRemoteAddress());
                sendResponse(exchange, 400, "Bad Request: Missing query parameters");
                return;
            }

            String token = getQueryParam(query, ARG_TOKEN);
            String command = getQueryParam(query, ARG_COMMAND);

            if (token == null || command == null) {
                log.warn("Receive http request with missing required parameters. IP: {}", exchange.getRemoteAddress());
                sendResponse(exchange, 400, "Bad Request: Missing required parameters 'token' or 'command'");
                return;
            }

            if (!isValidToken(token)) {
                log.warn("Receive http request with invalid token: {}. IP: {}", token, exchange.getRemoteAddress());
                sendResponse(exchange, 403, "Forbidden: Invalid token");
                return;
            }

            var result = Registries.COMMANDS.execute(Server.getInstance(), command);
            sendResponse(exchange, 200, result.isSuccess() ? "Success" : "Fail");
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }

        private String getQueryParam(String query, String paramName) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals(paramName)) {
                    return pair[1];
                }
            }
            return null;
        }

        private boolean isValidToken(String token) {
            return config.token().equals(token);
        }
    }
}