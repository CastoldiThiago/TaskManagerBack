package com.CastoldiThiago.TaskManager.dto;

import com.CastoldiThiago.TaskManager.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private Long listId;
    private Boolean movedToMyDay;
    private LocalDateTime movedDate;
}
