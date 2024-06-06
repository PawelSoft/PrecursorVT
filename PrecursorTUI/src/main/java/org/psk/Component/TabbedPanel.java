package org.psk.uicomponent;

import com.googlecode.lanterna.gui2.*;

import java.util.ArrayList;
import java.util.List;

public class TabbedPanel extends Panel {

    // Lista przechowująca wszystkie zakładki
    private final List<Tab> tabs = new ArrayList<>();
    // Indeks aktualnie wybranej zakładki
    private int selectedTabIndex = 0;
    // Panel do wyświetlania zawartości zakładek
    private final Panel tabContentPanel;
    // Panel do wyświetlania nagłówków zakładek
    private final Panel tabHeaderPanel;

    // Konstruktor ustawiający układ panelu na BorderLayout
    public TabbedPanel() {
        super(new BorderLayout());

        // Inicjalizacja panelu nagłówków zakładek z układem poziomym
        tabHeaderPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        // Inicjalizacja panelu zawartości zakładek z układem BorderLayout
        tabContentPanel = new Panel(new BorderLayout());

        // Dodanie panelu nagłówków z obramowaniem na górze oraz panelu zawartości na środku
        addComponent(tabHeaderPanel.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);
        addComponent(tabContentPanel, BorderLayout.Location.CENTER);
    }

    // Metoda dodająca nową zakładkę do panelu
    public void addTab(String title, Component component) {
        Tab tab = new Tab(title, component);
        tabs.add(tab);
        updateTabs();
    }

    // Aktualizuje wyświetlane zakładki i zawartość
    private void updateTabs() {
        tabHeaderPanel.removeAllComponents();

        // Tworzenie przycisków dla każdej zakładki
        for (int i = 0; i < tabs.size(); i++) {
            final int tabIndex = i;
            Tab tab = tabs.get(i);
            String btnLabel = tab.title;
            Button tabButton = new Button(tab.title, () -> selectTab(tabIndex));
            tabButton.setRenderer(new TabFlatButtonRenderer());
            // Dodanie nawiasów do etykiety przycisku, jeśli jest to aktualnie wybrana zakładka
            if (i == selectedTabIndex) {
                tabButton.setLabel("[" + btnLabel + "]");
            }
            tabHeaderPanel.addComponent(tabButton);
        }

        // Aktualizacja zawartości zakładki
        tabContentPanel.removeAllComponents();
        tabContentPanel.addComponent(tabs.get(selectedTabIndex).component);
        invalidate();
    }

    // Wybiera zakładkę o podanym indeksie
    private void selectTab(int index) {
        selectedTabIndex = index;
        updateTabs();
    }

    // Przechodzi do następnej zakładki
    private void nextTab() {
        selectedTabIndex = (selectedTabIndex + 1) % tabs.size();
        updateTabs();
    }

    // Przechodzi do poprzedniej zakładki
    private void previousTab() {
        selectedTabIndex = (selectedTabIndex - 1 + tabs.size()) % tabs.size();
        updateTabs();
    }

    // Klasa wewnętrzna reprezentująca pojedynczą zakładkę
    private static class Tab {
        String title;      // Tytuł zakładki
        Component component; // Komponent do wyświetlenia w zakładce

        Tab(String title, Component component) {
            this.title = title;
            this.component = component;
        }
    }
}
