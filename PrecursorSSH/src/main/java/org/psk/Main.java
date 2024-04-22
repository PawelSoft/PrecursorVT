package org.psk;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Tworzenie serwera SSH
        try (SshServer sshd = SshServer.setUpDefaultServer()) {
            sshd.setPort(22); // Ustawienie portu

            // Ustawienie klucza hosta (w praktyce należy wygenerować własny klucz)
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

            // Ustawienie autoryzatora
            sshd.setPasswordAuthenticator((username, password, session) -> "a".equals(username) &&
                    "a".equals(password));

            // Ustawienie fabryki procesu, która zwraca czerwony tekst
            //ShellFactory shellFactory = new CustomShellFactory();
            //sshd.setShellFactory(shellFactory);

            sshd.setShellFactory(new ProcessShellFactory("wsl java -jar term.jar", "wsl java", "-jar"));
            //sshd.setShellFactory(new CustomShellFactory());
            //sshd.setShellFactory(new ProcessShellFactory("bash", "/usr/bin/bash", "-i"));

            //sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));

            // Start serwera SSH
            System.out.println("Starting SSH server...");
            sshd.start();
            System.out.println("SSH server started. Listening on port 22... ");

            // Oczekiwanie na zakończenie
            Thread.sleep(Long.MAX_VALUE);
            sshd.stop();

        } catch (IOException e) {
            System.err.println("Error starting SSH server: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("SSH server interrupted.");
        } finally {
            // Zatrzymanie serwera SSH
            System.out.println("SSH server stopped.");
        }
    }
}