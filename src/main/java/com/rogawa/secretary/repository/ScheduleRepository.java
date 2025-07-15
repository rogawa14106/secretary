package com.rogawa.secretary.repository;

import com.rogawa.secretary.model.Schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 予定の日付が大きい順に取得する
    public List<Schedule> findAllByOrderByStartDatetime();

    // start_dayとend_dayの範囲にあるスケジュールを持ってくる。
    // WHERE: 予定が終了するのが検索開始日時より遅い && 予定を開始するのが検索終了日時より早い
    // ORDER: 開始日昇順
    @Query(value = "SELECT s FROM Schedule s WHERE s.endDatetime >= ?1 AND s.startDatetime <= ?2 ORDER BY s.startDatetime")
    public List<Schedule> findAllByDateRange(LocalDateTime start_day, LocalDateTime end_day);
}
