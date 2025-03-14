package com.rogawa.secretary.views;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.rogawa.secretary.repository.ScheduleRepository;
import com.rogawa.secretary.views.calender.Calender;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Securetary")
public class MainView extends AppLayout {

    // private final ScheduleRepository repo;
    private final Header header;
    private final Calender calender;

    private LocalDate calenderMonth;
    private static final DayOfWeek FIXED_DAY_OF_WEEK = DayOfWeek.SUNDAY; // カレンダーの週頭にする曜日
    // private static final DayOfWeek FIXED_DAY_OF_WEEK = DayOfWeek.MONDAY;

    public MainView(/* ScheduleRepository repo, */ Header header, Calender calender) {

        // this.repo = repo;
        this.header = header;
        this.calender = calender;
        this.calenderMonth = LocalDate.now();

        // テーマをダークに設定
        getElement().getThemeList().set("dark", true);

        // ヘッダーを配置
        addToNavbar(true, this.header.createHeader());
        // ヘッダのタイトルにテキストをセット
        setHeaderDate();

        // カレンダーを配置
        setContent(this.calender);
        this.calender.initCalender(this.calenderMonth, FIXED_DAY_OF_WEEK);

        // データ更新用ハンドラを各コンポーネントに追加
        // ヘッダの新規作成ボタンから予定を作成した時
        this.header.addUpdateListener(c -> {// 作成したカレンダーのIDをredrawContentに渡したい
            initCalender();
        });
        // ヘッダの前の月へボタンを押した時
        this.header.addClickPrivBtnListener(c -> {
            // カレンダー月を減らす
            changeViewMonth(this.calenderMonth.plusMonths(-1));
        });
        // ヘッダの次の月へボタンを押した時
        this.header.addClickNextBtnListener(c -> {
            // カレンダー月を増やす
            changeViewMonth(this.calenderMonth.plusMonths(1));
        });
        // ヘッダで月選択をした時
        this.header.addSelectMonthListener(c -> {
            changeViewMonth(c.getValue());
        });

        // カレンダで予定情報を更新した時
        this.calender.addUpdateListener(c -> {// 作成したカレンダーのIDをredrawContentに渡したい
            initCalender();
        });
    }

    // このアプリケーション全体で描画する月を変更する
    private void changeViewMonth(LocalDate newMonth) {
        this.calenderMonth = newMonth;
        // ヘッダの月表示を更新
        setHeaderDate();
        // カレンダーを更新
        initCalender();
    }

    // ヘッダの月を変更する
    private void setHeaderDate() {
        // String headerTitle =
        // DateTimeFormatter.ofPattern("yyyy年M月").format(this.calenderMonth);
        // this.header.setViewTitle(headerTitle);
        this.header.setCalenderMonth(this.calenderMonth);
    }

    // カレンダーを初期化する
    private void initCalender() {
        this.calender.initCalender(this.calenderMonth, FIXED_DAY_OF_WEEK);
    }
}
