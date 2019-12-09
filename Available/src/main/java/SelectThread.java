import com.github.javafaker.Faker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class SelectThread extends Thread {
    private CountDownLatch countDownLatch;
    private Semaphore semaphore;

    public SelectThread(Semaphore semaphore, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.semaphore = semaphore;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        try {
            //获取许可
            semaphore.acquire();
            countryCount();
            //访问完后，释放
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 倒数器减1
            countDownLatch.countDown();
        }

    }

    public static void countryCount() {
        Connection connection = JDBCUtil.getConnection();
        String sql = "select count(1) from available_test where country=?;";
        String country = new Faker().country().name();
        PreparedStatement preparedStatement = null;
        ResultSet total = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, country);
            total = preparedStatement.executeQuery();
            while (total.next()) {
                System.out.println(country + ":" + total.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(total, connection, preparedStatement);
        }
    }
}
