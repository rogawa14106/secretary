package com.rogawa.secretary.views.calender;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class Calender extends VerticalLayout {
    private final ScheduleServiceImpl service;
    private final ScheduleRepository repo;

    // 定数
    private static final Integer WEEK_DAY_CNT = 7; // 一週間の日数
    private static final Integer MAX_DRAWING_DATES = 42; // カレンダーに描画する日付数の最大値

    //
    private LocalDate firstDayOfCalender; // カレンダーの最初に描画される日付
    private LocalDate[] drawingDates; // 描画する日付の配列
    private List<Schedule> drawingSchedules = new ArrayList<Schedule>(); // 描画するスケジュールのリスト
    private List<DateCard> dateCards = new ArrayList<DateCard>(); // カレンダーの日要素を示すオブジェクトのリスト
    private DayOfWeek fixedDayOfWeek; // 週頭固定曜日

    public Calender(ScheduleRepository repo, ScheduleServiceImpl service) {
        this.repo = repo;
        this.service = service;
    }

    public VerticalLayout createCalender(String targetYearMonth, DayOfWeek fixedDayOfWeek) {
        // 現在のカレンダーのデータを削除
        // clearCalender();

        // 描画する月のカレンダーの最初の日を確定
        this.firstDayOfCalender = searchFirstDayOfCalender(targetYearMonth, fixedDayOfWeek);

        // カレンダーに描画する範囲の日付を取得;
        this.drawingDates = getDrawingDates(targetYearMonth, fixedDayOfWeek);

        // 各日付のオブジェクトを作成して配列に入れる
        for (Integer i = 0; i < MAX_DRAWING_DATES; i++) {
            DateCard dateCard = new DateCard(drawingDates[i]);
            this.dateCards.add(dateCard);
        }

        // カレンダーに描画する範囲のスケジュールを取得;
        this.drawingSchedules = getDrawingSchedules();

        // 日付オブジェクトにスケジュールを入れる
        System.out.println("### drawing schedules ######################");
        System.out.println(this.drawingSchedules.size());
        for (Integer i = 0; i < this.drawingSchedules.size(); i++) {
            // ここから TODO
            // 日付の開始から終了までに合致する日付オブジェクトに予定を追加する
            // カレンダーの最初の日とスケジュールの開始日を比較して、入れるべき日付オブジェクトのインデックスを作成する
            System.out.println(this.drawingSchedules.get(i));
        }

        // カレンダーのレイアウトを作成
        VerticalLayout calenderLayout = new VerticalLayout();
        calenderLayout.setPadding(false);
        calenderLayout.setSpacing(false);
        calenderLayout.getStyle().set("width", "100%");
        calenderLayout.getStyle().set("height", "100%");

        // 各日付オブジェクトを配置する
        for (Integer i = 0; i < MAX_DRAWING_DATES / WEEK_DAY_CNT; i++) {
            // 週のレイアウトを作成
            HorizontalLayout weekLayout = new HorizontalLayout();
            weekLayout.setPadding(false);
            weekLayout.setSpacing(false);
            weekLayout.getStyle().set("width", "100%");
            weekLayout.getStyle().set("height", "calc(100% / " + MAX_DRAWING_DATES / WEEK_DAY_CNT + ")");
            for (Integer j = 0; j < WEEK_DAY_CNT; j++) {
                // 日付オブジェクトを週のレイアウトに7個ずつ配置
                DateCard dateCard = this.dateCards.get(i * WEEK_DAY_CNT + j);
                weekLayout.add(dateCard.createDateCard());
            }
            calenderLayout.add(weekLayout);
        }
        // スケジュールを入れる
        return calenderLayout;
    }

    private void clearCalender() {
        this.drawingDates = null;
        this.drawingSchedules = null;
    }

    private List<Schedule> getDrawingSchedules() {
        // カレンダーの開始日/終了日をLocalDateTimeに整形する
        LocalDateTime start_day = this.drawingDates[0].atStartOfDay();
        LocalDateTime end_day = this.drawingDates[MAX_DRAWING_DATES - 1].atStartOfDay();

        System.out.println("### getDrawindSchedules #################");
        System.out.println("start_day: " + start_day);
        System.out.println("end_day: " + end_day);
        // 開始日〜終了日までの間に入っている予定を取得
        List<Schedule> drawingSchedules = repo.findAllByDateRange(start_day, end_day);

        System.out.println("drawingSchedules size: " + drawingSchedules.size());
        return drawingSchedules;
    }

    // カレンダーに描画する対象になる日付を取得する
    private LocalDate[] getDrawingDates(String targetYearMonth, DayOfWeek fixedDayOfWeek) {
        // 描画する日付配列を初期化
        LocalDate[] drawingDates = new LocalDate[MAX_DRAWING_DATES];

        // カレンダーの最初の月~描画MAX日数分の日付配列を作成
        for (Integer i = 0; i < MAX_DRAWING_DATES; i++) {
            drawingDates[i] = firstDayOfCalender.plusDays(i);
        }

        return drawingDates;
    }

    // 月ごとのカレンダー上で最初の日になる日付を探す
    private LocalDate searchFirstDayOfCalender(String targetYearMonth, DayOfWeek fixedDayOfWeek) {
        LocalDate firstDayOfMonth = LocalDate.parse(targetYearMonth + "-01");
        LocalDate firstDayOfCalender = firstDayOfMonth;

        // 月のはじめの日(x月1日)の曜日を確認
        // カレンダーの週頭固定曜日でない場合は、その前の週の集頭固定曜日を探す
        if (firstDayOfMonth.getDayOfWeek() != fixedDayOfWeek) {
            firstDayOfCalender = firstDayOfMonth.minusDays(WEEK_DAY_CNT).with(fixedDayOfWeek);
        }

        return firstDayOfCalender;
    }

}
