package org.psk;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.psk.tuiapplication.MainWindow;
import org.psk.tuiapplication.TodoListWindow;

@Slf4j
public class ClientHandler implements Command {

    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private InputStream in;
    private OutputStream out, errout;
    private ChannelSession session;
    private Environment environment;
    private int ScreenWidth = 80;
    private int ScreenHeight = 24;
    private ExitCallback exitCallback;

    private final BlockingQueue<byte[]> messages = new LinkedBlockingQueue<>();

    private Thread receiverThread, senderThread, termThread;

    private MultiWindowTextGUI gui;
    private TerminalScreen screen;

    /**
     * Domyślny konstruktor. Inicjuje ekran TUI.
     */
    public ClientHandler() {
        logger.info("Initializing ClientHandler");
    }

    /**
     * Ustawia exitCallback.
     * @param exitCallback Obiekt ExitCallback.
     */
    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;
    }

    /**
     * Ustawia strumień błędu.
     * @param outputStream Strumień błędu.
     */
    @Override
    public void setErrorStream(OutputStream outputStream) {
        this.errout = outputStream;
    }

    /**
     * Ustawia strumień wejściowy - z niego odczytywane są klawisze, które są naciskane.
     * @param inputStream Strumień wejściowy.
     */
    @Override
    public void setInputStream(InputStream inputStream) {
        this.in = inputStream;
    }

    /**
     * Ustawia strumień wyjściowy - to na niego wysyłane będę dane do wyświetlenia na ekranie.
     * @param outputStream Strumień wyjściowy.
     */
    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;
    }

    /**
     * Uruchamia nową sesję użytkownika.
     * @param channelSession Obiekt ChannelSession - sesja kanału.
     * @param environment Środowisko - pozwala na odczytanie rozmiaru okna zdalnego i innych parametrów.
     * @throws IOException W przypadku niepowodzenia.
     */
    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        this.session = channelSession;
        this.environment = environment;

        Map<String, String> env = environment.getEnv();
        logger.info("Environment variables: " + env);
        logger.info("Channel session: " + session);

        this.environment.addSignalListener((channel, signal) -> {
            try {
                messages.put(new byte[]{(byte) 255, (byte) 255, (byte) 0, (byte) 255, (byte) 255});
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }, Signal.WINCH);

        try {
            this.ScreenHeight = Integer.parseInt(env.get("LINES"));
            this.ScreenWidth = Integer.parseInt(env.get("COLUMNS"));
        } catch (NumberFormatException e){
            logger.info(e.getLocalizedMessage());
        }

        receiverThread = new Thread(this::receiver);
        senderThread = new Thread(this::interpreter);
        termThread = new Thread(this::initializeTerminal);

        termThread.start();
    }

    /**
     * Inicjalizuje terminal i GUI
     */
    private void initializeTerminal() {
        try {
            Terminal terminal = new DefaultTerminalFactory(out, in, StandardCharsets.UTF_8).createTerminal();
            screen = new TerminalScreen(terminal);
            TextColor.RGB brandBg = new TextColor.RGB(67, 160, 71);

            screen.startScreen();

            gui = new MultiWindowTextGUI(screen,
                    new DefaultWindowManager(), null, new EmptySpace(brandBg));

            Panel panel = new Panel();
            panel.setLayoutManager(new GridLayout(2));

            MainWindow mainwindow = new MainWindow(gui, this);
            TodoListWindow todoListWindow = new TodoListWindow(gui, screen, mainwindow);

            terminal.addResizeListener((terminal1, terminalSize) -> todoListWindow.updateSize());

            gui.waitForWindowToClose(mainwindow);
            gui.addWindowAndWait(mainwindow);

            logger.info("Created terminal");
        } catch (IOException e) {
            log.error("Error occurred while initializing terminal interface: {}", e.getMessage());
        }
    }

    /**
     * Przerywa sesję użytkownika.
     * @param channelSession Obiekt ChannelSession - nieużywany.
     */
    @Override
    public void destroy(ChannelSession channelSession) {
        try {
            if (receiverThread != null && receiverThread.isAlive())
                receiverThread.interrupt();
            if (senderThread != null && senderThread.isAlive())
                senderThread.interrupt();
            if (termThread != null && termThread.isAlive()) {
                screen.close();
                termThread.interrupt();
            }

        } catch (Exception e){
            logger.error("An exception occured while destroying session " + e.getMessage());
        }
        finally {
            if (exitCallback != null)
                exitCallback.onExit(0);
            logger.info("Client disconnected due to CTRL + C");
        }
    }

    /**
     * Metoda odbierająca dane z wejścia.
     * W pętli odczytuje dane z wejścia (InputStream), umieszcza je w buforze i dodaje do kolejki wiadomości.
     * Jeśli wątek zostanie przerwany, zapisuje ten fakt w logach.
     * Po zakończeniu działania wątku, zapisuje informację o jego zakończeniu.
     */
    private void receiver(){
        try{
            byte[] buf = new byte[1024];
            int bytesread;
            while((bytesread = in.read(buf)) != -1){
                byte[] tmp = new byte[bytesread];
                System.arraycopy(buf, 0, tmp, 0, bytesread);
                messages.put(tmp);
            }
        } catch (InterruptedException ignored){
            logger.info("Receiver interrupted");
        }
        catch (Exception e){
            logger.error(e.getMessage() + e.getLocalizedMessage());
        }
        finally {
            logger.info("Receiver thread finished!");
        }
    }

    /**
     * Metoda przetwarzająca odebrane wiadomości.
     * W pętli odbiera dane z kolejki wiadomości, konwertuje je na tablicę int i loguje.
     * Jeśli wątki receiverThread i senderThread przestaną działać, sesja zostaje przerwana.
     */
    private void interpreter(){
        try {
            while(receiverThread.isAlive() || !messages.isEmpty()) {
                byte[] data = messages.take();
                int[] intData = new int[data.length];
                for(int i=0;i < data.length; ++i)
                    intData[i] = data[i] & 0xFF;

            }
        } catch (InterruptedException e){
            logger.info("Sender thread finished!");
        } catch (Exception e) {
            log.error("Error occurred while interpreting command: {}", e.getMessage());
            destroy(session);
        }
    }

    /**
     * Metoda do zewnętrznego zakończenia sesji użytkownika.
     */
    public void terminateSession() {
        destroy(session);
    }
}
