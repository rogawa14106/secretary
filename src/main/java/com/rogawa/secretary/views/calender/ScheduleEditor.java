package com.rogawa.secretary.views.calender;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.rogawa.secretary.model.Schedule;
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
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.shared.Registration;

@SpringComponent
@UIScope
public class ScheduleEditor extends Dialog {

    private final ScheduleForm scheduleForm;
    private List<Schedule> schedules;
    private LocalDate date;

    public ScheduleEditor(ScheduleForm scheduleForm) {
        this.scheduleForm = scheduleForm;
        // スケジュール新規作成/更新/削除時の動作
        scheduleForm.addChangeListener(c -> {
            fireEvent(new UpdateEvent(this));
            scheduleForm.close();
        });
        scheduleForm.addCancelListener(c -> {
            fireEvent(new UpdateEvent(this));
            scheduleForm.close();
        });

        // 自身のスタイルを設定
        this.setWidth("98vw");
        this.setHeight("40vh");
        this.setTop("55%");
    }

    public void initScheduleEditor(LocalDate date, List<Schedule> schedules) {
        // 値を設定
        this.date = date;
        this.schedules = schedules;

        // ダイアログのレイアウトを作る
        placeItems();
    }

    // ダイアログのレイアウトを作る
    public void placeItems() {
        // 子要素をすべて削除
        this.removeAll();

        // ヘッダに日付を入れる
        this.setHeaderTitle(this.date.format(DateTimeFormatter.ofPattern("M月d日")) + "の予定");

        // スケジュール一覧を配置
        this.add(createScheduleList());

        // 新規作成ボタン表示
        this.add(createAddBtnItem());
    }

    // スケジュール一覧のレイアウトを作成
    private VerticalLayout createScheduleList() {
        VerticalLayout scheduleList = new VerticalLayout();
        scheduleList.setPadding(false);
        scheduleList.setSpacing(false);

        for (Integer i = 0; i < this.schedules.size(); i++) {
            // スケジュールアイテムを追加
            HorizontalLayout scheduleCard = createScheduleItem(this.schedules.get(i));
            scheduleList.add(scheduleCard);
        }
        return scheduleList;
    }

    // スケジュールの一覧の各アイテムのレイアウト
    private HorizontalLayout createScheduleItem(Schedule schedule) {
        HorizontalLayout scheduleCard = new HorizontalLayout();

        // スケジュールアイテムのスタイルを設定
        scheduleCard.setPadding(true);
        scheduleCard.setWidthFull();
        scheduleCard.addClassNames(
                LumoUtility.Border.BOTTOM,
                LumoUtility.BoxShadow.XSMALL,
                LumoUtility.FontSize.SMALL);

        // 要素を追加していく
        // スケジュールの開始/終了時刻を追加
        scheduleCard.add(createTimeLayout(schedule));

        // スケジュールのタイトルを追加
        scheduleCard.add(schedule.getTitle());

        // スケジュールクリック時の動作を定義
        scheduleCard.addClickListener(e -> {
            // フォームを表示
            openScheduleForm(schedule);
        });

        return scheduleCard;
    }

    // スケジュールの一覧に表示するアイテムのうち、時刻部分のレイアウト
    private HorizontalLayout createTimeLayout(Schedule schedule) {
        HorizontalLayout timeLayout = new HorizontalLayout();

        // 表示する文字列
        String allDayTxt = "終日"; // 終日予定のときに表示するテキスト
        String rangeTxt = "~"; // 範囲を表す記号。開始時間と終了時間の間にこの文字を入れる

        // 終日予定だったら終日と表示して早期リターン
        if (schedule.getIsAllDay() == true) {
            timeLayout.add(allDayTxt);
            return timeLayout;
        }

        // 開始/終了日を取得
        LocalDateTime startDate = schedule.getDatetime();
        LocalDateTime endDate = schedule.getEndDatetime();

        // 今日中に開始/終了する予定かどうかの真偽値
        Boolean isStartToday = this.date.isEqual(startDate.toLocalDate());
        Boolean isEndToday = this.date.isEqual(endDate.toLocalDate());

        // 今日以前に始まり、今日以降に終わる場合は終日と表示して早期リターン
        if (!isStartToday && !isEndToday) {
            timeLayout.add(allDayTxt);
            return timeLayout;
        }

        String dispTxt = ""; // 表示するテキスト
        String timeFormatPattern = "H:mm"; // 時間のフォーマット文字列
        // 開始/終了日とこのクラス自身のdateを比較し、今日(this.date)中に開始/終了するかどうか判定
        // 予定が今日始まる場合は開始時刻を追加
        if (isStartToday) {
            dispTxt = dispTxt + startDate.format(DateTimeFormatter.ofPattern(timeFormatPattern));
        }
        // 範囲を表す記号を追加
        dispTxt = dispTxt + rangeTxt;
        // 予定が今日終わる場合は終了時刻を追加
        if (isEndToday) {
            dispTxt = dispTxt + endDate.format(DateTimeFormatter.ofPattern(timeFormatPattern));
        }

        timeLayout.add(dispTxt);
        return timeLayout;
    }

    // 新規作成ボタン
    private HorizontalLayout createAddBtnItem() {
        HorizontalLayout addBtnItem = new HorizontalLayout();
        addBtnItem.setPadding(true);

        // スタイルを設定
        addBtnItem.setWidthFull();
        addBtnItem.setAlignItems(FlexComponent.Alignment.CENTER);

        // 要素を追加
        Span icon = new Span(LumoIcon.PLUS.create());
        icon.getStyle().set("margin", "0 auto");
        addBtnItem.add(icon);

        // スケジュールクリック時の動作を定義
        addBtnItem.addClickListener(e -> {
            // 新規作成フォームを表示する
            openScheduleForm(new Schedule());
        });

        return addBtnItem;
    }

    // スケジュールフォームに値をセットして開く
    private void openScheduleForm(Schedule schedule) {
        // 新規作成のときはデフォルトの時間をセットする
        if (schedule.getId() == null) {
            LocalDateTime baseDateTime = this.date.atTime(LocalTime.now()).withMinute(0);
            schedule.setDatetime(baseDateTime);
            schedule.setEndDatetime(baseDateTime.plusHours(1));
        }
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
