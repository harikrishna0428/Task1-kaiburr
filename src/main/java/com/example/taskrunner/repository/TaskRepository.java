package com.example.taskrunner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.taskrunner.model.Task;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByNameContainingIgnoreCase(String name);
    List<Task> findByOwner(String owner);
}
