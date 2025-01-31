package Models.DAO;

import Models.Task;
import Models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements ITaskDAO {
    private final DataBaseManager dataBaseManager;

    public TaskDAO(){
        dataBaseManager = DataBaseManager.getDataBaseManager();
    }

    @Override
    public Task add(Task task) {
        Connection connection = dataBaseManager.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tasks (name, description, status, priority, userID) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setBoolean(3, task.getStatus());
            preparedStatement.setInt(4, task.getPriority());
            preparedStatement.setInt(5, task.getUser().getID());
            int recordsAffected = preparedStatement.executeUpdate();
            if (recordsAffected == 0){
                System.out.println("Can't add this task right now, please check your database connection.");
                return null;
            }
            // to get generated ID for task
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedID = generatedKeys.getInt(1);
                    task.setID(generatedID);
                    return task;
                } else {
                    System.out.println("No ID get to task, as it failed to save it.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Can't add this task right now, please check your database connection.");
        }
        return null;
    }

    @Override
    public Task get(int id) {
        Connection connection = dataBaseManager.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from tasks where id = " + id);

            if (!resultSet.next()){
                return null;
            }
            return extractTaskDataInModel(resultSet);
        } catch (SQLException e) {
            System.out.println("Can't add this task right now, please check your database connection.");
        }
        return null;
    }

    @Override
    public Task getTaskForUser(int id, User user) {
        Task task = this.get(id);
        task.setUser(user);
        return task;
    }

    @Override
    public Boolean delete(Task task) {
        return null;
    }

    @Override
    public Boolean update(int taskID, Task task2) {
        Connection connection = dataBaseManager.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tasks SET name = ?, description = ?, status = ?, priority = ? WHERE userID = ? and ID = ?");
            preparedStatement.setString(1, task2.getName());
            preparedStatement.setString(2, task2.getDescription());
            preparedStatement.setBoolean(3, task2.getStatus());
            preparedStatement.setInt(4, task2.getPriority());
            preparedStatement.setInt(5, task2.getUser().getID());
            preparedStatement.setInt(6, task2.getID());
            int rowAffected = preparedStatement.executeUpdate();
            return rowAffected != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Task> getAllTaskForUser(User user) {
        return getTaskWithFiltration(user, null, null, null, null);
    }

    @Override
    public List<Task> getTaskWithFiltration(User user, String name, String description, Boolean status, Integer priority) {
        Connection connection = dataBaseManager.getConnection();
        try {
            String query ="select * from tasks where userID = ?";
            int parameterNumber = 1;
            if (name != null){
                query += " and name LIKE ?";
            }
            if (description != null){
                query += " and description LIKE ?";
            }
            if (status != null){
                query += " and status = ?";
            }
            if (priority != null){
                query += " and priority = ?";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getID());
            if (name != null){
                parameterNumber++;
                preparedStatement.setString(parameterNumber,"%" + name + "%");
            }
            if (description != null){
                parameterNumber++;
                preparedStatement.setString(parameterNumber, "%" + description + "%");
            }
            if (status != null){
                parameterNumber++;
                preparedStatement.setBoolean(parameterNumber, status);
            }
            if (priority != null){
                parameterNumber++;
                preparedStatement.setInt(parameterNumber, priority);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (resultSet.next()){
                Task task = extractTaskDataInModel(resultSet);
                task.setUser(user);
                tasks.add(task);
            }


            return tasks;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Task extractTaskDataInModel(ResultSet resultSet) throws SQLException{
        Task task = new Task();
        try {
            task.setID(resultSet.getInt("ID"));
            task.setName(resultSet.getString("name"));
            task.setDescription(resultSet.getString("description"));
            task.setPriority(resultSet.getInt("priority"));
            task.setStatus(resultSet.getBoolean("status"));
            task.setDateTime((LocalDateTime)resultSet.getObject("datetime"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return task;
    }
}
