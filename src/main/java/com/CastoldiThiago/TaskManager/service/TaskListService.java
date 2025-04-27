package com.CastoldiThiago.TaskManager.service;

import com.CastoldiThiago.TaskManager.dto.UpdateListRequest;
import com.CastoldiThiago.TaskManager.model.Task;
import com.CastoldiThiago.TaskManager.model.TaskList;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.repository.TaskListRepository;
import com.CastoldiThiago.TaskManager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskListService {

    private final TaskListRepository taskListRepository;

    public TaskList createList(TaskList taskList, User owner) {
        taskList.setOwner(owner);
        taskList.setCreatedAt(LocalDateTime.now());
        return taskListRepository.save(taskList);
    }

    public void deleteList(Long id, User owner) {
        TaskList list = taskListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lista no encontrada"));

        if(!list.getOwner().equals(owner)){
            throw new AccessDeniedException("No puedes eliminar listas de otro usuario");
        }

        taskListRepository.delete(list);
    }

    public List<TaskList> getAllListsByUser(User owner) {
        return taskListRepository.findAllByOwner(owner);
    }

    public TaskList updateList(Long id, UpdateListRequest request, User owner) {
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
        return taskListRepository.save(taskList);
    }

}

