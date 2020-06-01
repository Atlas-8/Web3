package dao;

import com.sun.deploy.util.SessionState;
import model.BankClient;

import javax.servlet.ServletException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SELECT * FROM bank_client;");
        ResultSet result = stmt.getResultSet();
        List<BankClient> bankClientList = new ArrayList<>();
        while (result.next()) {
            bankClientList.add(new BankClient(result.getLong("id"), result.getString("name"),
                    result.getString("password"), result.getLong("money")));
        }
        result.close();
        stmt.close();
        return bankClientList;
    }

    public boolean validateClient(String name, String password) throws SQLException {
        String sql = "SELECT * FROM bank_client WHERE name=? AND password=?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, password);
            statement.execute();
            try (ResultSet result = statement.getResultSet()) {
                return result.next();
            }
        }
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException, ServletException {
        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE bank_client SET money=money+? WHERE name=? AND password=?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, transactValue);
            statement.setString(2, name);
            statement.setString(3, password);
            statement.executeUpdate();
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            connection.rollback();
            throw new ServletException(e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public BankClient getClientById(long id) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SELECT * FROM bank_client WHERE id='" + id + "';");
            try (ResultSet result = stmt.getResultSet()) {
                result.next();
                return new BankClient(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getString("password"),
                        result.getLong("money"));
            }
        }
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        BankClient client = getClientByName(name);
        return client.getMoney() >= expectedSum;
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SELECT * FROM bank_client WHERE name='" + name + "';");
        ResultSet result = stmt.getResultSet();
        result.next();
        Long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SELECT * FROM bank_client WHERE name='" + name + "';");
            try (ResultSet result = stmt.getResultSet()) {
                result.next();
                return new BankClient(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getString("password"),
                        result.getLong("money"));
            }
        }
    }

    public void addClient(BankClient client) throws SQLException {
        String sql = "INSERT INTO bank_client (name, password, money) VALUES (?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, client.getName());
        statement.setString(2, client.getPassword());
        statement.setLong(3, client.getMoney());
        statement.executeUpdate();
        statement.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS bank_client (id BIGINT AUTO_INCREMENT, " +
                "name VARCHAR(256), password VARCHAR(256), money BIGINT, PRIMARY KEY (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}