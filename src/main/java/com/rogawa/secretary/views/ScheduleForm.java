package com.rogawa.secretary.views;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;

@SpringComponent
@UIScope
// スケジュールを新規作成、編集、削除するためのフォーム用クラス
public class ScheduleForm extends Dialog {

    private final ScheduleServiceImpl service;

    // フォームのフィールド定義
    // 予定のタイトル
    TextField title = new TextField();
    // 終日予定かどうか
    Checkbox isAllDay = new Checkbox();
    // 予定の開始日時
    DateTimePicker datetime = new DateTimePicker();
    // 予定の終了日時
    DateTimePicker endDatetime = new DateTimePicker();
    // 予定の開始日
    DatePicker date = new DatePicker();
    // 予定の終了日
    DatePicker endDate = new DatePicker();
    // 予定の所有者
    TextField owner = new TextField();
    // 予定の説明
    TextArea description = new TextArea();

    // ボタン定義
    Button cancelButton = new Button("やめる");
    Button saveButton = new Button("保存");
    Button deleteButton = new Button("削除");

    // フォームの値をScheduleのパラメータにバインドするためのバインダー
    private final Binder<Schedule> binder = new Binder<>(Schedule.class);

    // フォームを初期化する
    public ScheduleForm(ScheduleServiceImpl service) {
        this.service = service;

        // Binderを利用し、このクラスのパラメータ(フォームに入力された値)とモデルScheduleのパラメータを紐付ける
        binder.forField(title).bind(Schedule::getTitle, Schedule::setTitle);
        binder.forField(isAllDay).bind(Schedule::getIsAllDay, Schedule::setIsAllDay);
        binder.forField(date).bind(
                (schedule) -> { // getter
                    if (schedule.getDatetime() != null) {
                        LocalDate localDate = schedule.getDatetime().toLocalDate();
                        return localDate;
                    } else {
                        return null;
                    }
                },
                (schedule, date) -> { // setter
                    if (date != null) {
                        LocalDateTime localDateTime = date.atStartOfDay();
                        schedule.setDatetime(localDateTime);
                    } else {
                        schedule.setDatetime(null);
                    }
                });
        binder.forField(endDate).bind(
                (schedule) -> { // getter
                    if (schedule.getEndDatetime() != null) {
                        LocalDate localDate = schedule.getEndDatetime().toLocalDate();
                        return localDate;
                    } else {
                        return null;
                    }
                },
                (schedule, endDate) -> { // setter
                    if (endDate != null) {
                        LocalDateTime localDateTime = endDate.atStartOfDay();
                        schedule.setEndDatetime(localDateTime);
                    } else {
                        schedule.setEndDatetime(null);
                    }
                });
        binder.forField(datetime).bind(Schedule::getDatetime, Schedule::setDatetime);
        binder.forField(endDatetime).bind(Schedule::getEndDatetime, Schedule::setEndDatetime);
        binder.forField(owner).bind(Schedule::getOwner, Schedule::setOwner);
        binder.forField(description).bind(Schedule::getDescription, Schedule::setDescription);

        // ボタンクリック時のイベントを設定
        cancelButton.addClickListener(e -> cancel());
        saveButton.addClickListener(e -> save());
        deleteButton.addClickListener(e -> delete());

        // 初期値として、deleteボタンが見えないように設定する
        // フォームを開いたときに値がセットされているときのみ、deleteボタンが見えるように設計する(編集時など)
        deleteButton.setVisible(false);

        // ダイアログ外のスペースをクリック時に閉じないようにする
        // warning フォームを閉じたときにキャンセルイベントを発火しないとスケジュールの入力が保持されてしまうため
        // this.setCloseOnOutsideClick(false);

        // UIを作成する
        createUI();
    }

