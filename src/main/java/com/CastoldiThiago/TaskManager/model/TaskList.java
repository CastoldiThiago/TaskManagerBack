package com.CastoldiThiago.TaskManager.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Table(name = TaskList.TABLE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaskList {
    public static final String TABLE_NAME = "task_lists";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @ManyToOne
    private User owner;

    @JsonManagedReference
    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
