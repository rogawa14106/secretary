package com.rogawa.secretary.repository;

import com.rogawa.secretary.model.Schedule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 予定の日付が大きい順に取得するメソッドを追加
    public List<Schedule> findAllByOrderByDatetime();
}
