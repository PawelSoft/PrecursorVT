package org.psk;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import javax.swing.*;
import java.util.HashSet;


public class MainWindow extends BasicWindow {
    private RGBConverterWindow rgbconverterwindow;
    private NumberSystemConverterWindow numbersystemconverterwindow;
    private MultiWindowTextGUI gui;
    public MainWindow(MultiWindowTextGUI gui) {
        super("PrecursorVT");
        this.gui = gui;
        rgbconverterwindow = new RGBConverterWindow(gui,this);
        numbersystemconverterwindow = new NumberSystemConverterWindow(gui,this);
        initWindow();

    }

    private void initWindow() {

        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Window.Hint.NO_POST_RENDERING);
        newHints.add(Hint.CENTERED);
        setHints(newHints);


        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        Button buttonrgbconverterwindow = new Button("RGBConverter", new Runnable() {
            @Override
            public void run() {
                gui.removeWindow(MainWindow.this);
                gui.addWindowAndWait(rgbconverterwindow);
            }
        });
        buttonrgbconverterwindow.setPreferredSize(new TerminalSize(40, 2));
        Button buttonnumbersystemconverterwindow= new Button("NumberSystemConverter", new Runnable() {
            @Override
            public void run() {
                gui.removeWindow(MainWindow.this);
                gui.addWindowAndWait(numbersystemconverterwindow);
            }
        });
        buttonnumbersystemconverterwindow.setPreferredSize(new TerminalSize(40, 2));




        panel.addComponent(buttonrgbconverterwindow);
        panel.addComponent(buttonnumbersystemconverterwindow);


        panel.addComponent(new EmptySpace());
        Button buttonclosed= new Button("Wyjdz z programu", new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        });
        buttonclosed.setPreferredSize(new TerminalSize(40, 2));
        panel.addComponent(buttonclosed);
        setComponent(panel);
    }
}
