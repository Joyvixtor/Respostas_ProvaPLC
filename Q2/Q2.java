import java.util.*;
import java.util.concurrent.*;

public class Q2 {
    public static void main(String[] args) {
        int bees;
        int beeTasks;
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite a quantidade de abelhas operárias disponíveis: ");
        bees = sc.nextInt();

        System.out.print("E quantas tarefas estas operárias irão realizar?: ");
        beeTasks = sc.nextInt();

        List<Task> tasks = new ArrayList<>();
        String taskData;

        System.out.println("Os detalhes das tarefas no formato: id tempo task1 task2 ...");
        sc.nextLine();

        for (int i = 0; i < beeTasks; i++) {
            taskData = sc.nextLine();
            tasks.add(new Task(taskData));
        }
        sc.close();

        ExecutorService executorService = Executors.newFixedThreadPool(bees);

        
        Set<Integer> completedTaskIds = new HashSet<>();

        while (!tasks.isEmpty()) {
            for (Task task : tasks) {
                if (allDependenciesCompleted(task, completedTaskIds)) {
                    executorService.submit(() -> {
                        task.execute();
                        System.out.println("Tarefa " + task.id + " feita");
                        completedTaskIds.add(task.id);
                    });
                    tasks.remove(task);
                    break;
                }
            }
        }

        executorService.shutdown();
    }

    private static boolean allDependenciesCompleted(Task task, Set<Integer> completedTaskIds) {
        for (int dependency : task.taskDependencies) {
            if (!completedTaskIds.contains(dependency)) {
                return false;
            }
        }
        return true;
    }
}

class Task {
    public int id;
    public long requiredResolutionTime;
    public List<Integer> taskDependencies;

    public Task(String taskData) {
        String[] input = taskData.split("\\s+");

        this.id = Integer.parseInt(input[0]);
        this.requiredResolutionTime = Long.parseLong(input[1]);
        this.taskDependencies = new ArrayList<>();

        for (int i = 2; i < input.length; i++) {
            this.taskDependencies.add(Integer.parseInt(input[i]));
        }
    }

    public void execute() {
        try {
            Thread.sleep(requiredResolutionTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
