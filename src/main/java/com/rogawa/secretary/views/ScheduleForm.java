package com.rogawa.secretary.views;

import java.time.Duration;
import java.util.Objects;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.service.ScheduleServiceImpl;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
// スケジュールを新規作成、編集、削除するためのフォーム用クラス
public class ScheduleForm extends Div {

    private final ScheduleServiceImpl service;

    // フォームのフィールド定義
    // 予定のタイトル
    TextField title = new TextField("タイトル");
    // 予定の日時
    DateTimePicker datetime = new DateTimePicker();
    // 予定の所有者
    TextField owner = new TextField("予定の所有者");
    // 予定の説明
    TextArea description = new TextArea();

    // ボタン
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
        binder.forField(datetime).bind(Schedule::getDatetime, Schedule::setDatetime);
        binder.forField(owner).bind(Schedule::getOwner, Schedule::setOwner);
        binder.forField(description).bind(Schedule::getDescription, Schedule::setDescription);

        // ボタンクリック時のイベントを設定
        cancelButton.addClickListener(e -> cancel());
        saveButton.addClickListener(e -> save());
        deleteButton.addClickListener(e -> delete());

        // 初期値として、deleteボタンが見えないように設定する
        // フォームを開いたときに値がセットされているときのみ、deleteボタンが見えるように設計する(編集時など)
        deleteButton.setVisible(false);
    }

    // 入力フォームのダイアログを作成する
    public Dialog createDialog() {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        // タイトル欄の設定は特になし

        // 日時入力欄の設定
        datetime.setStep(Duration.ofMinutes(30));
        datetime.setLabel("日時");

        // 説明入力欄の設定
        int CHAR_LIMIT = 140;
        description.setLabel("説明");
        description.setMaxLength(CHAR_LIMIT);
        description.setValueChangeMode(ValueChangeMode.EAGER);
        description.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + CHAR_LIMIT);
        });

        // 予定所有者欄の設定は特になし

        // フォームに入力欄を配置
        formLayout.add(title, datetime, description, owner);
        // フォームのレイアウトを設定
        formLayout.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2));
        // Stretch the title field over 2 columns
        // formLayout.setColspan(title, 2);

        // キャンセルボタン
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");

        // 削除ボタン
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        // 保存ボタン
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        // フォームをダイアログに追加
        dialog.add(formLayout);
        // ボタンをダイアログのフッターに配置
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(deleteButton);
        dialog.getFooter().add(saveButton);

        return dialog;
    }

    // キャンセル
    private void cancel() {
        setSchedule(null);
        fireEvent(new CancelEvent(this));
        System.out.println("#### Schedule creating was canceled");
    }

    // スケジュール新規作成、編集
    private void save() {
        if (binder.validate().isOk()) {
            service.createSchedule(binder.getBean());
            setSchedule(null);
            fireEvent(new ChangeEvent(this));
        }
        // TODO バリデーションエラーハンドル
        System.out.println("#### Schedule is created");
    }

    // スケジュール削除
    private void delete() {
        service.deleteSchedule(binder.getBean().getId());
        setSchedule(null);
        fireEvent(new ChangeEvent(this));
        System.out.println("#### Schedule is deleted");
    }

    // イベントの設定を呼び出し側に譲渡する
    public class ChangeEvent extends ComponentEvent<ScheduleForm> {
        public ChangeEvent(ScheduleForm source) {
            super(source, false);
        }
    }

    // イベントの設定を呼び出し側に譲渡する
    public class CancelEvent extends ComponentEvent<ScheduleForm> {
        public CancelEvent(ScheduleForm source) {
            super(source, false);
        }
    }

    // イベントの設定を呼び出し側に譲渡する
    public Registration addChangeListener(ComponentEventListener<ChangeEvent> listener) {
        return addListener(ChangeEvent.class, listener);
    }

    // イベントの設定を呼び出し側に譲渡する
    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    // 外部からモデルを設定可能にする。
    public void setSchedule(Schedule schedule) {
        binder.setBean(schedule);
        if (Objects.nonNull(schedule)) {
            title.focus();
            deleteButton.setVisible(schedule.getId() != null);
        }
    }
}
