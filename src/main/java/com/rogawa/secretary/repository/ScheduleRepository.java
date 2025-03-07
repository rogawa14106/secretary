package com.rogawa.secretary.repository;

import com.rogawa.secretary.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

}
