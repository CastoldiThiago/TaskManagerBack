package com.CastoldiThiago.TaskManager.controller;
import com.CastoldiThiago.TaskManager.dto.CreateTaskDTO;
import com.CastoldiThiago.TaskManager.model.Task;
import com.CastoldiThiago.TaskManager.model.TaskList;
import com.CastoldiThiago.TaskManager.model.TaskStatus;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.service.TaskListService;
import com.CastoldiThiago.TaskManager.service.TaskService;
import com.CastoldiThiago.TaskManager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskListService taskListService;

    // Obtiene todas las tareas del usuario ordenadas por fecha límite ascendente
    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasksOrderedByDueDate(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Task> tasks = taskService.getAllTasksOrderedByDueDate(user);
        return ResponseEntity.ok(tasks);
    }


    // Obtiene tareas por estado específico ordenadas por fecha límite ascendente
    @GetMapping("/filter/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Task> tasks = taskService.getTasksByStatus(user, status);
        return ResponseEntity.ok(tasks);
    }

    // Filtra tareas por estado y un rango específico de fechas límite
    @GetMapping("/filter/status")
    public ResponseEntity<List<Task>> getTasksByStatusAndDueDateRange(
            @RequestParam TaskStatus status,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            Principal principal) {

        User user = userService.findByUsername(principal.getName());
        List<Task> tasks = taskService.getTasksByStatusAndDateRange(user, status, from, to);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/filter/dateRange")
    public ResponseEntity<List<Task>> getTasksByDateRange(Principal principal,  @RequestParam LocalDateTime from, @RequestParam LocalDateTime to) {
        User user = userService.findByUsername(principal.getName());
        List<Task> tasks = taskService.getTasksByDateRange(user, from, to);
        return ResponseEntity.ok(tasks);
    }

    // Obtener las tareas del usuario ordenadas por la fecha de creación descendente
    @GetMapping("/recent")
    public ResponseEntity<List<Task>> getRecentTasks(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Task> tasks = taskService.getAllTasksByUserAndOrderedByCreatedAt(user);
        return ResponseEntity.ok(tasks);
    }

    // Obtener todas las tareas del usuario sin un orden específico
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasksByUser(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Task> tasks = taskService.getAllTasksByUser(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-day")
    public ResponseEntity<List<Task>> getMyDayTasks(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Task> myDayTasks = taskService.getMyDayTasks(user);
        return ResponseEntity.ok(myDayTasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Task task = taskService.getTaskById(user, id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskDTO taskDTO,
                           Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        Task createdTask = taskService.createTask(taskDTO, currentUser);
        return ResponseEntity.ok(createdTask);
    }

    // Obtener todas las tareas de una lista específica
    @GetMapping("list/{taskListId}")
    public List<Task> getTasksByList(@PathVariable Long taskListId, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        return taskService.getTasksByListId(taskListId, currentUser);
    }

    // Actualizar tarea y opcionalmente cambiar su lista
    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                           @RequestBody CreateTaskDTO request,
                           @RequestParam(required = false) Long taskListId,
                           Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        Task updatedTask = taskService.updateTask(id, request, currentUser);
        return  ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }




}

