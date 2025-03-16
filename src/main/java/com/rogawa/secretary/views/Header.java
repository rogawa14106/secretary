package com.rogawa.secretary.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

@SpringComponent
@UIScope
public class Header extends HorizontalLayout {

    private LocalDate calenderMonth;

    private H3 viewTitle;
    private final ListBox<LocalDate> monthSelector;
    private final Popover monthSelectorView; // monthSelectorを表示するコンポーネント
    private Boolean isEnableMonthSelectorEvent; // monthSelectorのチェンジイベントの有効無効を切り替える

    public Header() {
        viewTitle = new H3();
        this.monthSelectorView = new Popover();
        this.monthSelector = createMonthSelector();
    }

    // ヘッダのカレンダー月を変更する
    public void setCalenderMonth(LocalDate date) {
        this.calenderMonth = date;
        // ヘッダのタイトルを変更
        String headerTitle = DateTimeFormatter.ofPattern("yyyy年M月").format(this.calenderMonth);
        viewTitle.setText(headerTitle);
        // 月選択ボックスのアイテムを更新
        initMonthSelector();
    };

    public Component createHeader() {
        // ヘッダの月表示部分
        // 月表示を押すと月選択ボックスがポップオーバーするようにする
        monthSelectorView.setTarget(this.viewTitle);
        monthSelectorView.add(this.monthSelector);

        // 前の月へボタン
        Span privButton = new Span(VaadinIcon.CARET_LEFT.create());
        privButton.addClickListener(e -> {
            System.out.println("### privButton Clicked ###");
            fireEvent(new ClickPrivBtnEvent(this));
        });
        // 次の月へボタン
        Span nextButton = new Span(VaadinIcon.CARET_RIGHT.create());
        nextButton.addClickListener(e -> {
            System.out.println("### nextButton Clicked ###");
            fireEvent(new ClickNextBtnEvent(this));
        });

        HorizontalLayout navItemSpacer = new HorizontalLayout();
        navItemSpacer.setAlignItems(FlexComponent.Alignment.CENTER);
        navItemSpacer.setClassName("navbar-item");
        navItemSpacer.add(privButton, viewTitle, nextButton);
        navItemSpacer.add(monthSelectorView); // FIXME お試し

        // フィルタボタン
        Button filterButton = new Button(VaadinIcon.FILTER.create());
        Div navItemFilterButton = new Div(filterButton);
        navItemFilterButton.setClassName("navbar-item");

        // スケジュール作成ボタン
        // Button plusButton = new Button(LumoIcon.PLUS.create(), e -> {
        // scheduleForm.setSchedule(new Schedule());
        // scheduleForm.open();
        // });
        // Div navItemPlusButton = new Div(plusButton);
        // navItemPlusButton.setClassName("navbar-item");

        // 各要素をヘッダに配置
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setFlexGrow(1, navItemSpacer);
        layout.setPadding(false);
        layout.addClassNames(
                LumoUtility.Border.TOP,
                LumoUtility.Padding.SMALL);
        layout.add(navItemSpacer);

        // layout.add(navItemFilterButton); //TODO 予定をフィルタするボタン(優先度低)

        // layout.add(navItemPlusButton); // カレンダー側で作成できるので不要とする

        // TODO 今日へ戻るボタンを作りたい

        return layout;
    }

    // 月選択ボックスを作成する
    private ListBox<LocalDate> createMonthSelector() {
        ListBox<LocalDate> monthSelector = new ListBox<>();

        // Itemの表示フォーマットを定義する
        monthSelector.setItemLabelGenerator(date -> {
            String month = date.format(DateTimeFormatter.ofPattern("yyyy年 M月"));
            return month;
        });
        // 月を選択したときのイベントを定義する
        monthSelector.addValueChangeListener(e -> {
            // Warning
            // イベント無効フラグが立っていたら、上位のイベントを発火しない
            // これをしないとItem更新時に無限ループしてstack overflowが発生する
            // このフラグがあるので、monthselectorは別モジュールとして作ったほうがいいかも
            if (!isEnableMonthSelectorEvent) {
                return;
            }
            this.monthSelectorView.close(); // ポップオーバーを閉じる
            fireEvent(new SelectMonthEvent(this, e.getValue()));
        });
        return monthSelector;
    }

    // 月選択ボックスを初期化する
    private void initMonthSelector() {
        // 表示する月の設定
        Integer displayMonthRange = 24;
        Integer monthOffset = displayMonthRange / 2;
        LocalDate startMonth = this.calenderMonth.minusMonths(monthOffset);

        // 描画するアイテムの配列を作成
        LocalDate[] monthItems = new LocalDate[displayMonthRange];

        for (Integer i = 0; i < displayMonthRange; i++) {
            monthItems[i] = startMonth.plusMonths(i);
        }

        // ValueChangeイベント発火時に何もしないようにする
        this.isEnableMonthSelectorEvent = false;

        // 値をセットする
        this.monthSelector.setItems(monthItems);
        this.monthSelector.setValue(this.calenderMonth);

        // イベントを有効にする
        this.isEnableMonthSelectorEvent = true;
    }

    // カレンダー更新処理の設定を呼び出し側に譲渡する
    public class UpdateEvent extends ComponentEvent<Header> {
        public UpdateEvent(Header source) {
            super(source, false);
        }
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    // 前の月ボタンを押した時のイベントを呼び出し側に譲渡する
    public class ClickPrivBtnEvent extends ComponentEvent<Header> {
        public ClickPrivBtnEvent(Header source) {
            super(source, false);
        }
    }

    public Registration addClickPrivBtnListener(ComponentEventListener<ClickPrivBtnEvent> listener) {
        return addListener(ClickPrivBtnEvent.class, listener);
    }

    // 次の月ボタンを押した時のイベントを呼び出し側に譲渡する
    public class ClickNextBtnEvent extends ComponentEvent<Header> {
        public ClickNextBtnEvent(Header source) {
            super(source, false);
        }
    }

    public Registration addClickNextBtnListener(ComponentEventListener<ClickNextBtnEvent> listener) {
        return addListener(ClickNextBtnEvent.class, listener);
    }

    // 月選択した時のイベントを呼び出し側に譲渡する
    public class SelectMonthEvent extends ComponentEvent<Header> {
        private final LocalDate month;

        public SelectMonthEvent(Header source, @EventData("event.month") LocalDate month) {
            super(source, false);
            this.month = month;
        }

        // 選ばれた値を渡す
        public LocalDate getValue() {
            return this.month;
        }
    }

    public Registration addSelectMonthListener(ComponentEventListener<SelectMonthEvent> listener) {
        return addListener(SelectMonthEvent.class, listener);
    }
}
