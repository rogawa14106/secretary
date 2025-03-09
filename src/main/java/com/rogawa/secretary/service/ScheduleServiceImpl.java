package com.rogawa.secretary.service;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
}
