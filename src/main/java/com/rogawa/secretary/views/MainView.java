package com.rogawa.secretary.views;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;

//import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route
// public class MainView extends VerticalLayout {
@PageTitle("Securetary")
public class MainView extends AppLayout {

    private final ScheduleRepository repo;
    // final Grid<Schedule> grid;

    // private final ScheduleForm scheduleForm;

    private final Header header;

    public MainView(ScheduleRepository repo, ScheduleForm scheduleForm, Header header) {
        this.repo = repo;
        this.header = header;

        // ヘッダーを配置
        addToNavbar(header.createHeader());

        // スケジュール一覧を配置
        setContent(createContent());
    }

    private Component createContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.add("content");
        // glayout.add(grid);
        listSchedule();
        return layout;
    }

    private void listSchedule() {
        // grid.setItems(repo.findAll());
    }

}

// Have the drawer toggle button on the left
// layout.add(new DrawerToggle());

// A user icon
// layout.add(new Image("images/user.svg", "Avatar"));
// HorizontalLayout layout = new HorizontalLayout();
//
// // Configure styling for the header
// layout.setId("header");
// layout.getThemeList().set("dark", true);
// layout.setWidthFull();
// layout.setSpacing(false);
// layout.setAlignItems(FlexComponent.Alignment.CENTER);
//
// // Placeholder for the title of the current view.
// // The title will be set after navigation.
// viewTitle = new H1("Home");
// layout.add(viewTitle);
//
