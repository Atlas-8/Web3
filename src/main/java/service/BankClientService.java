package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;
import javax.servlet.ServletException;
import java.sql.*;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) throws ServletException {
        BankClientDAO dao = getBankClientDAO();
        try {
            return dao.getClientByName(name);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    public List<BankClient> getAllClient() throws ServletException {
        BankClientDAO dao = getBankClientDAO();
        try {
            return dao.getAllBankClient();
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    public boolean deleteClient(String name) {
        Connection connection = getMysqlConnection();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM bank_client WHERE name='" + name + "'");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addClient(BankClient client) throws DBException {
        BankClientDAO dao = getBankClientDAO();
        Connection connection = getMysqlConnection();
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SELECT * FROM bank_client WHERE name='" + client.getName() + "'");
            ResultSet result = stmt.getResultSet();
            if (!result.next()) {
                dao.addClient(client);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) throws SQLException, ServletException {
        BankClientDAO dao = getBankClientDAO();
        if (!dao.validateClient(sender.getName(), sender.getPassword())) {
            return false;
        }
        sender.setId(dao.getClientIdByName(sender.getName()));
        sender.setMoney(dao.getClientById(sender.getId()).getMoney());
        if (!dao.isClientHasSum(sender.getName(), value)) {
            return false;
        }
        BankClient receiver = dao.getClientByName(name);
        dao.updateClientsMoney(sender.getName(), sender.getPassword(), value*(-1));
        dao.updateClientsMoney(receiver.getName(), receiver.getPassword(), value);
        return true;
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
    public void createTable() throws DBException{
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").          //db type
                    append("localhost:").             //host name
                    append("3306/").                  //port
                    append("web3?").                  //db name
                    append("user=Atlas&").            //login
                    append("password=1987010688&").   //password
                    append("useSSL=false");           //disable SSL

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
