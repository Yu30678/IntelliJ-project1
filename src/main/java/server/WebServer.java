package server;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controller.memberController;
import controller.productController;
import controller.cartController;
import controller.S3Controller;
import util.CORSWrapperHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
public class WebServer {
    public static void start() throws Exception{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/product", new productController());
        server.createContext("/member", new memberController());
        server.createContext("/cart", new cartController());
        server.createContext("/s3/list",     new CORSWrapperHandler(S3Controller.list(),     "http://localhost:3000"));
        server.createContext("/s3/upload",   new CORSWrapperHandler(S3Controller.upload(),   "http://localhost:3000"));
        server.createContext("/s3/download", new CORSWrapperHandler(S3Controller.download(), "http://localhost:3000"));
        server.createContext("/s3/delete",   new CORSWrapperHandler(S3Controller.delete(),   "http://localhost:3000"));
        //server.createContext("/order", new OrderController());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8080");
    }
}
