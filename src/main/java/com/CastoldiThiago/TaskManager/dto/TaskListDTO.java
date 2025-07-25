package com.CastoldiThiago.TaskManager.dto;

import com.CastoldiThiago.TaskManager.model.TaskList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDTO {
    private long id;
    private String name;
    private String description;

    public TaskListDTO(TaskList save) {
        this.id = save.getId();
        this.name = save.getName();
        this.description = save.getDescription();
    }
}
