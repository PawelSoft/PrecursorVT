package org.psk.uicomponent;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.DefaultMutableThemeStyle;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Renderer przycisku używający płaskiego stylu z niestandardowym stylem tematycznym dla przycisku wybranego.
 */
public class TabFlatButtonRenderer implements Button.ButtonRenderer {

    private static final DefaultMutableThemeStyle customSelectedThemeStyle =
            new DefaultMutableThemeStyle(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT);

    /**
     * Zwraca pozycję kursora dla przycisku. W przypadku tego renderera zwraca null, ponieważ pozycja kursora nie jest używana.
     *
     * @param component Przycisk, dla którego ma być określona pozycja kursora.
     * @return Zawsze null, ponieważ ta funkcjonalność nie jest obsługiwana.
     */
    public TerminalPosition getCursorLocation(Button component) {
        return null;
    }

    /**
     * Zwraca preferowany rozmiar przycisku na podstawie szerokości tekstu etykiety.
     *
     * @param component Przycisk, dla którego ma być określony preferowany rozmiar.
     * @return Preferowany rozmiar przycisku jako TerminalSize.
     */
    public TerminalSize getPreferredSize(Button component) {
        return new TerminalSize(TerminalTextUtils.getColumnWidth(component.getLabel()), 1);
    }

    /**
     * Rysuje komponent przycisku na podanym obszarze graficznym.
     *
     * @param graphics Obiekt TextGUIGraphics używany do rysowania w interfejsie tekstowym.
     * @param button   Przycisk, który ma być narysowany.
     */
    public void drawComponent(TextGUIGraphics graphics, Button button) {

        ThemeDefinition themeDefinition = button.getThemeDefinition();


        graphics.fill(' ');


        if (button.isFocused()) {
            graphics.applyThemeStyle(customSelectedThemeStyle);
        } else {
            graphics.applyThemeStyle(themeDefinition.getNormal());
        }


        graphics.putString(0, 0, button.getLabel());
    }
}
