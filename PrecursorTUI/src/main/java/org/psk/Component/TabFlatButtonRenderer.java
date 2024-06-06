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

    private static final DefaultMutableThemeStyle customSelectedThemeStyle =
            new DefaultMutableThemeStyle(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT);

    public TerminalPosition getCursorLocation(Button component) {
        return null;
    }

    public TerminalSize getPreferredSize(Button component) {
        return new TerminalSize(TerminalTextUtils.getColumnWidth(component.getLabel()), 1);
    }

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
