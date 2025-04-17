import com.sun.net.httpserver.HttpServer;
import controller.memberController;
import controller.productController;
import controller.cartController;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/member" , new memberController());
        server.createContext("/product", new productController());
        server.createContext("/cart", new cartController());
        //server.createContext("/member/register", new memberRegisterHandler());
        //server.createContext("/member/login", new memberLoginHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
