package com.rogawa.secretary.views.calender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.rogawa.secretary.model.Schedule;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class DateCard extends VerticalLayout {

    private final LocalDate date;
    private List<Schedule> schedules = new ArrayList<>();

    public DateCard(LocalDate date) {
        this.date = date;
    }

    public VerticalLayout createDateCard() {
        VerticalLayout dateCardLayout = new VerticalLayout();
        dateCardLayout.getStyle().set("width", "calc(100% / 7)");
        // dateCardLayout.getStyle().set("height", "100%");

        // test FIXME
        dateCardLayout.getStyle().set("border", "1px solid");
        dateCardLayout.add(this.date.format(DateTimeFormatter.ofPattern("d")));
        // if (schedules != null) {
        // dateCardLayout.add(String.valueOf(schedules.get(0).getId()));
        // }

        return dateCardLayout;
    }

    public void addSchedules(Schedule schedule) {
        this.schedules.add(schedule);
    };
}
