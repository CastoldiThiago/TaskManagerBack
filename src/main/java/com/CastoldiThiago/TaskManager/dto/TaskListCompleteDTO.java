package com.CastoldiThiago.TaskManager.dto;

import com.CastoldiThiago.TaskManager.model.TaskList;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TaskListCompleteDTO {
    private long id;
    private String name;
    private String description;
    private List<TaskDTO> tasks;

    public TaskListCompleteDTO(TaskList taskList) {
        this.id = taskList.getId();
        this.name = taskList.getName();
        this.description = taskList.getDescription();
        this.tasks = taskList.getTasks().stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }
}
