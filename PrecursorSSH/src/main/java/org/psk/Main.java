package org.psk;

import lombok.SneakyThrows;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.shell.ProcessShellFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Klasa implementująca serwer SSH.
 */
@Slf4j
public class Main {

    /**
     * Główna metoda uruchamiająca serwer SSH.
     * @param args Argumenty uruchomieniowe programu.
     */
    public static void main(String[] args) {
        startSshServer();
    }

    /**
     * Metoda inicjująca i uruchamiająca serwer SSH.
     */
    private static void startSshServer() {
        final Path pathToKey = Paths.get("hostkey.ser");
        final Path pathToJsonFile = Paths.get("auth.json");
        final Map<String, String> credentials = loadCredentialsFromJsonFile(pathToJsonFile);

        final ExecutorService service = Executors.newSingleThreadExecutor();
        final Object lock = new Object();

        // Tworzenie serwera SSH
        try (SshServer sshd = SshServer.setUpDefaultServer()) {
            sshd.setPort(22); // Ustawienie portu

            //sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

            // Ustawienie klucza hosta (w praktyce należy wygenerować własny klucz)
            sshd.setKeyPairProvider(new FileKeyPairProvider(pathToKey));
            // Ustawienie uwierzytelniania
            sshd.setPasswordAuthenticator((username, password, session) -> {
                String storedPassword = credentials.get(username);
                return storedPassword != null && storedPassword.equals(password);
            });

            sshd.setShellFactory(new ProcessShellFactory("wsl java -jar term.jar", "wsl java", "-jar"));

            // Start serwera SSH
            log.info("Starting SSH server...");
            sshd.start();
            log.info("SSH server started. Listening on port: {}", 22);

            service.submit(() -> {
                try {
                    synchronized (lock) {
                        while (!Thread.currentThread().isInterrupted()) {
                            lock.wait(10000);
                        }
                    }
                } catch (InterruptedException e) {
                    log.info("Server has been stopped.");
                    Thread.currentThread().interrupt();
                }
            });

            boolean isTerminated = false;
            try {
                isTerminated = service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                log.error("Error occurred: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }

            if (isTerminated) {
                log.info("ExecutorService has terminated.");
            } else {
                log.info("ExecutorService has not terminated.");
            }

            service.shutdown();
            sshd.stop();

            // Oczekiwanie na zakończenie
            /*Thread.sleep(Long.MAX_VALUE);
            sshd.stop();*/

        } catch (IOException e) {
            System.err.println("Error starting SSH server: " + e.getMessage());
            log.error("Error starting SSH server: {}", e.getMessage());
        } finally {
            // Zatrzymanie serwera SSH
            System.out.println("SSH server stopped.");
            log.info("SSH server stopped");
        }
    }

    /**
     * Funkcja ładująca dane uwierzytelniające z pliku JSON.
     * @param filePath Ścieżka do pliku JSON zawierającego dane uwierzytelniające.
     * @return Mapa danych uwierzytelniających (nazwa użytkownika -> hasło).
     */
    @SneakyThrows
    private static Map<String, String> loadCredentialsFromJsonFile(final Path filePath) {
        Map<String, String> credentials = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(filePath.toFile());
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getValue().isTextual()) {
                    credentials.put(entry.getKey(), entry.getValue().asText());
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while reading JSON file: {}", e.getMessage());
        }
        return credentials;
    }
}