package com.rogawa.secretary.views.calender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class DateCard extends VerticalLayout {

    private final ScheduleServiceImpl service;
    private final LocalDate date;
    private String widthStyleStr;
    private List<Schedule> schedules = new ArrayList<>();

    private ScheduleEditor scheduleEditor; // こいつはメンバにしなくてもいいかも

    public DateCard(LocalDate date, String widthStyleStr, ScheduleServiceImpl service) {
        this.date = date;
        this.widthStyleStr = widthStyleStr;
        this.service = service;
        // ※ 初期化時点ではスケジュールは空
    }

    public VerticalLayout createDateCard() {

        VerticalLayout dateCardLayout = new VerticalLayout();
        // スタイルを設定
        dateCardLayout.setPadding(false);
        dateCardLayout.setSpacing(false);
        dateCardLayout.getStyle().set("width", "calc(100% / 7)");
        // dateCardLayout.getStyle().set("height", "100%");
        dateCardLayout.getStyle().set("border", "1px solid");

        // 日付表示を配置
        dateCardLayout.add(this.date.format(DateTimeFormatter.ofPattern("d")));

        // スケジュールのタイトルを描画
        for (Integer i = 0; i < schedules.size(); i++) {
            String scheduleTitle = schedules.get(i).getTitle();
            dateCardLayout.add(createTitleChip(scheduleTitle));
        }

        // スケジュールエディターを定義
        this.scheduleEditor = new ScheduleEditor(this.service, schedules);
        scheduleEditor.addUpdateListener(e -> {
            // スケジュール作成/更新/削除/キャンセル時の動作を定義
            this.scheduleEditor = new ScheduleEditor(this.service, schedules);// FIXME おためし
            fireEvent(new UpdateEvent(this));
        });

        // 日付カードをクリック時の動作を定義
        dateCardLayout.addClickListener(e -> {
            this.scheduleEditor.open();
        });

        return dateCardLayout;
    }

    // この日付にスケジュールを追加する
    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
    };

    // タイトルを表示するチップを作成する
    public Span createTitleChip(String title) {
        Span titleChip = new Span(title);
        return titleChip;
    }

    // イベントの設定を呼び出し側に譲渡する
    public class UpdateEvent extends ComponentEvent<DateCard> {
        public UpdateEvent(DateCard source) {
            super(source, false);
        }
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
