package com.rogawa.secretary.service;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;

import java.awt.Color;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule getSchedule(Long id) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);

        if (optionalSchedule.isEmpty()) {
            throw new Error("予定が存在しません。");
        }

        return scheduleRepository.findById(id).get();
    }

    @Override
    public Schedule createSchedule(Schedule schedule) {
        // 更新日を今の日時に設定する
        LocalDateTime currentDateTime = LocalDateTime.now();
        schedule.setUpdateTime(currentDateTime);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule updateSchedule(Long id, Schedule requestBody) {
        Schedule schedule = getSchedule(id);

        // リクエストの各値がnullでない場合は値を更新する
        // nullの場合は現在の値を使う
        String newTitle = requestBody.getTitle() == null ? schedule.getTitle() : requestBody.getTitle();
        schedule.setTitle(newTitle);

        Boolean newIsAllDay = requestBody.getIsAllDay() == null ? schedule.getIsAllDay() : requestBody.getIsAllDay();
        schedule.setIsAllDay(newIsAllDay);

        LocalDateTime newDatetime = requestBody.getDatetime() == null ? schedule.getDatetime()
                : requestBody.getDatetime();
        schedule.setDatetime(newDatetime);

        LocalDateTime newEndDateTime = requestBody.getEndDatetime() == null ? schedule.getEndDatetime()
                : requestBody.getEndDatetime();
        schedule.setEndDatetime(newEndDateTime);

        String newOwner = requestBody.getOwner() == null ? schedule.getOwner() : requestBody.getOwner();
        schedule.setOwner(newOwner);

        String newDescription = requestBody.getDescription() == null ? schedule.getDescription()
                : requestBody.getDescription();
        schedule.setDescription(newDescription);

        LocalDateTime currentDateTime = LocalDateTime.now();
        schedule.setUpdateTime(currentDateTime);

        return scheduleRepository.save(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        getSchedule(id);
        scheduleRepository.deleteById(id);
    }

    // scheduleの所有者文字列からカラーコードを作成する
    public String _generateOwnerColorCode(String ownerTxt) {
        // カラーコードを取得できなかった時用に適当な初期値を設定しておく
        String ownerColorCode = "#524050";

        try {
            // 予定の所有者文字列からSHA-1でハッシュ値を生成
            // Warning: 環境によってSHA-1が使えないときもある
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] sha1Byte = sha1.digest(ownerTxt.getBytes());

            // 生成したハッシュ値の末尾6文字を取得することでカラーコードを定義する
            // Warning: Java17以降からしか使用出来ない
            HexFormat hex = HexFormat.of().withLowerCase();
            String hexString = hex.formatHex(sha1Byte);
            ownerColorCode = "#" + hexString.substring(0, 6);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("### NoSuchAlgorithmException");
        }

        return ownerColorCode;
    }

    public String generateOwnerColorCode(String ownerTxt) {
        // 所有者名からハッシュ値を取得
        int hash = ownerTxt.hashCode();

        // ハッシュ値から色相を決定
        float hue = (hash & 0xFFFFFF) % 360;

        // 彩度と明度を制限して、背景色が暗めになるように調整
        float saturation = 0.6f + (Math.abs(hash) % 30) / 100f; // 0.6~0.9
        float brightness = 0.2f + (Math.abs(hash / 2) % 30) / 100f; // 0.2~0.5

        // HSBからColorへ変換
        Color color = Color.getHSBColor(hue / 360f, saturation, brightness);

        // カラーコードへ変換
        String colorCode = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());

        System.out.println(ownerTxt + colorCode);

        // 特別に、特定の人だけ静的に変える(クソ実装)
        if (colorCode == "#7B0E7D") {
            colorCode = "#D2691E";
        } else if (colorCode == "#381436") { // 祝日
            colorCode = "#A52A2A";
        }

        return colorCode;
    }

    // デフォルト値を入れたスケジュールを作成する
    public Schedule createDefaultSchedule(LocalDateTime baseDateTime) {
        // デフォルト値
        // datetime: ベースになる日付
        // endDatetime: ベースになる日付+1h
        Schedule defaultSchedule = new Schedule();
        defaultSchedule.setDatetime(baseDateTime);
        defaultSchedule.setEndDatetime(baseDateTime.plusHours(1));
        return defaultSchedule;
    }

    public Schedule cloneSchedule(Schedule schedule) {
        Schedule scheduleClone = new Schedule();
        scheduleClone.setId(schedule.getId());
        scheduleClone.setTitle(schedule.getTitle());
        scheduleClone.setOwner(schedule.getOwner());
        scheduleClone.setIsAllDay(schedule.getIsAllDay());
        scheduleClone.setDatetime(schedule.getDatetime());
        scheduleClone.setEndDatetime(schedule.getEndDatetime());
        scheduleClone.setDescription(schedule.getDescription());
        scheduleClone.setUpdateTime(schedule.getUpdateTime());
        return scheduleClone;
    }
}
