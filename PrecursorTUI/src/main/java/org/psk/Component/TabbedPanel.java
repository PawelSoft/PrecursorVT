package org.psk.uicomponent;

import com.googlecode.lanterna.gui2.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel zakładek umożliwiający przełączanie się między różnymi zawartościami.
 */
public class TabbedPanel extends Panel {

    private final List<Tab> tabs = new ArrayList<>();
    private int selectedTabIndex = 0;
    private final Panel tabContentPanel;
    private final Panel tabHeaderPanel;

    /**
     * Konstruktor ustawiający układ panelu na BorderLayout i inicjujący panele nagłówków i zawartości zakładek.
     */
    public TabbedPanel() {
        super(new BorderLayout());


        tabHeaderPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        tabContentPanel = new Panel(new BorderLayout());

        addComponent(tabHeaderPanel.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);
        addComponent(tabContentPanel, BorderLayout.Location.CENTER);
    }

    /**
     * Dodaje nową zakładkę do panelu.
     *
     * @param title     Tytuł zakładki.
     * @param component Komponent do wyświetlenia w zakładce.
     */
    public void addTab(String title, Component component) {
        Tab tab = new Tab(title, component);
        tabs.add(tab);
        updateTabs();
    }

    /**
     * Aktualizuje wyświetlane zakładki oraz ich zawartość.
     */
    private void updateTabs() {
        tabHeaderPanel.removeAllComponents();

        for (int i = 0; i < tabs.size(); i++) {
            final int tabIndex = i;
            Tab tab = tabs.get(i);
            String btnLabel = tab.title;
            Button tabButton = new Button(tab.title, () -> selectTab(tabIndex));
            tabButton.setRenderer(new org.psk.uicomponent.TabFlatButtonRenderer());
            if (i == selectedTabIndex) {
                tabButton.setLabel("[" + btnLabel + "]");
            }
            tabHeaderPanel.addComponent(tabButton);
        }

        tabContentPanel.removeAllComponents();
        tabContentPanel.addComponent(tabs.get(selectedTabIndex).component);
        invalidate();
    }

    /**
     * Wybiera zakładkę o podanym indeksie.
     *
     * @param index Indeks wybranej zakładki.
     */
    private void selectTab(int index) {
        selectedTabIndex = index;
        updateTabs();
    }

    /**
     * Przechodzi do następnej zakładki.
     */
    private void nextTab() {
        selectedTabIndex = (selectedTabIndex + 1) % tabs.size();
        updateTabs();
    }

    /**
     * Przechodzi do poprzedniej zakładki.
     */
    private void previousTab() {
        selectedTabIndex = (selectedTabIndex - 1 + tabs.size()) % tabs.size();
        updateTabs();
    }

    /**
     * Klasa wewnętrzna reprezentująca pojedynczą zakładkę.
     */
    private static class Tab {
        String title;
        Component component;

        /**
         * Konstruktor tworzący nową zakładkę.
         *
         * @param title     Tytuł zakładki.
         * @param component Komponent do wyświetlenia w zakładce.
         */
        Tab(String title, Component component) {
            this.title = title;
            this.component = component;
        }
    }
}
