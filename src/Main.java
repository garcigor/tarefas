import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connection = conectarAoBancoDeDados();

        TaskManager taskManager = new TaskManager(connection);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            exibirMenu();

            int escolha = scanner.nextInt();
            scanner.nextLine();  // Limpa o buffer do teclado

            switch (escolha) {
                case 1:
                    adicionarTarefa(scanner, taskManager);
                    break;
                case 2:
                    removerTarefa(scanner, taskManager);
                    break;
                case 3:
                    listarTarefas(scanner, taskManager);
                    break;
                case 4:
                    adicionarUsuario(scanner, taskManager);
                    break;
                case 0:
                    encerrarPrograma(taskManager, connection);
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static Connection conectarAoBancoDeDados() {
        String url = "jdbc:mysql://localhost:3306/task_manager_db?useSSL=false";
        String usuario = "root";
        String senha = "9090@Igvigor";

        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    private static void exibirMenu() {
        System.out.println("Escolha uma opção:");
        System.out.println("1 - Adicionar Tarefa");
        System.out.println("2 - Remover Tarefa");
        System.out.println("3 - Listar Tarefas");
        System.out.println("4 - Adicionar Usuário");
        System.out.println("0 - Sair");
    }

    private static void adicionarTarefa(Scanner scanner, TaskManager taskManager) {
        System.out.println("Digite a descrição da tarefa:");
        String descricao = scanner.nextLine();

        System.out.println("Digite a data de vencimento da tarefa (formato YYYY-MM-DD):");
        String dataVencimentoStr = scanner.nextLine();
        Date dataVencimento;
        try {
            dataVencimento = new SimpleDateFormat("yyyy-MM-dd").parse(dataVencimentoStr);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido. Tarefa não adicionada.");
            return;
        }

        System.out.println("Digite o nome de usuário:");
        String username = scanner.nextLine();

        taskManager.adicionarTarefa(descricao, new java.sql.Date(dataVencimento.getTime()), username);
    }

    private static void adicionarUsuario(Scanner scanner, TaskManager taskManager) {
        System.out.println("Digite o nome de usuário:");
        String username = scanner.nextLine();

        System.out.println("Digite a senha:");
        String senha = scanner.nextLine();

        taskManager.adicionarUsuario(username, senha);

    }

    private static void removerTarefa(Scanner scanner, TaskManager taskManager) {
        System.out.println("Digite o ID da tarefa que deseja remover:");
        int taskId = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer do teclado
        taskManager.removerTarefa(taskId);
    }

    private static void listarTarefas(Scanner scanner, TaskManager taskManager) {
        System.out.println("Digite o nome de usuário para listar suas tarefas:");
        String username = scanner.nextLine();
        taskManager.listarTarefas(username);
    }

    private static void encerrarPrograma(TaskManager taskManager, Connection connection) {
        System.out.println("Saindo do programa. Até logo!");
        taskManager.fecharConexao();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}


