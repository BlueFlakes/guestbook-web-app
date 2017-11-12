package guestbook.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import guestbook.dao.GuestBookDao;
import guestbook.exceptions.DAOException;
import guestbook.models.GuestStatement;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Form implements HttpHandler {
    private GuestBookDao guestBookDao;
    private List<GuestStatement> guestStatements;

    public Form() throws DAOException {
        this.guestBookDao = new GuestBookDao();
        loadGuestStatements();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String response;
        String method = httpExchange.getRequestMethod();

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/GuestBookTemplate.twig");
        JtwigModel model = JtwigModel.newModel();

        response = template.render(model);

        if (method.equalsIgnoreCase("post")) {
            saveGuestStatement(httpExchange);
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void loadGuestStatements() throws DAOException {
        this.guestStatements = this.guestBookDao.getGuestStatements();
    }

    private void saveGuestStatement(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        Map<String, String> inputs = parseFormData(formData);

        String name = inputs.get("Username");
        String message = inputs.get("Message");

        addToGuestStatementsList(name, message);
        saveToDatabase(name, message);
    }

    private void addToGuestStatementsList(String name, String message) {
        int probablyLastIndex = this.guestStatements.size() - 1;
        int highestId = this.guestStatements.size() > 0 ? this.guestStatements.get(probablyLastIndex).getId()
                                                        : 0;
        int uniqueId = highestId + 1;

        GuestStatement stmt = new GuestStatement(uniqueId, name, LocalDateTime.now(), message);
        this.guestStatements.add(stmt);
    }

    private void saveToDatabase(String name, String message) {
        try {
            guestBookDao.addGuestStatement(name, message);
        } catch (DAOException e) {
            // TODO exception management
        }
    }

    /**
     * Form data is sent as a urlencoded string. Thus we have to parse this string to get data that we want.
     * See: https://en.wikipedia.org/wiki/POST_(HTTP)
     */
    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }
}
