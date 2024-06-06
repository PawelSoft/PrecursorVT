package org.psk.uicomponent;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.DefaultMutableThemeStyle;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

public class TabFlatButtonRenderer implements Button.ButtonRenderer {

    // Definicja niestandardowego stylu tematycznego dla wybranego przycisku
    private static final DefaultMutableThemeStyle customSelectedThemeStyle =
            new DefaultMutableThemeStyle(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT);

    // Zwraca pozycję kursora dla przycisku (tutaj nie jest potrzebne, więc zwraca null)
    public TerminalPosition getCursorLocation(Button component) {
        return null;
    }

    // Zwraca preferowany rozmiar przycisku na podstawie szerokości tekstu etykiety
    public TerminalSize getPreferredSize(Button component) {
        return new TerminalSize(TerminalTextUtils.getColumnWidth(component.getLabel()), 1);
    }

    // Rysuje komponent przycisku
    public void drawComponent(TextGUIGraphics graphics, Button button) {
        // Pobiera definicję stylu tematycznego dla przycisku
        ThemeDefinition themeDefinition = button.getThemeDefinition();

        // Wypełnia cały obszar przycisku spacjami
        graphics.fill(' ');

        // Zastosowanie niestandardowego stylu, jeśli przycisk jest wybrany, w przeciwnym razie używa normalnego stylu
        if (button.isFocused()) {
            graphics.applyThemeStyle(customSelectedThemeStyle);
        } else {
            graphics.applyThemeStyle(themeDefinition.getNormal());
        }

        // Umieszcza tekst etykiety przycisku na pozycji (0,0) w obszarze graficznym
        graphics.putString(0, 0, button.getLabel());
    }
}
