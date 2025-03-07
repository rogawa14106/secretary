
package com.rogawa.secretary.service;

import com.rogawa.secretary.model.Schedule;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ScheduleService {

    List<Schedule> getSchedules();

    Schedule getSchedule(Long id);

    Schedule createSchedule(Schedule schedule);

    Schedule updateSchedule(Long id, Schedule schedule);

    void deleteSchedule(Long id);
}
