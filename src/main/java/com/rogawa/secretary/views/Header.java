package com.rogawa.secretary.views;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

@SpringComponent
@UIScope
public class Header extends HorizontalLayout {

    private H2 viewTitle;
    private final ScheduleForm scheduleForm;
    private final ScheduleRepository repo;

    public Header(ScheduleRepository repo, ScheduleForm scheduleForm) {
        this.repo = repo;
        this.scheduleForm = scheduleForm;
    }

    public Component createHeader() {
        viewTitle = new H2("Home");
        Div navItemSpacer = new Div(viewTitle);
        navItemSpacer.setClassName("navbar-item");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setFlexGrow(1, navItemSpacer);
        layout.setPadding(true);
        layout.add(navItemSpacer);

        // a button of filtering schedules.
        Button filterButton = new Button(VaadinIcon.FILTER.create());
        Div navItemFilterButton = new Div(filterButton);
        navItemFilterButton.setClassName("navbar-item");
        layout.add(navItemFilterButton);

        // a button of creating schedule.
        Dialog scheduleDialog = createScheduleDialog();
        Button plusButton = new Button(VaadinIcon.PLUS_CIRCLE.create(), e -> {
            scheduleForm.setSchedule(new Schedule());
            scheduleDialog.open();
        });
        Div navItemPlusButton = new Div(plusButton);
        navItemPlusButton.setClassName("navbar-item");
        layout.add(navItemPlusButton);

        return layout;
    }

    private Dialog createScheduleDialog() {
        Dialog dialog = scheduleForm.createDialog();
        scheduleForm.addChangeListener(c -> {
            dialog.close();
            // TODO リスト更新処理をどう呼ぶか
            // listSchedule();
        });
        scheduleForm.addCancelListener(c -> {
            dialog.close();
        });
        scheduleForm.setSchedule(new Schedule());
        return dialog;
    }
}
