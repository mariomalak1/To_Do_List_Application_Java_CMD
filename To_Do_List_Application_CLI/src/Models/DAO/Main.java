package Models.DAO;

import Models.Task;
import Models.User;

import java.sql.SQLException;

public class Main {
    public static void main(String [] args) throws SQLException {
        TaskDAO taskDAO = new TaskDAO();
        User user = new User();
        Task task = new Task("Mario task1", "", 1, false, user);

        taskDAO.add(task);
        System.out.println(taskDAO.get(1));
    }
}
