package com.CastoldiThiago.TaskManager.service;

import com.CastoldiThiago.TaskManager.dto.TaskListCompleteDTO;
import com.CastoldiThiago.TaskManager.dto.TaskListDTO;
import com.CastoldiThiago.TaskManager.dto.UpdateListRequest;
import com.CastoldiThiago.TaskManager.exception.ResourceNotFoundException;
import com.CastoldiThiago.TaskManager.model.TaskList;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.repository.TaskListRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskListService {

    private final TaskListRepository taskListRepository;

    public TaskListDTO createList(TaskListDTO taskList, User owner) {
        TaskList newTaskList = new TaskList();
        newTaskList.setName(taskList.getName());
        newTaskList.setOwner(owner);
        newTaskList.setDescription(taskList.getDescription());
        newTaskList.setCreatedAt(LocalDateTime.now());
        return new TaskListDTO(taskListRepository.save(newTaskList));
    }

    public TaskListCompleteDTO getTaskListById(Long taskListId) {
        TaskList taskList = taskListRepository.findById(taskListId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskListId));
        return new TaskListCompleteDTO(taskList);
    }

    public void deleteList(Long id, User owner) {
        TaskList list = taskListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lista no encontrada"));

        if(!list.getOwner().equals(owner)){
            throw new AccessDeniedException("No puedes eliminar listas de otro usuario");
        }

        taskListRepository.delete(list);
    }

    public List<TaskListDTO> getAllListsByUser(User owner) {
        return taskListRepository.findAllByOwner(owner)
                .stream()
                .map(TaskListDTO::new)
                .collect(Collectors.toList());
    }

    public TaskListDTO updateList(Long id, UpdateListRequest request, User owner) {
        TaskList taskList = taskListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista de tareas no encontrada"));

        if (!taskList.getOwner().equals(owner)) {
            throw new AccessDeniedException("Unauthorized update attempt");
        }

        if (request.getName() != null) {
            taskList.setName(request.getName());
        }

        if (request.getDescription() != null) {
            taskList.setDescription(request.getDescription());
        }
        return new TaskListDTO(taskListRepository.save(taskList));
    }

}

