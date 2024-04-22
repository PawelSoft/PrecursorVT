package org.psk;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.*;

public class MyComponent implements Component {
    @Override
    public TerminalPosition getPosition() {
        return null;
    }

    @Override
    public TerminalPosition getGlobalPosition() {
        return null;
    }

    @Override
    public Component setPosition(TerminalPosition terminalPosition) {
        return null;
    }

    @Override
    public TerminalSize getSize() {
        return null;
    }

    @Override
    public Component setSize(TerminalSize terminalSize) {
        return null;
    }

    @Override
    public TerminalSize getPreferredSize() {
        return null;
    }

    @Override
    public Component setPreferredSize(TerminalSize terminalSize) {
        return null;
    }

    @Override
    public Component setLayoutData(LayoutData layoutData) {
        return null;
    }

    @Override
    public LayoutData getLayoutData() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public Component setVisible(boolean b) {
        return null;
    }

    @Override
    public Container getParent() {
        return null;
    }

    @Override
    public boolean hasParent(Container container) {
        return false;
    }

    @Override
    public TextGUI getTextGUI() {
        return null;
    }

    @Override
    public Theme getTheme() {
        return null;
    }

    @Override
    public ThemeDefinition getThemeDefinition() {
        return null;
    }

    @Override
    public Component setTheme(Theme theme) {
        return null;
    }

    @Override
    public boolean isInside(Container container) {
        return false;
    }

    @Override
    public ComponentRenderer<? extends Component> getRenderer() {
        return null;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public Border withBorder(Border border) {
        return null;
    }

    @Override
    public TerminalPosition toBasePane(TerminalPosition terminalPosition) {
        return null;
    }

    @Override
    public TerminalPosition toGlobal(TerminalPosition terminalPosition) {
        return null;
    }

    @Override
    public BasePane getBasePane() {
        return null;
    }

    @Override
    public Component addTo(Panel panel) {
        return null;
    }

    @Override
    public void onAdded(Container container) {

    }

    @Override
    public void onRemoved(Container container) {

    }

    @Override
    public void draw(TextGUIGraphics textGUIGraphics) {

    }

    @Override
    public boolean isInvalid() {
        return false;
    }
}
