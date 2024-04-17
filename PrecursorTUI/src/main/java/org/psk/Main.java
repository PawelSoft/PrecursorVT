package org.psk;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            TerminalScreen screen = new TerminalScreen(terminal);
            TextGraphics graphics = screen.newTextGraphics();

            screen.startScreen();

            boolean running = true;

            // Początkowe współrzędne kwadratu
            int x = 10;
            int y = 5;

            while (running) {
                screen.clear();

                // Rysowanie kwadratu
                drawSquare(graphics, x, y);

                screen.refresh();

                KeyStroke keyStroke = screen.pollInput();

                if (keyStroke != null) {
                    switch (keyStroke.getKeyType()) {
                        case ArrowUp:
                            y--;
                            break;
                        case ArrowDown:
                            y++;
                            break;
                        case ArrowLeft:
                            x--;
                            break;
                        case ArrowRight:
                            x++;
                            break;
                        case Escape:
                            running = false;
                            break;
                        default:
                            break;
                    }
                }

                Thread.sleep(10);
            }

            screen.stopScreen();
            terminal.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void drawSquare(TextGraphics graphics, int x, int y) {
        graphics.setCharacter(x, y, new TextCharacter('■'));
        graphics.setCharacter(x + 1, y, new TextCharacter('■'));
        graphics.setCharacter(x, y + 1, new TextCharacter('■'));
        graphics.setCharacter(x + 1, y + 1, new TextCharacter('■'));
    }
}