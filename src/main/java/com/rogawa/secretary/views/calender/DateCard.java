package com.rogawa.secretary.views.calender;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;

@SpringComponent
@UIScope
public class DateCard extends VerticalLayout {

    private final ScheduleServiceImpl service;
    private LocalDate date;
    private String widthStyleStr;
    private List<Schedule> schedules = new ArrayList<>();

    private ScheduleEditor scheduleEditor; // こいつはメンバにしなくてもいいかも

    public DateCard(String widthStyleStr, ScheduleServiceImpl service) {
        this.widthStyleStr = widthStyleStr;
        this.service = service;
        // スケジュールエディターを定義
        this.scheduleEditor = new ScheduleEditor(this.service);
        this.scheduleEditor.addUpdateListener(e -> {
            // スケジュール作成/更新/削除/キャンセル時のscheduleEditorの動作を定義
            fireEvent(new UpdateEvent(this));
        });

        // 日付カードをクリック時の動作を定義
        this.addClickListener(e -> {
            this.scheduleEditor.initScheduleEditor(); // スケジュールエディタを再描画
            this.scheduleEditor.open(); // スケジュールエディタを開く
        });
        // ※ 初期化時点では日付、スケジュールは空
    }

    // 日付をセットする
    public void setDate(LocalDate date) {
        this.date = date;
        this.scheduleEditor.setDate(date);
    }

    // スケジュールを追加する
    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        this.scheduleEditor.addSchedule(schedule);
    };

    // すべてのスケジュールを削除する
    public void removeAllSchedules() {
        this.schedules = new ArrayList<>();
        this.scheduleEditor.removeAllSchedules();
    }

    // レイアウトを作成する
    public void initDateCard() {
        placeItems();
    }

    private void placeItems() {
        // 小要素をすべて削除
        this.removeAll();

        // スタイルを設定
        this.setPadding(false);
        this.setSpacing(false);
        this.getStyle().set("width", this.widthStyleStr);
        this.addClassNames(
                LumoUtility.Border.LEFT,
                LumoUtility.Border.TOP,
                LumoUtility.BoxShadow.XSMALL,
                LumoUtility.FontSize.XSMALL);
        if (this.date.equals(LocalDate.now())) {
            // 日付が今日の場合は背景色を変える
            this.addClassName(LumoUtility.Background.PRIMARY_10);
        }

        // 日付表示を配置
        this.add(createDateChip());

        // スケジュールのタイトルを描画
        for (Integer i = 0; i < schedules.size(); i++) {
            String title = schedules.get(i).getTitle();
            String owner = schedules.get(i).getOwner();
            this.add(createTitleChip(title, owner));
        }
    }

    public HorizontalLayout createDateChip() {
        HorizontalLayout dateChip = new HorizontalLayout();
        dateChip.setHeight("1rem");
        dateChip.getStyle().set("line-height", "1rem");
        dateChip.getStyle().set("padding-left", "0.1rem");

        // 日付レイアウトを作成
        String dateTxt = this.date.format(DateTimeFormatter.ofPattern("d"));
        Span dateSpan = new Span(dateTxt);

        // 休日は色を付ける
        // TODO 祝日も色つけたい。
        // => google calender APIを非同期で実行するか、休日DBを作るか、休日定義するクラス作って静的に判定するか
        if (this.date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            // 土曜は青くする
            dateSpan.addClassName(LumoUtility.TextColor.PRIMARY);
        } else if (this.date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            // 日曜は赤くする
            dateSpan.addClassName(LumoUtility.TextColor.ERROR);
        }

        // 配置
        dateChip.add(dateSpan);
        return dateChip;
    }

    // タイトルを表示するチップを作成する
    public HorizontalLayout createTitleChip(String title, String owner) {
        HorizontalLayout titleChip = new HorizontalLayout();
        titleChip.setHeight("1rem");
        titleChip.setWidth("100%");
        titleChip.setPadding(false);
        titleChip.setAlignItems(FlexComponent.Alignment.START);
        titleChip.getStyle().set("background-color", service.generateOwnerColorCode(owner));
        titleChip.getStyle().set("line-height", "1rem");
        titleChip.getStyle().set("border-radius", "2px");
        titleChip.getStyle().set("padding-left", "0.1rem");
        titleChip.getStyle().set("margin-bottom", "0.1rem");
        titleChip.addClassNames(
                LumoUtility.Whitespace.NOWRAP,
                LumoUtility.TextOverflow.CLIP,
                LumoUtility.Overflow.HIDDEN);

        // タイトル
        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "0.7rem");
        titleSpan.addClassNames(LumoUtility.FontWeight.BOLD);

        // タイトルを追加
        titleChip.add(titleSpan);
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
