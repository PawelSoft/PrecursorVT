package org.psk;

import lombok.SneakyThrows;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.shell.ProcessShellFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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
        startTelnetServer();
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
     * Metoda inicjująca i uruchamiająca serwer Telnet.
     */
    private static void startTelnetServer() {
        final NioSocketAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(StandardCharsets.UTF_8)));

        acceptor.setHandler(new TelnetServerHandler());

        try {
            acceptor.bind(new InetSocketAddress(23)); // Ustawienie portu Telnet
            log.info("Telnet server started. Listening on port: {}", 23);
        } catch (IOException e) {
            log.error("Error starting Telnet server: {}", e.getMessage());
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
     * Handler for Telnet server.
     */
    static class TelnetServerHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            session.write("Welcome to the Telnet server. Type 'exit' to close the connection.\n");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            String str = message.toString().trim();
            if ("exit".equalsIgnoreCase(str)) {
                session.closeNow();
            } else {
                session.write("You said: " + str + "\n");
            }
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            log.info("IDLE " + session.getIdleCount(status));
        }
    }
}