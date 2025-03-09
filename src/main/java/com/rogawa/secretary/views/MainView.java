package com.rogawa.secretary.views;

import com.rogawa.secretary.repository.ScheduleRepository;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Securetary")
public class MainView extends AppLayout {

    private final ScheduleRepository repo;
    private final Header header;
    private final ListCalender listCalender;

    public MainView(ScheduleRepository repo, ScheduleForm scheduleForm, Header header, ListCalender listCalender) {
        this.repo = repo;
        this.header = header;
        this.listCalender = listCalender;

        // ヘッダーを配置
        addToNavbar(false, header.createHeader());

        // コンテンツを配置
        initContent();
        // getElement().getThemeList().set("dark", true);

        // コンテンツのスクロールをしないようにする(できない#TODO#)
        // getElement().getStyle().set("height", "100%");
        // getContent().getStyle().set("height", "100%");

        // データ更新用ハンドラを各コンポーネントに追加
        header.addUpdateListener(c -> initContent());
        listCalender.addCancelListener(c -> initContent());
    }

    // コンテンツを作成する
    private void initContent() {
        // カレンダーを描画する
        setContent(listCalender.createVirtualList());
    }

    // private void listSchedule() {
    // grid.setItems(repo.findAll());
    // }

}

// Have the drawer toggle button on the left
// layout.add(new DrawerToggle());

// A user icon
// layout.add(new Image("images/user.svg", "Avatar"));
// HorizontalLayout layout = new HorizontalLayout();
//
// // Configure styling for the header
// layout.setId("header");
// layout.getThemeList().set("dark", true);
// layout.setWidthFull();
// layout.setSpacing(false);
// layout.setAlignItems(FlexComponent.Alignment.CENTER);
//
// // Placeholder for the title of the current view.
// // The title will be set after navigation.
// viewTitle = new H1("Home");
// layout.add(viewTitle);
//
