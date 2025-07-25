package com.CastoldiThiago.TaskManager.dto;

import com.CastoldiThiago.TaskManager.model.TaskStatus;
import lombok.Data;

@Data
public class ChangeTaskStatusRequest {
    private TaskStatus state;
}
