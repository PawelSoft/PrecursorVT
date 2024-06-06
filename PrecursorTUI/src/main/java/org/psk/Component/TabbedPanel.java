package org.psk.uicomponent;

import com.googlecode.lanterna.gui2.*;

import java.util.ArrayList;
import java.util.List;

public class TabbedPanel extends Panel {

    private final List<Tab> tabs = new ArrayList<>();
    private int selectedTabIndex = 0;
    private final Panel tabContentPanel;
    private final Panel tabHeaderPanel;

    public TabbedPanel() {
        super(new BorderLayout());

        tabHeaderPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        tabContentPanel = new Panel(new BorderLayout());

        addComponent(tabHeaderPanel.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);
        addComponent(tabContentPanel, BorderLayout.Location.CENTER);
    }

    public void addTab(String title, Component component) {
        Tab tab = new Tab(title, component);
        tabs.add(tab);
        updateTabs();
    }

    private void updateTabs() {
        tabHeaderPanel.removeAllComponents();

        for (int i = 0; i < tabs.size(); i++) {
            final int tabIndex = i;
            Tab tab = tabs.get(i);
            String btnLabel = tab.title;
            Button tabButton = new Button(tab.title, () -> selectTab(tabIndex));
            tabButton.setRenderer(new TabFlatButtonRenderer());
            if (i == selectedTabIndex) {
                tabButton.setLabel("[" + btnLabel + "]");
            }
            tabHeaderPanel.addComponent(tabButton);
        }

        tabContentPanel.removeAllComponents();
        tabContentPanel.addComponent(tabs.get(selectedTabIndex).component);
        invalidate();
    }


    private void selectTab(int index) {
        selectedTabIndex = index;
        updateTabs();
    }

    private void nextTab() {
        selectedTabIndex = (selectedTabIndex + 1) % tabs.size();
        updateTabs();
    }

    private void previousTab() {
        selectedTabIndex = (selectedTabIndex - 1 + tabs.size()) % tabs.size();
        updateTabs();
    }

    private static class Tab {
        String title;
        Component component;

        Tab(String title, Component component) {
            this.title = title;
            this.component = component;
        }
    }
}
