package Views;

import Controllers.TaskController;
import Models.Task;
import Models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class TaskView {
    private User user;
    private TaskController taskController;

    public TaskView(User u){
        this.user = u;
    }


    public void createTask(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome in Create New Task page");

        System.out.print("Enter Your Task Name : ");
        String taskName = scanner.nextLine();

        if (taskName.equals("")){
            System.out.println("Must task have name");
            createTask();
        }

        System.out.print("Enter description for it : ");
        String description = scanner.nextLine();

        System.out.print("Enter description for it : ");
        String priorityString = scanner.nextLine();

        if (!MainView.isNumeric(priorityString)){
            System.out.println("Must enter numeric value for priority");
            createTask();
        }

        Integer priority = Integer.parseInt(priorityString);

        Task task =  new Task(user, taskName, priority, false, description, LocalDateTime.now());
        taskController.createTask(task);
    }

    public void showAllTasks(){
        List<Task> tasks = taskController.getAllTaskForUser();
        for (Task task: tasks){
            System.out.println("Task Name : " + task.getName() + "Priority : " + Task.Priorities.getPriorityByValue(task.getPriority()) + "Status : " + task.getStatus());
        }
    }

    public void showSpecificTask(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Task ID: ");
        String IdString = scanner.nextLine();
        if (!MainView.isNumeric(IdString)){
            System.out.println("Please enter valid value.");
            return;
        }
        int id = Integer.parseInt(IdString);

        Task task = taskController.getTaskByID(id);
        if (task != null){
            System.out.println(task);
        }
    }

    public void markAsComplete(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Task ID: ");
        String IdString = scanner.nextLine();
        if (!MainView.isNumeric(IdString)){
            System.out.println("Please enter valid value.");
            return;
        }
        int id = Integer.parseInt(IdString);

        Task task = taskController.getTaskByID(id);
        if (task != null){
            task.setStatus(true);
//            taskController;
        }
    }

    public void changeSpecificTask(){

    }

}