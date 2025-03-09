package com.rogawa.secretary.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
//import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Data
@Table(name = "schedules")
public class Schedule {
    // プライマリーキー
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 予定タイトル
    @NotBlank
    private String title;

    // 終日予定フラグ
    @NotNull
    private Boolean isAllDay;

    // 予定の開始日時
    @JsonFormat(pattern = "yyyy/MM/dd-HH:mm") // 入出力フォーマット
    private LocalDateTime datetime;

    // 予定の終了日時
    @JsonFormat(pattern = "yyyy/MM/dd-HH:mm") // 入出力フォーマット
    private LocalDateTime endDatetime;

    // 予定の所有者
    @NotBlank
    private String owner;

    // 予定の説明
    @NotNull
    private String description;

    // 予定の更新日
    @JsonFormat(pattern = "yyyy/MM/dd-HH:mm:ss") // 入出力フォーマット
    private LocalDateTime updateTime;

    // デバッグ用
    public void logWrite() {
        System.out.println("# Schedule properties");
        System.out.println("    id: " + id);
        System.out.println("    title: " + title);
        System.out.println("    isAllDay: " + isAllDay);
        System.out.println("    datetime: " + datetime);
        System.out.println("    endDatetime: " + endDatetime);
        System.out.println("    owner: " + owner);
        System.out.println("    description: " + description);
        System.out.println("    updateTime: " + updateTime);
    }
}
