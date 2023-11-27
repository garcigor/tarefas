import java.sql.Date;

public class Task {
    private int taskId;
    private String description;
    private Date dueDate;
    private int userId;

    public Task(int taskId, String description, Date dueDate, int userId) {
        this.taskId = taskId;
        this.description = description;
        this.dueDate = dueDate;
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Adicionando o método toString para facilitar a visualização
    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", userId=" + userId +
                '}';
    }
}


	
	
