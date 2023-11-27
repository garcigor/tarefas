import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private Connection connection;

    public TaskManager(Connection connection) {
        tasks = new ArrayList<>();
        this.connection = connection;
    }

    public void adicionarUsuario(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            if (password != null) {
                statement.setString(2, password);
            } else {
                statement.setNull(2, java.sql.Types.VARCHAR);
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Usuário adicionado com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adicionarTarefa(String descricao, Date dataVencimento, String username) {
        // Verifica se o usuário existe antes de adicionar a tarefa
        int userId = obterUserId(username);
        if (userId == -1) {
            // Adiciona o usuário se não existir
            adicionarUsuario(username, null);
            userId = obterUserId(username);
        }

        String sql = "INSERT INTO tasks (description, due_date, user_id) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, descricao);
            statement.setDate(2, dataVencimento);
            statement.setInt(3, userId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int taskId = generatedKeys.getInt(1);
                        Task novaTarefa = new Task(taskId, descricao, dataVencimento, userId);
                        tasks.add(novaTarefa);
                        System.out.println("Tarefa adicionada com sucesso!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int obterUserId(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 se o usuário não for encontrado
     }
    

    private boolean verificarUsuarioExistente(int userId) {
        String sql = "SELECT user_id FROM users WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Retorna true se houver um usuário com o ID fornecido
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removerTarefa(int taskId) {
        String sql = "DELETE FROM tasks WHERE task_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                tasks.removeIf(task -> task.getTaskId() == taskId);
                System.out.println("Tarefa removida com sucesso!");
            } else {
                System.out.println("Tarefa com ID " + taskId + " não encontrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listarTarefas(String username) {
        // Consulta para obter o ID do usuário com base no nome de usuário
        String userIdQuery = "SELECT user_id FROM users WHERE username = ?";
        String sql = "SELECT * FROM tasks WHERE user_id = ?";

        try (PreparedStatement userIdStatement = connection.prepareStatement(userIdQuery)) {
            userIdStatement.setString(1, username);

            try (ResultSet userIdResultSet = userIdStatement.executeQuery()) {
                if (userIdResultSet.next()) {
                    int userId = userIdResultSet.getInt("user_id");

                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setInt(1, userId);

                        try (ResultSet resultSet = statement.executeQuery()) {
                            System.out.println("Lista de Tarefas para o usuário " + username + ":");
                            while (resultSet.next()) {
                                int taskId = resultSet.getInt("task_id");
                                String descricao = resultSet.getString("description");
                                Date dataVencimento = resultSet.getDate("due_date");

                                System.out.println("ID: " + taskId);
                                System.out.println("Descrição: " + descricao);
                                System.out.println("Data de Vencimento: " + dataVencimento);
                                System.out.println("------------------------");
                            }
                        }
                    }
                } else {
                    System.out.println("Usuário não encontrado: " + username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão com o banco de dados fechada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
