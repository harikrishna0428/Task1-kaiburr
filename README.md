# Task Runner Application

A Spring Boot-based task execution system that allows running predefined system commands through a REST API. The application provides a secure way to execute whitelisted system commands and track their execution history.

## Features

- Create, read, update, and delete tasks
- Execute predefined system commands (whitelisted for security)
- View execution history for each task
- Track execution status and output
- Secure command execution with whitelisting
- MongoDB for data persistence

## Prerequisites

- Java 11 or higher
- Maven 3.6.3 or higher
- MongoDB 4.4 or higher
- Git (optional)

## Getting Started

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd taskrunner
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080` by default.

## API Endpoints

### Tasks

- `GET /tasks` - Get all tasks
- `GET /tasks/{id}` - Get a specific task by ID
- `POST /tasks` - Create a new task
  ```json
  {
    "name": "Task Name",
    "owner": "Owner Name",
    "command": "echo Hello World"
  }
  ```
- `PUT /tasks/{id}` - Update a task
- `DELETE /tasks/{id}` - Delete a task
- `POST /tasks/{id}/execute` - Execute a task

### Task Executions

- `GET /tasks/{taskId}/executions` - Get all executions for a task
- `GET /tasks/{taskId}/executions/{executionId}` - Get a specific execution

## Allowed Commands

The application only allows the following commands to be executed:
- echo
- date
- time
- whoami
- hostname
- pwd
- ls
- dir

## Security

- Command injection is prevented by whitelisting allowed commands
- All commands are executed in a controlled environment
- Input validation is performed on all endpoints

## Screenshots

### Task List
<!-- Add screenshot of task list view here -->

### Create New Task
<!-- Add screenshot of create task form here -->

### Task Execution History
<!-- Add screenshot of execution history here -->

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with Spring Boot
- Uses MongoDB for data storage
- Inspired by task automation tools
