package com.rogawa.secretary.views;

import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
//import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.shared.Registration;

@SpringComponent
@UIScope
public class ListCalender extends VerticalLayout {

    private ScheduleForm scheduleForm;
    private ScheduleServiceImpl service;
    private final ScheduleRepository repo;

    // 描画される予定の一覧(日付降順ソート)
    private List<Schedule> schedules;// 不要かも

    // カレンダー描画用のプロパティ
    private Integer minYear = 1999; // 描画した予定の中で最小の年
    private Integer minMonth = 1; // 描画中の年の中で最小の月
    private Integer minDay = 1; // 描画中の月の中で最小の日

    // カレンダーの描画用インスタンス
    // TODO 年月のヘッダを固定して描画できたら嬉しい(普通のヘッダみたいに) position: fixedでどうにかしたい
    private ComponentRenderer<Component, Schedule> scheduleCardRenderer = new ComponentRenderer<>(schedule -> {
        VerticalLayout cardLayout = new VerticalLayout();
        cardLayout.setSpacing(false);
        cardLayout.setPadding(false);

        // 予定の日付を取得
        LocalDateTime datetime = schedule.getDatetime();

        // 取得した年の年ヘッダーがまだ描画されていなかったら描画する
        String year = zeroSuppress(datetime.format(DateTimeFormatter.ofPattern("yyyy")));
        if (Integer.parseInt(year) > minYear) {
            // 描画
            cardLayout.add(yearHeader(year));
            // 描画用のパラメータを更新
            this.minYear = Integer.parseInt(year);
            this.minMonth = 1;
            this.minDay = 1;
        }
        // 取得した年/月の月ヘッダーがまだ描画されていなかったら描画する
        String month = zeroSuppress(datetime.format(DateTimeFormatter.ofPattern("MM")));
        if (Integer.parseInt(month) > minMonth) {
            // 描画
            cardLayout.add(monthHeader(month));
            // 描画用のパラメータを更新
            this.minMonth = Integer.parseInt(month);
            this.minDay = 1;
        }
        // 取得した年/月/日の日ヘッダーがまだ描画されていなかったら描画する
        String day = zeroSuppress(datetime.format(DateTimeFormatter.ofPattern("dd")));
        if (Integer.parseInt(day) > minDay) {
            // 描画する場合は、曜日を足して描画
            String dayOfWeek = datetime.format(DateTimeFormatter.ofPattern("(E)", Locale.JAPANESE));
            cardLayout.add(dayHeader(day + dayOfWeek));
            // 描画用のパラメータを更新
            this.minDay = Integer.parseInt(day);
        }

        // 予定を描画
        cardLayout.add(createScheduleCard(schedule));

        return cardLayout;
    });

    // コンストラクタ
    public ListCalender(ScheduleRepository repo, ScheduleServiceImpl service) {
        this.repo = repo;
        this.service = service;
    }

    // バーチャルリストを返す
    public VirtualList<Schedule> createVirtualList() {
        VirtualList<Schedule> list = new VirtualList<>();

        // カレンダー描画用のプロパティを初期化
        this.minYear = 1;
        this.minMonth = 1;
        this.minDay = 1;

        // 最新の予定情報を取得
        this.schedules = repo.findAllByOrderByDatetime();

        // バーチャルリストの設定
        list.setItems(schedules);
        list.setRenderer(scheduleCardRenderer);

        // 最小の高さを100%に設定する。これをしないと画面下にスペースができる
        // list.setMinHeight("100%");
        list.setHeight("100%");

        // #TODO# 今日の日付以上の予定までスクロール
        // list.scrollToIndex(1);
        return list;
    }

    private Integer searchTodayIndex() {
        Integer index = 0;
        return index;
    }

    // カレンダーの情報を更新する
    // public void updateCalender() {
    // this.schedules = repo.findAllByOrderByDatetime();
    // };

    // 年ヘッダーコンポーネント
    private Component yearHeader(String year) {
        Component layout = cardHeader(year + "年", LumoUtility.Background.CONTRAST_50, LumoUtility.FontSize.XLARGE);
        return layout;
    }

    // 月ヘッダーコンポーネント
    private Component monthHeader(String month) {
        return cardHeader(month + "月", LumoUtility.Background.CONTRAST_30, LumoUtility.FontSize.LARGE);
    }

    // 日ヘッダーコンポーネント
    private Component dayHeader(String day) {
        Component layout = cardHeader(day, LumoUtility.Background.CONTRAST_5, LumoUtility.FontSize.MEDIUM);
        // 曜日ごとに背景色を設定
        if (day.contains("土")) {
            layout.getStyle().set("background-color", "#DDE1FF");
        } else if (day.contains("日")) {
            layout.getStyle().set("background-color", "#FFDDE1");
        }
        return layout;
    }

