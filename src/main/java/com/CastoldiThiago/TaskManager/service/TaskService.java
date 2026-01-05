package com.CastoldiThiago.TaskManager.service;

import com.CastoldiThiago.TaskManager.dto.CreateTaskDTO;
import com.CastoldiThiago.TaskManager.dto.TaskDTO;
import com.CastoldiThiago.TaskManager.exception.ResourceNotFoundException;
import com.CastoldiThiago.TaskManager.model.Task;
import com.CastoldiThiago.TaskManager.model.TaskList;
import com.CastoldiThiago.TaskManager.model.TaskStatus;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.repository.TaskListRepository;
import com.CastoldiThiago.TaskManager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;


    public List<TaskDTO> getTasksByStatus(User user, TaskStatus status) {
        return taskRepository.findAllByUserAndStatusOrderByDueDateAsc(user, status)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByStatusAndDateRange(User user, TaskStatus status, LocalDateTime from, LocalDateTime to) {
        return taskRepository.findAllByUserAndStatusAndDueDateBetweenOrderByDueDateAsc(user, status, from, to)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByDateRange(User user, LocalDateTime from, LocalDateTime to) {
        return taskRepository.findAllByUserAndDueDateBetweenOrderByDueDateAsc(user, from, to)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getAllTasksOrderedByDueDate(User user) {
        return taskRepository.findAllByUserOrderByDueDateAsc(user)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getAllTasksByUser(User user) {

        return taskRepository.findAllByUser(user)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getAllTasksByUserAndOrderedByCreatedAt(User user) {
        return taskRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByListId(Long taskListId) {
        TaskList taskList = taskListRepository.findById(taskListId)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de tareas no encontrada. Id: " + taskListId));

        return taskRepository.findAllByTaskList(taskList)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getMyDayTasks(User user) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Task> todayTasks =
                taskRepository.findAllByUserAndDueDateBetween(user, startOfDay, endOfDay);

        List<Task> movedTasks =
                taskRepository.findAllByUserAndMovedToMyDayIsTrue(user);

        Map<Long, Task> myDayTasks = new LinkedHashMap<>();
        List<Task> modified = new ArrayList<>();

        // Tareas con vencimiento hoy
        for (Task task : todayTasks) {
            myDayTasks.put(task.getId(), task);

            if (Boolean.FALSE.equals(task.getMovedToMyDay())) {
                task.setMovedToMyDay(true);
                task.setMovedDate(now);
                modified.add(task);
            }
        }

        // Tareas movidas manualmente
        for (Task task : movedTasks) {

            if (task.getMovedDate() != null && task.getMovedDate().isBefore(startOfDay)) {
                task.setMovedToMyDay(false);
                task.setMovedDate(null);
                modified.add(task);
                continue;
            }

            myDayTasks.putIfAbsent(task.getId(), task);
        }

        if (!modified.isEmpty()) {
            taskRepository.saveAll(modified);
        }

        return myDayTasks.values()
                .stream()
                .map(TaskDTO::new)
                .toList();
    }



    public TaskDTO moveToMyDay(User user, Long taskId) {
        LocalDateTime movedDate = LocalDateTime.now();
        Task taskToMove = taskRepository.findByUserAndId(user, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));
        taskToMove.setMovedToMyDay(true);
        taskToMove.setMovedDate(movedDate);
        return(new TaskDTO(taskRepository.save(taskToMove)));
    }

    public Optional<Task> getTaskById(User user, Long taskId) {

        return taskRepository.findByUserAndId(user, taskId);
    }

    public TaskDTO createTask(CreateTaskDTO taskDTO, User user) {
        TaskList taskList = null;
        if (taskDTO.getListId() != null) {
            taskList = taskListRepository.findById(taskDTO.getListId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lista no encontrada"));
        }

        // Asigna estado por defecto si no se especifica
        if(taskDTO.getStatus() == null){
            taskDTO.setStatus(TaskStatus.TODO);
        }

        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .status(taskDTO.getStatus())
                .movedToMyDay(taskDTO.getMovedToMyDay())
                .movedDate(taskDTO.getMovedDate())
                .user(user)
                .taskList(taskList)
                .build();

        return new TaskDTO(taskRepository.save(task));
    }

    public void deleteTask(Long id, User user) {
        taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada."));

        taskRepository.deleteById(id);
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, CreateTaskDTO request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Campos básicos
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());

        if (request.getMovedToMyDay() != null) {
            task.setMovedToMyDay(request.getMovedToMyDay());
            task.setMovedDate(request.getMovedToMyDay() ? request.getMovedDate() : null);
        }

        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        } else {
            task.setDueDate(null);
        }

        // Cambio de lista
        if (request.getListId() != null) {
            TaskList newList = taskListRepository.findById(request.getListId())
                    .orElseThrow(() -> new RuntimeException("Lista no encontrada"));

            task.setTaskList(newList);
        } else {
            task.setTaskList(null);
        }

        return new TaskDTO(taskRepository.save(task));
    }

    public TaskDTO changeStatus(Long taskId, TaskStatus taskStatus, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        task.setStatus(taskStatus);
        return new TaskDTO(taskRepository.save(task));
    }

}

