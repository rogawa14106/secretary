package com.rogawa.secretary.controller;

import com.rogawa.secretary.model.Schedule;
import com.rogawa.secretary.service.ScheduleService;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/api/v1/schedules")
    public ResponseEntity<List<Schedule>> getSchedules() {
        return new ResponseEntity(scheduleService.getSchedules(), HttpStatus.OK);
    }

    @GetMapping("/api/v1/schedules/{id}")
    public ResponseEntity<Schedule> getSchedule(@PathVariable Long id) {
        return new ResponseEntity(scheduleService.getSchedule(id), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/api/v1/schedules")
    public ResponseEntity<Schedule> createSchedule(@Validated @RequestBody Schedule schedule) {
        return new ResponseEntity(scheduleService.createSchedule(schedule), HttpStatus.CREATED);
    }

    @Transactional
    @PatchMapping("/api/v1/schedules/{id}")
    public ResponseEntity<Schedule> updateSchedule(@RequestBody Schedule schedule, @PathVariable Long id) {
        return new ResponseEntity(scheduleService.updateSchedule(id, schedule), HttpStatus.CREATED);
    }

    @Transactional
    @DeleteMapping("/api/v1/schedules/{id}")
    public ResponseEntity<HttpStatus> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
