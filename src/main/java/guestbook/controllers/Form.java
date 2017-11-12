package guestbook.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import guestbook.exceptions.DAOException;
import guestbook.models.GuestBook;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Form implements HttpHandler {
    private GuestBook guestBook;

    public Form() throws DAOException {
        this.guestBook = new GuestBook();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String response;
        String method = httpExchange.getRequestMethod();

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/GuestBookTemplate.twig");
        JtwigModel model = JtwigModel.newModel();

        response = template.render(model);

        if (method.equalsIgnoreCase("post")) {
            evaluateGuestStatement(httpExchange);
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void evaluateGuestStatement(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        Map<String, String> inputs = parseFormData(formData);

        String name = inputs.get("Username");
        String message = inputs.get("Message");

        guestBook.addGuestStatement(name, message);
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
