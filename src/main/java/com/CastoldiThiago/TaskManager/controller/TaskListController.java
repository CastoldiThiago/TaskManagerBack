package com.CastoldiThiago.TaskManager.controller;

import com.CastoldiThiago.TaskManager.dto.UpdateListRequest;
import com.CastoldiThiago.TaskManager.model.Task;
import com.CastoldiThiago.TaskManager.model.TaskList;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.service.TaskListService;
import com.CastoldiThiago.TaskManager.service.TaskService;
import com.CastoldiThiago.TaskManager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task-lists")
public class TaskListController {

    private final TaskListService taskListService;
    private final TaskService taskService;
    private final UserService userService;

    @PostMapping
    public TaskList createList(@RequestBody TaskList taskList, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        return taskListService.createList(taskList, currentUser);
    }

    @GetMapping
    public List<TaskList> getLists(Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        return taskListService.getAllListsByUser(currentUser);
    }

    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
                taskListService.deleteList(id, user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskList> updateList(@PathVariable Long id, @RequestBody UpdateListRequest request, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        TaskList updatedList = taskListService.updateList(id, request, user);
        return ResponseEntity.ok(updatedList);
    }


}
