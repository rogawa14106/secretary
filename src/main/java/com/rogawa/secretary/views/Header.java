package com.rogawa.secretary.views;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

@SpringComponent
@UIScope
public class Header extends HorizontalLayout {

    private H2 viewTitle;
    private final ScheduleForm scheduleForm;
    private final ScheduleRepository repo;
    private final ScheduleServiceImpl service;

    public Header(ScheduleRepository repo, ScheduleServiceImpl service) {
        this.repo = repo;
        this.service = service;
        this.scheduleForm = createScheduleForm();
    }

    public Component createHeader(String title) {
        // ヘッダの月表示部分
        viewTitle = new H2(title);
        viewTitle.addClickListener(e -> {
            System.out.println("### MonthSelector Clicked ###");
            fireEvent(new SelectMonthEvent(this));
        });
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

        // フィルタボタン
        Button filterButton = new Button(VaadinIcon.FILTER.create());
        Div navItemFilterButton = new Div(filterButton);
        navItemFilterButton.setClassName("navbar-item");

        // スケジュール作成ボタン
        Button plusButton = new Button(LumoIcon.PLUS.create(), e -> {
            scheduleForm.setSchedule(new Schedule());
            scheduleForm.open();
        });
        Div navItemPlusButton = new Div(plusButton);
        navItemPlusButton.setClassName("navbar-item");

        // 各要素をヘッダに配置
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setFlexGrow(1, navItemSpacer);
        layout.setPadding(true);
        layout.add(navItemSpacer);

        layout.add(navItemFilterButton);

        layout.add(navItemPlusButton);

        return layout;
    }

    private ScheduleForm createScheduleForm() {
        ScheduleForm scheduleForm = new ScheduleForm(this.service);
        scheduleForm.addChangeListener(c -> {
            fireEvent(new UpdateEvent(this));
            scheduleForm.close();
        });
        scheduleForm.addCancelListener(c -> {
            fireEvent(new UpdateEvent(this));
            scheduleForm.close();
        });
        scheduleForm.setSchedule(new Schedule());
        return scheduleForm;
    }

    // ヘッダのタイトルを変更する
    public void setViewTitle(String title) {
        viewTitle.setText(title);
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

    // 次の月ボタンを押した時のイベントを呼び出し側に譲渡する
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

    // 次の月ボタンを押した時のイベントを呼び出し側に譲渡する
    public class SelectMonthEvent extends ComponentEvent<Header> {
        public SelectMonthEvent(Header source) {
            super(source, false);
        }
    }

    public Registration addSelectMonthListener(ComponentEventListener<SelectMonthEvent> listener) {
        return addListener(SelectMonthEvent.class, listener);
    }
}
