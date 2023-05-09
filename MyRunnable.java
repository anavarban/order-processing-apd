import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRunnable implements Runnable{

    private ExecutorService tpe;
    private AtomicInteger inQueue;
    private String type;

    private Scanner fileReaderOrders, fileReaderProducts;
    private FileWriter fileWriterOrders, fileWriterProducts;
    private String id;

    public MyRunnable(ExecutorService tpe, AtomicInteger inQueue, String type,
                      Scanner fileReaderOrders, Scanner fileReaderProducts,
                      FileWriter fileWriterOrders, FileWriter fileWriterProducts,
                      String id) {
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.type = type;
        this.fileReaderOrders = fileReaderOrders;
        this.fileReaderProducts = fileReaderProducts;
        this.fileWriterOrders = fileWriterOrders;
        this.fileWriterProducts = fileWriterProducts;
        this.id = id;
    }

    @Override
    public void run() {

        if (type.equals("order")) {
            if (fileReaderOrders.hasNextLine()) {
                String orderLine;
                synchronized (fileReaderProducts) {
                    orderLine = fileReaderOrders.nextLine();
                }

                String[] tok = orderLine.split(", ");
                String orderId = tok[0];
                int productNumber = Integer.parseInt(tok[1]);
                inQueue.addAndGet(productNumber);
                tpe.submit(new MyRunnable(tpe, inQueue, "product", fileReaderOrders, fileReaderProducts,
                        fileWriterOrders, fileWriterProducts, orderId));
                try {
                    fileWriterOrders.write(orderLine + ",shipped\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (type.equals("product")) {
            while (fileReaderProducts.hasNextLine()) {
                String productLine;
                synchronized (fileReaderProducts) {
                    productLine = fileReaderProducts.nextLine();
                }
                String[] tok = productLine.split(", ");
                String orderId = tok[0];
                String productId = tok[1];
                Tema2.seen.put(productId, false);
                if (orderId.equals(id)) {
                    if (Tema2.seen.get(productId) != null && !Tema2.seen.get(productId)) {
                        try {
                            fileWriterProducts.write(productLine + ",shipped\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Tema2.seen.replace(productId, true);
                    }
                }
            }
        }

        int left = inQueue.decrementAndGet();
        if (left == 0) {
            tpe.shutdown();
        }

    }
}