    // 入力フォームのダイアログを作成する
    private void createUI() {
        FormLayout formLayout = new FormLayout();

        // タイトル欄の設定
        title.setLabel("タイトル*");

        // 終日予定チェックボックスの設定
        isAllDay.setLabel("終日");
        // 終日予定かどうかによって、日付入力か日時入力かが変わるようにする。
        isAllDay.addValueChangeListener(e -> {
            toggleDateTimeForm(e.getValue());
        });

        // 開始日入力欄の設定
        date.setLabel("開始日*");

        // 終了日入力欄の設定
        endDate.setLabel("終了日*");

        // 日時入力欄の設定
        datetime.setStep(Duration.ofMinutes(30));
        datetime.setLabel("開始日時*");

        // 日時入力欄の設定
        endDatetime.setStep(Duration.ofMinutes(30));
        endDatetime.setLabel("終了日時*");

        // 説明入力欄の設定
        int CHAR_LIMIT = 140;
        description.setLabel("説明");
        description.setMaxLength(CHAR_LIMIT);
        description.setValueChangeMode(ValueChangeMode.EAGER);
        description.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + CHAR_LIMIT);
        });

        // 予定所有者欄の設定
        owner.setLabel("予定の所有者*");

        // フォームに入力欄を配置
        formLayout.add(title, owner, isAllDay, datetime, date, endDatetime, endDate, description);

        // フォームのレイアウトを設定
        formLayout.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2));
        // Stretch the title field over 2 columns
        // formLayout.setColspan(title, 2);
        formLayout.setColspan(isAllDay, 2);
        formLayout.setColspan(description, 2);

        // キャンセルボタン
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");

        // 削除ボタン
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        // 保存ボタン
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        // フォームをダイアログに追加
        this.add(formLayout);
        // ボタンをダイアログのフッターに配置
        this.getFooter().add(cancelButton);
        this.getFooter().add(deleteButton);
        this.getFooter().add(saveButton);
    }

    // 終日予定かどうかによってフォームを変えるメソッド
    private void toggleDateTimeForm(Boolean isAllDay) {
        // 終日予定の時に表示する
        date.setVisible(isAllDay); // 開始日入力欄
        endDate.setVisible(isAllDay); // 終了日入力欄

        // 終日予定ではない時に表示する
        datetime.setVisible(!isAllDay); // 開始日時入力欄
        endDatetime.setVisible(!isAllDay); // 終了日時入力欄
    }

    // キャンセル時の動作
    private void cancel() {
        System.out.println("#### Attempt to cancel the schedule creation");
        setSchedule(null);
        fireEvent(new CancelEvent(this));
        System.out.println("#### Schedule creation was canceled");
    }

    // スケジュール新規作成、編集
    private void save() {
        System.out.println("#### Attempt to save schedule");
        binder.getBean().logWrite();
        try {
            if (binder.validate().isOk()) { // この分岐、いらなそう FIXME
                service.createSchedule(binder.getBean());
                setSchedule(null);
                fireEvent(new ChangeEvent(this));
            }
            System.out.println("#### Schedule saved");
            Notification notification = Notification.show("予定を保存したよ", 2000, Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification notification = Notification.show("「*」がついている項目は必須だよ", 2000, Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

            System.err.println("#### Failed to save schedule");
            System.err.println(e);
        }
    }

    // スケジュール削除
    private void delete() {
        System.out.println("#### Attempt to delete schedule");
        service.deleteSchedule(binder.getBean().getId());
        setSchedule(null);
        fireEvent(new ChangeEvent(this));
        System.out.println("#### Schedule deleted");
    }

    // 外部からモデルを設定可能にする。
    // フォームを開きたいときはnullではなく必ずScheduleインスタンスを渡す
    public void setSchedule(Schedule schedule) {
        // 値をバインド
        binder.setBean(schedule);

        // 日付/日時入力の表示を決定
        toggleDateTimeForm(isAllDay.getValue());

        if (Objects.nonNull(schedule)) {
            title.focus();

            // IDが入っていなかったら、新規作成用のフォームにする
            // 削除ボタンの表示設定
            this.deleteButton.setVisible(schedule.getId() != null);
        }
    }

    // スケジュールデータ更新時のイベントの設定を呼び出し側に譲渡する
    public class ChangeEvent extends ComponentEvent<ScheduleForm> {
        public ChangeEvent(ScheduleForm source) {
            super(source, false);
        }
    }

    public Registration addChangeListener(ComponentEventListener<ChangeEvent> listener) {
        return addListener(ChangeEvent.class, listener);
    }

    // キャンセルイベントの設定を呼び出し側に譲渡する
    public class CancelEvent extends ComponentEvent<ScheduleForm> {
        public CancelEvent(ScheduleForm source) {
            super(source, false);
        }
    }

    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

}
