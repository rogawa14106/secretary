package com.rogawa.secretary.views.calender;

import java.util.List;
import java.util.ArrayList;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.rogawa.secretary.views.ScheduleForm;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.shared.Registration;

@SpringComponent
@UIScope
public class ScheduleEditor extends Dialog {

    private ScheduleServiceImpl service;
    private final ScheduleForm scheduleForm;
    private List<Schedule> schedules;

    public ScheduleEditor(ScheduleServiceImpl service, List<Schedule> schedules) {
        this.service = service;
        this.scheduleForm = createScheduleForm();
        this.schedules = schedules;
        createScheduleEditor();
    }

    public void createScheduleEditor() {
        // ダイアログのレイアウトを作る
        // スタイルを設定
        this.setWidth("100vh");
        this.setHeight("50vh");

        // スケジュールをリスト表示
        this.add(createScheduleList());

        // 新規作成ボタン表示
        this.add(createAdddingCard());
    }

    // スケジュールの一覧のレイアウト
    private VerticalLayout createScheduleList() {
        VerticalLayout scheduleList = new VerticalLayout();
        for (Integer i = 0; i < this.schedules.size(); i++) {
            HorizontalLayout scheduleCard = createScheduleItem(this.schedules.get(i));
            scheduleList.add(scheduleCard);
        }
        return scheduleList;
    }

    // 表示するitemのレイアウト
    private HorizontalLayout createScheduleItem(Schedule schedule) {
        HorizontalLayout scheduleCard = new HorizontalLayout();
        // スタイルを設定
        scheduleCard.setWidthFull();

        // 要素を追加
        scheduleCard.add(schedule.getTitle());

        // スケジュールクリック時の動作を定義
        scheduleCard.addClickListener(e -> {
            // フォームを表示
            openScheduleForm(schedule);
        });

        return scheduleCard;
    }

    // 新規作成ボタン
    private HorizontalLayout createAdddingCard() {
        HorizontalLayout addingCard = new HorizontalLayout();
        // スタイルを設定
        addingCard.setWidthFull();
        addingCard.setAlignItems(FlexComponent.Alignment.CENTER);

        // 要素を追加
        Span icon = new Span(LumoIcon.PLUS.create());
        addingCard.add(icon);

        // スケジュールクリック時の動作を定義
        addingCard.addClickListener(e -> {
            // 新規作成フォームを表示する
            openScheduleForm(new Schedule());
        });

        return addingCard;
    }

    // スケジュールフォームを作成する
    private ScheduleForm createScheduleForm() {
        ScheduleForm scheduleForm = new ScheduleForm(service);
        // スケジュール新規作成/更新/削除時の動作
        scheduleForm.addChangeListener(c -> {
            fireEvent(new UpdateEvent(this));
            // TODO ここにスケジュール一覧を再描画する処理を入れる
            scheduleForm.close();
        });
        scheduleForm.addCancelListener(c -> {
            fireEvent(new UpdateEvent(this));
            // TODO ここにスケジュール一覧を再描画する処理を入れる
            scheduleForm.close();
        });
        return scheduleForm;
    }

    // スケジュールフォームに値をセットして開く
    private void openScheduleForm(Schedule schedule) {
        scheduleForm.setSchedule(schedule);
        scheduleForm.open();
    }

    // イベントの設定を呼び出し側に譲渡する
    public class UpdateEvent extends ComponentEvent<ScheduleEditor> {
        public UpdateEvent(ScheduleEditor source) {
            super(source, false);
        }
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
