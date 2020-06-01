package servlet;

import model.BankClient;
import service.BankClientService;
import util.PageGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

@WebServlet("/transaction")
public class MoneyTransactionServlet extends HttpServlet {

    BankClientService bankClientService = new BankClientService();
    PageGenerator pageGenerator = PageGenerator.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println(pageGenerator.getPage("moneyTransactionPage.html", new HashMap<>()));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String senderNameInput = req.getParameter("senderName");
        String senderPassInput = req.getParameter("senderPass");
        String countInput = req.getParameter("count");
        String nameToInput = req.getParameter("nameTo");
        Long countValue = Long.parseLong(countInput);
        BankClient sender = new BankClient();
        sender.setName(senderNameInput);
        sender.setPassword(senderPassInput);
        HashMap<String, Object> parameters = new HashMap<>();
        try {
            if (bankClientService.sendMoneyToClient(sender, nameToInput, countValue)) {
                resp.setContentType("text/html;charset=utf-8");
                parameters.put("message", "The transaction was successful");
                resp.getWriter().println(pageGenerator.getPage("resultPage.html", parameters));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setContentType("text/html;charset=utf-8");
                parameters.put("message", "transaction rejected");
                resp.getWriter().println(pageGenerator.getPage("resultPage.html", parameters));
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

    }
}
