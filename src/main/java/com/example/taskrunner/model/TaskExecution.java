package com.example.taskrunner.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TaskExecution {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;

    private String output;

    public TaskExecution() {}

    public TaskExecution(LocalDateTime startTime, LocalDateTime endTime, String output) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.output = output;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
