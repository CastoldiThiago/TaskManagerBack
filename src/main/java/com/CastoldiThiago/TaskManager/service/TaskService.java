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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public List<TaskDTO> getTasksByListId(Long taskListId, User user) {
        TaskList taskList = taskListRepository.findById(taskListId)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de tareas no encontrada. Id: " + taskListId));
        if (!taskList.getOwner().equals(user)) {
            throw new AccessDeniedException("No tienes permiso sobre esta lista");
        }
        return taskRepository.findAllByTaskList(taskList)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getMyDayTasks(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 23:59:59.999999999
        List<Task> todayTasks = taskRepository.findAllByUserAndDueDateBetween(user, startOfDay, endOfDay);
        List<Task> myDayTasksMoved = taskRepository.findAllByUserAndMovedToMyDayIsTrue(user);
        List<Task> myDayTasksModified = new ArrayList<>();
        for (Task task : todayTasks) {
            if (task.getMovedToMyDay() == null || !task.getMovedToMyDay()) {
                task.setMovedToMyDay(true);
                task.setMovedDate(now);
                myDayTasksModified.add(task);
            }
        }
        List<Task> allMyDayTasks = new ArrayList<>(todayTasks);

        for (Task task : myDayTasksMoved) {
            if ( task.getMovedDate() != null && task.getMovedDate().isBefore(startOfDay)) {
                task.setMovedToMyDay(false);
                task.setMovedDate(null);
                myDayTasksModified.add(task);
                continue;
            }
            if (!allMyDayTasks.contains(task)) {
                allMyDayTasks.add(task);
            }
        }
        if (!myDayTasksModified.isEmpty()) {
            taskRepository.saveAll(myDayTasksModified);
        }
        return allMyDayTasks
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
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

        if (taskList != null) {
            taskList.getTasks().add(task);
        }
        return new TaskDTO(taskRepository.save(task));
    }

    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada."));
        if(!task.getUser().equals(user)){
            throw new AccessDeniedException("No puedes eliminar tareas de otros usuarios.");
        }
        taskRepository.deleteById(id);
    }

    public TaskDTO updateTask(Long taskId, CreateTaskDTO request, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (!task.getUser().equals(user)) {
            throw new AccessDeniedException("Unauthorized update attempt");
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getMovedToMyDay() != null) {
            task.setMovedToMyDay(request.getMovedToMyDay());
        }

        if (request.getMovedDate() != null) {
            task.setMovedDate(request.getMovedDate());
        }else if (task.getMovedDate() != null && !task.getMovedToMyDay()) {
            task.setMovedDate(null);
        }

        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }else if (task.getDueDate() != null) {
            task.setDueDate(null);
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (request.getListId() != null) {
            TaskList newList = taskListRepository.findById(request.getListId())
                    .orElseThrow(() -> new RuntimeException("Nueva lista no encontrada"));

            // Eliminar de la lista actual (si es necesario)
            if (task.getTaskList() != null) {
                task.getTaskList().getTasks().remove(task);
            }

            // Establecer nueva relación
            task.setTaskList(newList);
            newList.getTasks().add(task);
        } else if (task.getTaskList() != null) {
            task.setTaskList(null);
        }

        return new TaskDTO(taskRepository.save(task));
    }

    public TaskDTO changeStatus(Long taskId, TaskStatus taskStatus, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (!task.getUser().equals(user)) {
            throw new AccessDeniedException("Unauthorized update attempt");
        }
        task.setStatus(taskStatus);
        return new TaskDTO(taskRepository.save(task));
    }

}

