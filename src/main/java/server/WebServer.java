package server;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controller.memberController;
import controller.productController;
import controller.cartController;
import java.io.IOException;
import java.net.InetSocketAddress;
public class WebServer {
    public static void start() throws Exception{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/product", new productController());
        server.createContext("/member", new memberController());
        server.createContext("/cart", new cartController());
        //server.createContext("/order", new OrderController());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8080");
    }
}
