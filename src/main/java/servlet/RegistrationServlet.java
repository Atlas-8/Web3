package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    PageGenerator pageGenerator = PageGenerator.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println(pageGenerator.getPage("registrationPage.html", new HashMap<>()));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nameInput = req.getParameter("name");
        String passwordInput = req.getParameter("password");
        String moneyInput = req.getParameter("money");
        Long moneyCount = Long.parseLong(moneyInput);
        HashMap<String, Object> parameters = new HashMap<>();
        try {
            if (new BankClientService().addClient(new BankClient(nameInput, passwordInput, moneyCount))) {
                resp.setContentType("text/html;charset=utf-8");
                parameters.put("message", "Add client successful");
                resp.getWriter().println(pageGenerator.getPage("resultPage.html", parameters));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setContentType("text/html;charset=utf-8");
                parameters.put("message", "Client not add");
                resp.getWriter().println(pageGenerator.getPage("resultPage.html", parameters));
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (DBException e) {
            resp.setStatus(400);
        }
    }
}
