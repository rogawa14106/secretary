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
        // Date currentDateTime = new Date(System.currentTimeMillis());
        LocalDateTime currentDateTime = LocalDateTime.now();
        schedule.setUpdateTime(currentDateTime);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule updateSchedule(Long id, Schedule requestBody) {
        Schedule schedule = getSchedule(id);

        String new_title = requestBody.getTitle() == null ? schedule.getTitle() : requestBody.getTitle();
        schedule.setTitle(new_title);

        LocalDateTime new_datetime = requestBody.getDatetime() == null ? schedule.getDatetime()
                : requestBody.getDatetime();
        schedule.setDatetime(new_datetime);

        String new_owner = requestBody.getOwner() == null ? schedule.getOwner() : requestBody.getOwner();
        schedule.setOwner(new_owner);

        String new_description = requestBody.getDescription() == null ? schedule.getDescription()
                : requestBody.getDescription();
        schedule.setDescription(new_description);

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
