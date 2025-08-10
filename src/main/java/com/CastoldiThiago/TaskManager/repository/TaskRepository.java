package com.CastoldiThiago.TaskManager.repository;

import com.CastoldiThiago.TaskManager.model.Task;
import com.CastoldiThiago.TaskManager.model.TaskList;
import com.CastoldiThiago.TaskManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.CastoldiThiago.TaskManager.model.TaskStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser(User user);

    Optional<Task> findByUserAndId(User user, Long taskId);

    List<Task> findAllByUserAndMovedToMyDayIsTrue(User user);

    // Filtra por usuario y ordena por fecha de creación descendente
    List<Task> findAllByUserOrderByCreatedAtDesc(User user);

    // Filtra por usuario y estado ordenando por fecha límite ascendente
    List<Task> findAllByUserAndStatusOrderByDueDateAsc(User user, TaskStatus status);

    // Filtra por usuario, estado y rango de fecha límite
    List<Task> findAllByUserAndStatusAndDueDateBetweenOrderByDueDateAsc(User user, TaskStatus status, LocalDateTime from, LocalDateTime to);

    // Filtra por usuario y rango de fechas límite
    List<Task> findAllByUserAndDueDateBetweenOrderByDueDateAsc(User user, LocalDateTime from, LocalDateTime to);

    // Filtra solo por usuario y ordena por fecha límite (por ejemplo para listar todas las tareas)
    List<Task> findAllByUserOrderByDueDateAsc(User user);

    // Filtra por taskList
    List<Task> findAllByTaskList(TaskList taskList);

    List<Task> findAllByUserAndDueDateBetween(User user, LocalDateTime startOfToday, LocalDateTime endOfToday);

    @Query("SELECT t FROM Task t JOIN FETCH t.user WHERE t.id = :id")
    Optional<Task> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.taskList WHERE t.id = :id")
    Optional<Task> findByIdWithList(@Param("id") Long id);
}
