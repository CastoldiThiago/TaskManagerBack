package com.CastoldiThiago.TaskManager.controller;

import com.CastoldiThiago.TaskManager.dto.TaskListCompleteDTO;
import com.CastoldiThiago.TaskManager.dto.TaskListDTO;
import com.CastoldiThiago.TaskManager.dto.UpdateListRequest;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.service.TaskListService;
import com.CastoldiThiago.TaskManager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task-lists")
public class TaskListController {

    private final TaskListService taskListService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TaskListDTO> createList(@RequestBody TaskListDTO taskList, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(taskListService.createList(taskList, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<TaskListDTO>> getLists(Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(taskListService.getAllListsByUser(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskListCompleteDTO> getTaskById(@PathVariable Long id) {
        TaskListCompleteDTO taskList = taskListService.getTaskListById(id);

        return ResponseEntity.ok(taskList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteList(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        taskListService.deleteList(id, user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskListDTO> updateList(@PathVariable Long id, @RequestBody UpdateListRequest request, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TaskListDTO updatedList = taskListService.updateList(id, request, user);
        return ResponseEntity.ok(updatedList);
    }


}
