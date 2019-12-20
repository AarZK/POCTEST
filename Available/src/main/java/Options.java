import java.sql.ResultSet;
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
        System.out.println("7:增加列(初始化)");
        System.out.println("8:删除列");
        System.out.println("9:并发转账");
        System.out.println("10:模拟秒杀");
        System.out.println("q:退出");
        System.out.println("<< 请输入操作序号：");

        String optionFlag = null;
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            optionFlag = scanner.nextLine();
        }

        switch (optionFlag) {
            case "1":
                PrepareData.prepareTable();
                menu();
                break;
            case "2":
                concur("2");
                menu();
                break;
            case "3":
                PrepareData.getCount();
                menu();
                break;
            case "4":
                concur("4");
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
            case "7":
                PrepareData.addColumn();
                menu();
                break;
            case "8":
                PrepareData.deleteColumn();
                menu();
                break;
            case "9":
                PrepareData.getAvgBalance();
                concur("9");
                PrepareData.getAvgBalance();
                menu();
                break;
            case "10":
                PrepareData.prepareGoods();
                concur("10");
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

    public static void concur(String optionFlag) {
        Scanner scanner = new Scanner(System.in);

        int total = 0;
        System.out.println("<< 请输入执行次数:");
        new Scanner(System.in);
        if (scanner.hasNextLine()) {
            total = Integer.parseInt(scanner.nextLine());
        }
        int concurrent = 0;
        System.out.println("<< 请输入并发数:");
        new Scanner(System.in);
        if (scanner.hasNextLine()) {
            concurrent = Integer.parseInt(scanner.nextLine());
        }

        int questions1 = 0;
        int commit1 = 0;
        int rollback1 = 0;
        int select1 = 0;
        int insert1 = 0;
        int delete1 = 0;
        int update1 = 0;
        int uptime1 = 0;

        ResultSet globalMessage = null;
        globalMessage = PrepareData.getGlobalStatus();
        while (true) {
            try {
                if (!globalMessage.next()) {
                    break;
                } else {

                    switch (globalMessage.getString(1)) {
                        case "Questions":
                            questions1 = globalMessage.getInt(2);
                            break;
                        case "Com_commit":
                            commit1 = globalMessage.getInt(2);
                            break;
                        case "Com_rollback":
                            rollback1 = globalMessage.getInt(2);
                            break;
                        case "Com_select":
                            select1 = globalMessage.getInt(2);
                            break;
                        case "Com_insert":
                            insert1 = globalMessage.getInt(2);
                            break;
                        case "Com_delete":
                            delete1 = globalMessage.getInt(2);
                            break;
                        case "Com_update":
                            update1 = globalMessage.getInt(2);
                            break;
                        case "Uptime":
                            uptime1 = globalMessage.getInt(2);
                            break;
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        //线程池
        ExecutorService executor = Executors.newFixedThreadPool(concurrent);
        //定义信号量，只能n个线程同时访问
        final Semaphore semaphore = new Semaphore(concurrent);
        //创建一个初始值为n的倒数计数器
        final CountDownLatch countDownLatch = new CountDownLatch(total);
        int loopFlag;
        int from = 0;
        from = PrepareData.getMaxSeq() + 1;
        for (loopFlag = from; loopFlag < total + from; loopFlag++) {
//            Thread thread = new InsertThread(semaphore, countDownLatch);
            switch (optionFlag) {
                case "2":
                    executor.execute(new InsertThread(semaphore, countDownLatch, loopFlag));
                    break;
                case "4":
                    executor.execute(new SelectThread(semaphore, countDownLatch));
                    break;
                case "9":
                    executor.execute(new TransferThread(semaphore, countDownLatch));
                    break;
                case "10":
                    executor.execute(new BuyThread(semaphore, countDownLatch));
                    break;
            }

//            thread.start();
        }

        // 阻塞当前线程，直到倒数计数器倒数到0
        try {
            countDownLatch.await();
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int questions2 = 0;
        int commit2 = 0;
        int rollback2 = 0;
        int select2 = 0;
        int insert2 = 0;
        int delete2 = 0;
        int update2 = 0;
        int uptime2 = 0;

        globalMessage = null;
        globalMessage = PrepareData.getGlobalStatus();
        while (true) {
            try {
                if (!globalMessage.next()) {
                    break;
                } else {
                    switch (globalMessage.getString(1)) {
                        case "Questions":
                            questions2 = globalMessage.getInt(2);
                            break;
                        case "Com_commit":
                            commit2 = globalMessage.getInt(2);
                            break;
                        case "Com_rollback":
                            rollback2 = globalMessage.getInt(2);
                            break;
                        case "Com_select":
                            select2 = globalMessage.getInt(2);
                            break;
                        case "Com_insert":
                            insert2 = globalMessage.getInt(2);
                            break;
                        case "Com_delete":
                            delete2 = globalMessage.getInt(2);
                            break;
                        case "Com_update":
                            update2 = globalMessage.getInt(2);
                            break;
                        case "Uptime":
                            uptime2 = globalMessage.getInt(2);
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int questions = questions2 - questions1;
        int commit = commit2 - commit1;
        int rollback = rollback2 - rollback1;
        int select = select2 - select1;
        int insert = insert2 - insert1;
        int delete = delete2 - delete1;
        int update = update2 - update1;
        int uptime = uptime2 - uptime1;
        if (uptime == 0) {
            uptime = 1;
        }
        int QPS = questions / uptime;
        int TPS = (commit + rollback) / uptime;
        System.out.println(">>");
        System.out.println("   请求数: " + questions + " 事务提交数：" + commit + " 事务回滚数：" + rollback + " 查询数：" + select + " 插入数：" + insert + " 更新数：" + update + " 删除数： " + delete + " sql 耗时：" + uptime);
        System.out.println("   QPS:" + QPS + " TPS:" + TPS);
        System.out.println(">>");
    }

}
