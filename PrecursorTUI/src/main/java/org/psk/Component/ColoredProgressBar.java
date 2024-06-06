package org.psk.Component;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.DefaultMutableThemeStyle;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.ProgressBar;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

public class ColoredProgressBar extends ProgressBar {

    // Definicje stylów dla różnych kolorów tła paska postępu
    private static final DefaultMutableThemeStyle redThemeStyle = new DefaultMutableThemeStyle(TextColor.ANSI.WHITE, TextColor.ANSI.RED);
    private static final DefaultMutableThemeStyle greenThemeStyle = new DefaultMutableThemeStyle(TextColor.ANSI.WHITE, TextColor.ANSI.GREEN);
    private static final DefaultMutableThemeStyle blueThemeStyle = new DefaultMutableThemeStyle(TextColor.ANSI.WHITE, TextColor.ANSI.BLUE_BRIGHT);

    // Wybrany styl tematyczny, który zostanie zastosowany do paska postępu
    private DefaultMutableThemeStyle selectedThemeStyle;

    // Konstruktor przyjmujący minimalną i maksymalną wartość, preferowaną szerokość oraz kolor paska postępu
    public ColoredProgressBar(int min, int max, int preferredWidth, TextColor.ANSI color) {
        super(min, max, preferredWidth);

        // Ustawienie wybranego stylu tematycznego na podstawie podanego koloru
        if (color == TextColor.ANSI.RED) {
            selectedThemeStyle = redThemeStyle;
        }
        else if (color == TextColor.ANSI.GREEN) {
            selectedThemeStyle = greenThemeStyle;
        }
        else if (color == TextColor.ANSI.BLUE) {
            selectedThemeStyle = blueThemeStyle;
        }
    }

    // Tworzy domyślny renderer dla paska postępu, który będzie używany do rysowania komponentu
    @Override
    protected ComponentRenderer<ProgressBar> createDefaultRenderer() {
        return new CustomProgressBarRenderer();
    }

    // Klasa wewnętrzna definiująca renderer dla paska postępu
    public static class CustomProgressBarRenderer implements ComponentRenderer<ProgressBar> {
        public CustomProgressBarRenderer() {
        }

        // Zwraca preferowany rozmiar dla paska postępu
        public TerminalSize getPreferredSize(ProgressBar component) {
            int preferredWidth = component.getPreferredWidth();
            if (preferredWidth > 0) {
                return new TerminalSize(preferredWidth, 1);
            } else {
                return component.getLabelFormat() != null && !component.getLabelFormat().trim().isEmpty()
                        ? new TerminalSize(TerminalTextUtils.getColumnWidth(String.format(component.getLabelFormat(), 100.0F)) + 2, 1)
                        : new TerminalSize(10, 1);
            }
        }

        // Rysuje komponent paska postępu
        public void drawComponent(TextGUIGraphics graphics, ProgressBar component) {
            TerminalSize size = graphics.getSize();
            if (size.getRows() != 0 && size.getColumns() != 0) {
                ThemeDefinition themeDefinition = component.getThemeDefinition();
                int columnOfProgress = (int)(component.getProgress() * (float)size.getColumns());
                String label = component.getFormattedLabel();
                int labelRow = size.getRows() / 2;
                int labelWidth = TerminalTextUtils.getColumnWidth(label);
                if (labelWidth > size.getColumns()) {
                    boolean tail = true;
                    while (labelWidth > size.getColumns()) {
                        if (tail) {
                            label = label.substring(0, label.length() - 1);
                        } else {
                            label = label.substring(1);
                        }
                        tail = !tail;
                        labelWidth = TerminalTextUtils.getColumnWidth(label);
                    }
                }

                int labelStartPosition = (size.getColumns() - labelWidth) / 2;

                for(int row = 0; row < size.getRows(); ++row) {
                    // Rzutowanie komponentu na ColoredProgressBar w celu uzyskania wybranego koloru
                    ColoredProgressBar coloredBar = (ColoredProgressBar) component;

                    // Zastosowanie wybranego stylu tematycznego (koloru)
                    graphics.applyThemeStyle(coloredBar.getSelectedColor());

                    for(int column = 0; column < size.getColumns(); ++column) {
                        if (column == columnOfProgress) {
                            graphics.applyThemeStyle(themeDefinition.getNormal());
                        }

                        if (row == labelRow && column >= labelStartPosition && column < labelStartPosition + labelWidth) {
                            char character = label.charAt(TerminalTextUtils.getStringCharacterIndex(label, column - labelStartPosition));
                            graphics.setCharacter(column, row, character);
                            if (TerminalTextUtils.isCharDoubleWidth(character)) {
                                ++column;
                                if (column == columnOfProgress) {
                                    graphics.applyThemeStyle(themeDefinition.getNormal());
                                }
                            }
                        } else {
                            graphics.setCharacter(column, row, themeDefinition.getCharacter("FILLER", ' '));
                        }
                    }
                }

            }
        }
    }

    // Zwraca wybrany styl tematyczny (kolor)
    public DefaultMutableThemeStyle getSelectedColor() {
        return selectedThemeStyle;
    }
}
