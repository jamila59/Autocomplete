import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

public class Server {
    // Port number used to connect to this server
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
    // Maximum number of matches returned in response
    private static final int MAX_MATCHES = 5;
    // JSON endpoint structure
    private static final String QUERY_TEMPLATE = "{\"items\":[%s]}";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("java Server [tsv file]");
        }
        String filename = args[0];
        Autocomplete autocomplete = new Autocomplete(filename);
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 100);
        server.createContext("/", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("index.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        server.createContext("/query", (HttpExchange t) -> {
            String s = parse("s", t.getRequestURI().getQuery().split("&"));
            List<Term> matches = autocomplete.allMatches(s);
            if (matches.size() > MAX_MATCHES) {
                matches = matches.subList(0, MAX_MATCHES);
            }
            send(t, "application/json", String.format(QUERY_TEMPLATE, json(matches)));
        });
        server.setExecutor(null);
        server.start();
    }

    private static String parse(String key, String... params) {
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return "";
    }

    private static void send(HttpExchange t, String contentType, String data)
            throws IOException, UnsupportedEncodingException {
        t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

    private static String json(Iterable<Term> matches) {
        StringBuilder results = new StringBuilder();
        for (Term term : matches) {
            if (results.length() > 0) {
                results.append(',');
            }
            results.append('{')
                   .append("\"query\":")
                   .append('"').append(term.query()).append('"')
                   .append(',')
                   .append("\"weight\":")
                   .append(term.weight())
                   .append('}');
        }
        return results.toString();
    }
}