    // ヘッダーの共通コンポーネント
    private Component cardHeader(String text, String bgClassName, String fontSizeClasName) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames(LumoUtility.BoxShadow.XSMALL);
        layout.addClassNames(bgClassName, fontSizeClasName);
        layout.setWidthFull();
        layout.setSpacing(false);

        // スペーサ
        Div spacer = new Div();
        spacer.getStyle().setMinWidth("0.5rem");
        // テキスト表示
        Div headerTxt = new Div(text);
        headerTxt.getStyle().set("font-weight", "600");
        headerTxt.setWidthFull();

        layout.add(spacer);
        layout.add(headerTxt);
        return layout;
    }

    // 予定コンポーネント
    private Component createScheduleCard(Schedule schedule) {
        // 表示する値を取得
        // String.valueOf(schedule.getId());
        Boolean isAllDay = schedule.getIsAllDay();
        String datetimeTxt;
        if (isAllDay) {
            datetimeTxt = "終日";
        } else {
            datetimeTxt = schedule.getDatetime().format(DateTimeFormatter.ofPattern("HH:mm")) + "~";
        }
        String titleTxt = schedule.getTitle();
        String ownerTxt = schedule.getOwner();

        // 値を表示するコンポーネント作成
        // 日付
        // タイトル
        // 所有者
        Component ownerBadge = chip(ownerTxt);
        ownerBadge.getStyle().set("background-color", convOwner2ColorCode(ownerTxt));

        // 左側のコンポーネントを配置
        HorizontalLayout leftContent = new HorizontalLayout();
        leftContent.add(
                new Div(datetimeTxt),
                new Div(titleTxt));

        // 右側のコンポーネントを配置
        HorizontalLayout rightContent = new HorizontalLayout();
        rightContent.add(
                ownerBadge);
        rightContent.getStyle().set("margin-left", "auto");

        // 予定コンポーネント全体を配置
        HorizontalLayout layout = new HorizontalLayout();
        // layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.getStyle().set("padding", "1rem");
        // layout.addClassNames(LumoUtility.BoxShadow.XSMALL);
        // layout.getStyle().set("overflow", "hidden");
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        layout.add(leftContent, rightContent);

        // クリック時の動作定義
        layout.addClickListener(e -> {
            openScheduleDialog(schedule);
        });

        return layout;
    }

    // 予定の所有者名をカラーコードに変換する
    private String convOwner2ColorCode(String ownerTxt) {
        // 適当な初期値
        String colorCode = "#524050";
        // SHA-1
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] sha1Byte = sha1.digest(ownerTxt.getBytes());

            // Java17以降からしか使用出来ない
            HexFormat hex = HexFormat.of().withLowerCase();
            String hexString = hex.formatHex(sha1Byte);
            colorCode = "#" + hexString.substring(0, 6);
            // System.out.println("### " + ownerTxt + "BadgeColor: " + colorCode);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("### NoSuchAlgorithmException");
        }

        return colorCode;
    }

    // テキストチップ #TODO# UIコンポーネントとして切り出し
    private Component chip(String text) {
        Span chip = new Span(text);
        chip.getElement().getThemeList().add("badge primary");
        return chip;
    }

    // スケジュール編集/削除用のダイアログを開く
    private void openScheduleDialog(Schedule schedule) {
        // フォームインスタンス作成
        ScheduleForm scheduleForm = new ScheduleForm(service);
        // ダイアログ作成
        Dialog dialog = scheduleForm.createDialog();
        // イベント設定
        scheduleForm.addChangeListener(c -> {
            fireEvent(new UpdateEvent(this));
            dialog.close();
        });

        scheduleForm.addCancelListener(c -> {
            fireEvent(new UpdateEvent(this));
            dialog.close();
        });

        // フォームの値を設定
        scheduleForm.setSchedule(schedule);
        // ダイアログを開く
        dialog.open();
    }

    private String zeroSuppress(String numStr) {
        Pattern p = Pattern.compile("^0*(.+)");
        Matcher m = p.matcher(numStr);
        if (m.matches()) {
            return m.group(1);
        } else {
            return "";
        }
    }

    // イベントの設定を呼び出し側に譲渡する
    public class UpdateEvent extends ComponentEvent<ListCalender> {
        public UpdateEvent(ListCalender source) {
            super(source, false);
        }
    }

    // イベントの設定を呼び出し側に譲渡する
    public Registration addCancelListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }
}
