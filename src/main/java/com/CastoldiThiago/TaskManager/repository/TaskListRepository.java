package com.CastoldiThiago.TaskManager.repository;

import com.CastoldiThiago.TaskManager.model.TaskList;
import com.CastoldiThiago.TaskManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findAllByOwner(User owner);
    @Query("SELECT tl FROM TaskList tl LEFT JOIN FETCH tl.tasks WHERE tl.id = :id")
    Optional<TaskList> findByIdWithTasks(@Param("id") Long id);
}
