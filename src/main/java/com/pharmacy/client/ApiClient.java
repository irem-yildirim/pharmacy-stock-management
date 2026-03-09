package com.pharmacy.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP utility class for communicating with the Pharmacy REST API backend.
 *
 * <p>
 * All methods use plain {@link HttpURLConnection} — no external HTTP library
 * required.
 * </p>
 *
 * <p>
 * <b>IMPORTANT for Swing usage:</b> All API calls MUST be wrapped in a
 * {@link javax.swing.SwingWorker} to avoid blocking the Event Dispatch Thread
 * (EDT).
 * See snippet classes in {@code com.pharmacy.client.snippets} for usage
 * examples.
 * </p>
 */
public class ApiClient {

    public static final String BASE_URL = "http://localhost:8080/api";

    /** Jackson ObjectMapper — shared, thread-safe after configuration. */
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /** Prevent instantiation. */
    private ApiClient() {
    }

    // ── HTTP Methods ──────────────────────────────────────────────────────────

    /**
     * Performs a GET request and returns the raw JSON response body.
     *
     * @param endpoint e.g. "/drugs" or "/drugs/8699514016444"
     * @return JSON string response body, or empty string on error
     */
    public static String get(String endpoint) {
        try {
            URL url = URI.create(BASE_URL + endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return readResponse(conn);
        } catch (IOException e) {
            System.err.println("[ApiClient] GET error: " + e.getMessage());
            return "";
        }
    }

    /**
     * Performs a POST request with a JSON body.
     *
     * @param endpoint e.g. "/drugs"
     * @param jsonBody serialized JSON string body
     * @return JSON string response body
     */
    public static String post(String endpoint, String jsonBody) {
        return sendWithBody("POST", endpoint, jsonBody);
    }

    /**
     * Performs a PUT request with a JSON body.
     *
     * @param endpoint e.g. "/drugs/8699514016444"
     * @param jsonBody serialized JSON string body
     * @return JSON string response body
     */
    public static String put(String endpoint, String jsonBody) {
        return sendWithBody("PUT", endpoint, jsonBody);
    }

    /**
     * Performs a DELETE request.
     *
     * @param endpoint e.g. "/drugs/8699514016444"
     * @return HTTP status code (e.g. 204 for success)
     */
    public static int delete(String endpoint) {
        try {
            URL url = URI.create(BASE_URL + endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return conn.getResponseCode();
        } catch (IOException e) {
            System.err.println("[ApiClient] DELETE error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Attempts to log in utilizing the /api/users/login endpoint.
     *
     * @param username The user's username
     * @param password The user's password (plain text)
     * @return The authenticated User object if successful, null if failed
     */
    public static com.pharmacy.entity.User login(String username, String password) {
        try {
            java.util.Map<String, String> creds = new java.util.HashMap<>();
            creds.put("username", username);
            creds.put("password", password);
            String jsonBody = toJson(creds);

            String response = post("/users/login", jsonBody);
            if (response == null || response.isEmpty() || response.equals("Invalid credentials")) {
                return null;
            }
            return parseJson(response, com.pharmacy.entity.User.class);
        } catch (Exception e) {
            System.err.println("[ApiClient] Login error: " + e.getMessage());
            return null;
        }
    }

    // ── JSON Parsing Helpers ──────────────────────────────────────────────────

    /**
     * Deserializes a JSON string to a single object.
     *
     * @param json JSON string
     * @param type target class
     * @return deserialized object, or null on error
     */
    public static <T> T parseJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            System.err.println("[ApiClient] Parse error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deserializes a JSON array string to a List.
     *
     * <p>
     * Usage:
     * {@code List<Drug> drugs = ApiClient.parseJsonList(json, new TypeReference<>() {})}
     * </p>
     *
     * @param json    JSON array string
     * @param typeRef Jackson TypeReference for the list type
     * @return deserialized list, or empty list on error
     */
    public static <T> T parseJsonList(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            System.err.println("[ApiClient] ParseList error: " + e.getMessage());
            try {
                return MAPPER.readValue("[]", typeRef);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * Serializes an object to a JSON string.
     *
     * @param obj object to serialize
     * @return JSON string, or "{}" on error
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            System.err.println("[ApiClient] Serialize error: " + e.getMessage());
            return "{}";
        }
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private static String sendWithBody(String method, String endpoint, String jsonBody) {
        try {
            URL url = URI.create(BASE_URL + endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return readResponse(conn);
        } catch (IOException e) {
            System.err.println("[ApiClient] " + method + " error: " + e.getMessage());
            return "";
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        InputStream is = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();
        if (is == null)
            return "";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
