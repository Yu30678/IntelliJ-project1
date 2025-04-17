package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.productDAO;
import model.product;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class productController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getPath().equals("/product") &&
                exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            try {
                List<product> product = productDAO.getAllProducts();
                String response = new Gson().toJson(product);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                String error = new Gson().toJson("Error loading products");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }
}
