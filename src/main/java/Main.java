import com.sun.net.httpserver.HttpServer;
import controller.*;

import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/member" , new memberController());
        server.createContext("/product", new productController());
        server.createContext("/cart", new cartController());
        server.createContext("/order", new orderController());
        server.createContext("/order_detail", new orderController());
        server.createContext("/user", new userController());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
