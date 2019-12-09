import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Options {
    public static void menu() {
        System.out.println("***********************************************");
        System.out.println("                    操作菜单");
        System.out.println("***********************************************");
        System.out.println("1:创建表");
        System.out.println("2:并发插入");
        System.out.println("3:查询数据量");
        System.out.println("4:并发查询");
        System.out.println("5:创建索引");
        System.out.println("6:删除索引");
        System.out.println("q:退出");
        System.out.println(">> 请输入操作序号：");

        String optionFlag = null;
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            optionFlag = scanner.nextLine();
        }

        switch (optionFlag) {
            case "1":
                createTable();
                menu();
                break;
            case "2":
                insertRecord();
                break;
            case "3":
                PrepareData.getCount();
                menu();
                break;
            case "4":
                concurSelect();
                menu();
                break;
            case "5":
                PrepareData.createIndex();
                menu();
                break;
            case "6":
                PrepareData.dropIndex();
                menu();
                break;
            case "q":
                System.exit(0);
                break;
            default:
                System.out.println("未知操作！");
                menu();
        }
    }

    public static void createTable() {
        PrepareData.prepareTable();
    }

    public static void insertRecord() {
        Scanner scanner = new Scanner(System.in);

        int total = 0;
        System.out.println("请输入预期插入总记录数:");
        new Scanner(System.in);
        if (scanner.hasNextLine()) {
            total = Integer.parseInt(scanner.nextLine());
        }
        int concurrent = 0;
        System.out.println("请输入并发数:");
        new Scanner(System.in);
        if (scanner.hasNextLine()) {
            concurrent = Integer.parseInt(scanner.nextLine());
        }
        //线程池
        ExecutorService executor = Executors.newFixedThreadPool(concurrent);
        //定义信号量，只能n个线程同时访问
        final Semaphore semaphore = new Semaphore(concurrent);
        //创建一个初始值为n的倒数计数器
        final CountDownLatch countDownLatch = new CountDownLatch(total);
        int loopFlag;
        for (loopFlag = 0; loopFlag < total; loopFlag++) {
//            Thread thread = new InsertThread(semaphore, countDownLatch);
            executor.execute(new InsertThread(semaphore, countDownLatch));
//            thread.start();

        }
        // 阻塞当前线程，直到倒数计数器倒数到0
        try {
            countDownLatch.await();
            executor.shutdown();
            menu();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void concurSelect() {
        Scanner scanner = new Scanner(System.in);

        int total = 0;
        System.out.println("请输入总查询次数:");
        new Scanner(System.in);
        if (scanner.hasNextLine()) {
            total = Integer.parseInt(scanner.nextLine());
        }
        int concurrent = 0;
        System.out.println("请输入并发数:");
        new Scanner(System.in);
        if (scanner.hasNextLine()) {
            concurrent = Integer.parseInt(scanner.nextLine());
        }
        //线程池
        ExecutorService executor = Executors.newFixedThreadPool(concurrent);
        //定义信号量，只能n个线程同时访问
        final Semaphore semaphore = new Semaphore(concurrent);
        //创建一个初始值为n的倒数计数器
        final CountDownLatch countDownLatch = new CountDownLatch(total);
        int loopFlag;
        for (loopFlag = 0; loopFlag < total; loopFlag++) {
//            Thread thread = new InsertThread(semaphore, countDownLatch);
            executor.execute(new SelectThread(semaphore, countDownLatch));
//            thread.start();
        }
        // 阻塞当前线程，直到倒数计数器倒数到0
        try {
            countDownLatch.await();
            executor.shutdown();
            menu();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
