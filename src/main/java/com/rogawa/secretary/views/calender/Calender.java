package com.rogawa.secretary.views.calender;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class Calender extends VerticalLayout {
    private final ScheduleServiceImpl service;
    private final ScheduleRepository repo;

    private final ScheduleEditor scheduleEditor;

    // 定数
    private static final Integer WEEK_DAY_CNT = 7; // 一週間の日数
    private static final Integer MAX_DRAWING_DATES = 42; // カレンダーに描画する日付数の最大値

    //
    private LocalDate firstDayOfCalender; // カレンダーの最初に描画される日付
    private LocalDate[] drawingDates; // 描画する日付の配列
    private List<Schedule> drawingSchedules = new ArrayList<Schedule>(); // 描画するスケジュールのリスト
    private List<DateCard> dateCards = new ArrayList<DateCard>(); // カレンダーの各日付のレイアウトのリスト
    private DateCard selectedDateCard; // クリックされたDateCard
    private LocalDate targetYearMonth; // カレンダーの月
    private DayOfWeek fixedDayOfWeek; // 週頭固定曜日

    public Calender(ScheduleRepository repo, ScheduleServiceImpl service, ScheduleEditor scheduleEditor) {
        this.repo = repo;
        this.service = service;
        this.scheduleEditor = scheduleEditor;

        // 日付ぶんのDataCardを作成してリストに追加する
        for (Integer i = 0; i < MAX_DRAWING_DATES; i++) {
            DateCard dateCard = new DateCard("calc(100% / " + WEEK_DAY_CNT + ")", this.service, scheduleEditor);
            // dateCardクリック時の動作
            dateCard.addClickListener(e -> {
                // クリックされたDateCardをカレンダークラスに保持しておく
                this.selectedDateCard = dateCard;
            });

            this.dateCards.add(dateCard);
        }

        // 予定情報更新時の動作を定義
        scheduleEditor.addUpdateListener(e -> {
            System.out.println("### updateEvent fired (scheduleEditor)");

            // カレンダー情報がアップデートされる ←event発火時の動作はMainViewで設定
            fireEvent(new UpdateEvent(this));

            // アップデートされた情報をもとにscheduleEditorを再描画する
            LocalDate date = this.selectedDateCard.getDate();
            List<Schedule> schedules = this.selectedDateCard.getSchedules();
            this.scheduleEditor.initScheduleEditor(date, schedules);
        });
    }

    public void initCalender(LocalDate targetYearMonth, DayOfWeek fixedDayOfWeek) {
        System.out.println("### initCalender(" + targetYearMonth + ") ########################");
        // 描画する月のカレンダーの最初の日を確定
        this.targetYearMonth = targetYearMonth;
        this.fixedDayOfWeek = fixedDayOfWeek;
        this.firstDayOfCalender = searchFirstDayOfCalender(targetYearMonth);

        // カレンダーに描画する範囲の日付を取得;
        this.drawingDates = getDrawingDates();

        // DateCardに日付を入れる
        setDateCardsDate();

        // カレンダーに描画するスケジュールを取得;
        this.drawingSchedules = retrieveDrawingSchedules();

        // DateCardにスケジュールを入れる
        setDateCardsSchedule();

        // 小要素を配置
        placeDateCards();
    }

    // カレンダーにすべてのDateCardを配置する
    private void placeDateCards() {
        // 子要素をすべて削除
        this.removeAll();

        // カレンダーのレイアウトを作成
        this.setPadding(false);
        this.setSpacing(false);
        this.getStyle().set("width", "100%");
        this.getStyle().set("height", "100%");

        // 各日付オブジェクトを配置する
        for (Integer i = 0; i < MAX_DRAWING_DATES / WEEK_DAY_CNT; i++) {
            // 週のレイアウトを作成
            HorizontalLayout weekLayout = new HorizontalLayout();
            // スタイルを設定
            weekLayout.setPadding(false);
            weekLayout.setSpacing(false);
            weekLayout.getStyle().set("width", "100%");
            weekLayout.getStyle().set("height", "calc(100% / " + MAX_DRAWING_DATES / WEEK_DAY_CNT + ")");

            // dateCardを配置
            for (Integer j = 0; j < WEEK_DAY_CNT; j++) {
                // 日付オブジェクトを週のレイアウトに7個ずつ配置
                DateCard dateCard = this.dateCards.get(i * WEEK_DAY_CNT + j);
                dateCard.initDateCard(targetYearMonth);
                weekLayout.add(dateCard);
            }
            this.add(weekLayout);
        }
    }

    // すべてのDataCardに日付をセットする
    private void setDateCardsDate() {
        // DateCardに日付をセットする
        for (Integer i = 0; i < MAX_DRAWING_DATES; i++) {
            LocalDate date = this.drawingDates[i];
            this.dateCards.get(i).setDate(date);
        }
    }

    // すべてのDataCardにスケジュールをセットする
    private void setDateCardsSchedule() {
        // すべてのdateCardsからスケジュールを削除する
        for (Integer i = 0; i < MAX_DRAWING_DATES; i++) {
            dateCards.get(i).removeAllSchedules();
        }

        // 描画対象のスケジュールをループして、スケジュールの日付に当てはまる場合はdateCardsに追加していく
        for (Integer i = 0; i < this.drawingSchedules.size(); i++) {
            // スケジュールの開始日から終了日までに存在する日付の日付オブジェクトに予定を追加する
            // カレンダーの最初の日とスケジュールの開始日を比較して、入れるべき日付オブジェクトのインデックスを作成する
            Schedule schedule = drawingSchedules.get(i);
            // スケジュールの開始日と終了日を取得
            LocalDate scheduleStartDate = schedule.getDatetime().toLocalDate();
            LocalDate scheduleEndDate = schedule.getEndDatetime().toLocalDate();
            // カレンダーの最初の日から何日目に予定を入れればよいか計算する
            Integer startIdx = scheduleStartDate.getDayOfYear() - firstDayOfCalender.getDayOfYear();
            // スケジュールが何日あるか計算する
            Integer scheduleRange = scheduleEndDate.getDayOfYear() - scheduleStartDate.getDayOfYear() + 1;
            // DateCardにスケジュールを入れる
            for (Integer j = 0; j < scheduleRange; j++) {
                Integer targetDayIdx = startIdx + j; // カレンダーの最初の日から何日進んだかを表すインデックス

                // カレンダー内に収まっているスケジュールのみをDateCardに入れる
                if (targetDayIdx > -1 && targetDayIdx < MAX_DRAWING_DATES) {
                    dateCards.get(startIdx + j).addSchedule(schedule);
                }
            }
        }
    }

    // カレンダーに描画する対象のスケジュールを取得する
    private List<Schedule> retrieveDrawingSchedules() {
        // カレンダーの開始日/終了日をLocalDateTimeに整形する
        LocalDateTime start_day = this.drawingDates[0].atStartOfDay();
        LocalDateTime end_day = this.drawingDates[MAX_DRAWING_DATES - 1].atStartOfDay();

        // 開始日〜終了日までの間に入っている予定をDBから取得
        List<Schedule> drawingSchedules = repo.findAllByDateRange(start_day, end_day);

        return drawingSchedules;
    }

    // カレンダーに描画する対象になる日付を取得する
    private LocalDate[] getDrawingDates() {
        // 描画する日付配列を初期化
        LocalDate[] drawingDates = new LocalDate[MAX_DRAWING_DATES];

        // カレンダーの最初の月~描画MAX日数分の日付配列を作成
        for (Integer i = 0; i < MAX_DRAWING_DATES; i++) {
            drawingDates[i] = firstDayOfCalender.plusDays(i);
        }

        return drawingDates;
    }

    // 月ごとのカレンダー上で最初の日になる日付を探す
    private LocalDate searchFirstDayOfCalender(LocalDate targetYearMonth) {
        // その月の最初の日を取得
        LocalDate firstDayOfMonth = targetYearMonth.with(TemporalAdjusters.firstDayOfMonth());

        // 月のはじめの日(x月1日)の曜日を確認
        // カレンダーの週頭固定曜日でない場合は、その前の週の集頭固定曜日を探す
        LocalDate firstDayOfCalender = firstDayOfMonth;
        if (firstDayOfMonth.getDayOfWeek() != this.fixedDayOfWeek) {
            firstDayOfCalender = firstDayOfMonth.with(TemporalAdjusters.previous(this.fixedDayOfWeek));
        }

        return firstDayOfCalender;
    }

    // イベントの設定を呼び出し側に譲渡する
    public class UpdateEvent extends ComponentEvent<Calender> {
        public UpdateEvent(Calender source) {
            super(source, false);
        }
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
