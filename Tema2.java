import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    public static String folder;
    public static int P;
    public static FileWriter fileWriterOrders;
    public static FileWriter fileWriterProducts;

    public static Scanner fileReaderOrders;
    public static Scanner fileReaderProducts;
    public static ConcurrentHashMap<String, Boolean> seen = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        folder = args[0];
        P = Integer.parseInt(args[1]);
        fileWriterOrders = new FileWriter("orders_out.txt");
        fileWriterProducts = new FileWriter("order_products_out.txt");

        fileReaderOrders = new Scanner(new File(folder + "/orders.txt"));
        fileReaderProducts = new Scanner(new File(folder + "/order_products.txt"));

        AtomicInteger inQueue  = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(P);

        inQueue.incrementAndGet();
        tpe.submit(new MyRunnable(tpe, inQueue, "order", fileReaderOrders,
                fileReaderProducts, fileWriterOrders, fileWriterProducts, ""));


        fileWriterProducts.close();
        fileWriterOrders.close();
        fileReaderProducts.close();
        fileReaderProducts.close();
    }
}