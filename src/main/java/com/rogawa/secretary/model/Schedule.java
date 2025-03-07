package com.rogawa.secretary.model;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

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
//import lombok.Getter;

@Entity
@Data
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    // @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm") // 入力時フォーマット
    @JsonFormat(pattern = "yyyy/MM/dd-HH:mm") // 出力時
    // private Date datetime;
    private LocalDateTime datetime;

    @NotBlank
    private String owner;

    @NotNull
    private String description;

    @JsonFormat(pattern = "yyyy/MM/dd-HH:mm:ss") // 出力時
    // private Date updateTime;
    private LocalDateTime updateTime;

    public void logWrite() {
        System.out.println("id: " + id);
        System.out.println("title: " + title);
        System.out.println("datetime: " + datetime);
        System.out.println("owner: " + owner);
        System.out.println("description: " + description);
        System.out.println("updateTime: " + updateTime);
    }
}
