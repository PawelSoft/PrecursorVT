package org.psk;

import lombok.SneakyThrows;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.shell.ProcessShellFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * Klasa implementująca serwer SSH.
 */
@Slf4j
public class Main {

    public static final int defaultSSHportNumber = 22;

    /**
     * Główna metoda uruchamiająca serwer SSH.
     * @param args Argumenty uruchomieniowe programu.
     */
    public static void main(String[] args) {
        int selectedSshPort = defaultSSHportNumber;

        if (args.length == 1) {
            try {
                int port = Integer.parseInt(args[0]);

                if (isValidPortRange(port)) {
                    log.info("SSH port: " + port);
                    selectedSshPort = port;
                } else {
                    log.error("Invalid port number. Port must be between 1 and 65535.");
                    System.exit(1);
                }

            } catch (NumberFormatException e) {
                log.error("Invalid input. Port must be an integer.");
                System.exit(1);
            }
        }

        startSshServer(selectedSshPort);
    }

    /**
     * Metoda inicjująca i uruchamiająca serwer SSH.
     * @param selectedSshPort Wybrany port dla ssh.
     */
    private static void startSshServer(int selectedSshPort) {
        final Path pathToKey = Paths.get("hostkey.ser");
        final Path pathToJsonFile = Paths.get("auth.json");
        final Map<String, String> credentials = loadCredentialsFromJsonFile(pathToJsonFile);

        final ExecutorService service = Executors.newSingleThreadExecutor();
        final Object lock = new Object();
        String os = System.getProperty("os.name").toLowerCase();

        // Tworzenie serwera SSH
        try (SshServer sshd = SshServer.setUpDefaultServer()) {
            sshd.setPort(selectedSshPort); // Ustawienie portu

            // Ustawienie klucza hosta (w praktyce należy wygenerować własny klucz)
            sshd.setKeyPairProvider(new FileKeyPairProvider(pathToKey));
            // Ustawienie uwierzytelniania
            sshd.setPasswordAuthenticator((username, password, session) -> {
                String storedPassword = credentials.get(username);
                return storedPassword != null && storedPassword.equals(password);
            });

            if (os.contains("win")) {
                sshd.setShellFactory(new ProcessShellFactory("wsl java -jar PrecursorTUI.jar", "wsl java", "-jar"));
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                sshd.setShellFactory(new VTClientShellFactory());
            }

            //sshd.setShellFactory(new ProcessShellFactory("/opt/jdk-18/bin/java -jar term.jar", "/opt/jdk-18/bin/java", "-jar", "term.jar"));

            // Start serwera SSH
            log.info("Starting SSH server...");
            sshd.start();
            log.info("SSH server started. Listening on port: {}", selectedSshPort);

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

    /**
     * Sprawdza, czy dany port mieści poprawnym zakresie możliwych portów.
     *
     * @param port Numer portu do sprawdzenia.
     * @return True, jeśli port jest poprawnym portem SSH; false w przeciwnym razie.
     */
    public static boolean isValidPortRange(int port) {
        return port >= 1 && port <= 65535;
    }
}