package com.example.taskrunner.controller;

import com.example.taskrunner.model.Task;
import com.example.taskrunner.model.TaskExecution;
import com.example.taskrunner.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // Whitelist of allowed commands
    private static final Set<String> ALLOWED_COMMANDS = new HashSet<>(Arrays.asList(
        "echo", "date", "time", "whoami", "hostname", "pwd", "ls", "dir"
    ));

    // Blacklist of dangerous patterns
    private static final List<Pattern> DANGEROUS_PATTERNS = Arrays.asList(
        Pattern.compile(".*\\b(rm|del|delete|format|rmdir)\\b.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\b(shutdown|reboot|restart)\\b.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\b(net\\s+user|useradd|adduser)\\b.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\b(taskkill|kill|pkill)\\b.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\b(wget|curl|invoke-webrequest)\\b.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\b(powershell|bash|sh|cmd)\\b.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*[;&|><`$\\\\].*"),
        Pattern.compile(".*\\.\\..*")
    );

    // Validate command for security
    private void validateCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }

        String trimmedCommand = command.trim().toLowerCase();
        
        // Extract base command (first word)
        String baseCommand = trimmedCommand.split("\\s+")[0];
        
        // Check if command is in whitelist
        if (!ALLOWED_COMMANDS.contains(baseCommand)) {
            throw new SecurityException("Command '" + baseCommand + "' is not allowed. Allowed commands: " + ALLOWED_COMMANDS);
        }

        // Check against dangerous patterns
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(command).matches()) {
                throw new SecurityException("Command contains dangerous patterns and is not allowed");
            }
        }
    }

    // PUT a task - Create or update a task
    @PutMapping
    public Task createOrUpdateTask(@RequestBody Task task) {
        // Validate command before saving
        validateCommand(task.getCommand());
        return taskRepository.save(task);
    }

    // GET tasks - Return all tasks or single task by ID
    @GetMapping
    public Object getTasks(@RequestParam(required = false) String id, 
                           @RequestParam(required = false) String name) {
        // If id parameter is provided, return single task
        if (id != null && !id.isEmpty()) {
            return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        }
        
        // If name parameter is provided, search by name
        if (name != null && !name.isEmpty()) {
            List<Task> tasks = taskRepository.findByNameContainingIgnoreCase(name);
            if (tasks.isEmpty()) {
                throw new RuntimeException("No tasks found with name: " + name);
            }
            return tasks;
        }
        
        // Otherwise return all tasks
        return taskRepository.findAll();
    }

    // Delete task by ID
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskRepository.deleteById(id);
    }

    // PUT a TaskExecution - Execute a shell command and store execution
    @PutMapping("/{id}/run")
    public Task runTask(@PathVariable String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        // Ensure taskExecutions list is initialized
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(new ArrayList<>());
        }

        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            // Execute the command through cmd.exe on Windows
            String os = System.getProperty("os.name").toLowerCase();
            Process process;
            
            if (os.contains("win")) {
                // Windows: use cmd.exe /c
                process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", task.getCommand()});
            } else {
                // Unix/Linux/Mac: use sh -c
                process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", task.getCommand()});
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            LocalDateTime endTime = LocalDateTime.now();

            // Create a new TaskExecution
            TaskExecution execution = new TaskExecution(
                    startTime,
                    endTime,
                    output.toString().trim()
            );

            // Add execution to task and save
            task.getTaskExecutions().add(execution);
            return taskRepository.save(task);

        } catch (Exception e) {
            LocalDateTime endTime = LocalDateTime.now();
            // Create a failed execution record even on exception
            TaskExecution execution = new TaskExecution(
                    startTime,
                    endTime,
                    "Error executing command: " + e.getMessage()
            );
            task.getTaskExecutions().add(execution);
            return taskRepository.save(task);
        }
    }
}
