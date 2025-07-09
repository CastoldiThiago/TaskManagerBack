package com.CastoldiThiago.TaskManager.dto;

import com.CastoldiThiago.TaskManager.model.Task;
import com.CastoldiThiago.TaskManager.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private Boolean movedToMyDay;
    private LocalDateTime movedDate;
    private TaskStatus status;
    private Long listId;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.dueDate = task.getDueDate();
        this.movedToMyDay = task.getMovedToMyDay();
        this.movedDate = task.getMovedDate();
        this.status = task.getStatus();
        this.listId = task.getTaskList() != null ? task.getTaskList().getId() : null;
    }
}
