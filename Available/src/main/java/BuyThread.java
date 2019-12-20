import org.omg.CORBA.StringHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class BuyThread extends Thread {

    private CountDownLatch countDownLatch;
    private Semaphore semaphore;

    public BuyThread(Semaphore semaphore, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            //获取许可
            semaphore.acquire();
            buy();
            //访问完后，释放
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 倒数器减1
            countDownLatch.countDown();
        }
    }

    private void buy() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String count = "select count(1) from people;";
        String getPrice = "select g_price from goods where g_name='something';";
        String getGoodName = "select g_name from goods;";
        ResultSet resultSet = null;
        int limit = 0;
        String goodName = null;
        Double price = 0.00;
        try {
            preparedStatement = connection.prepareStatement(count);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                limit = resultSet.getInt(1);
            }
            preparedStatement = connection.prepareStatement(getPrice);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                price = resultSet.getDouble(1);
            }
            preparedStatement = connection.prepareStatement(getGoodName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                goodName = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JDBCUtil.beginTransaction(connection);

        int user = (int) (Math.random() * limit) + 1;
        String userName = "select fullname from people where seq=?;";
        String pay = "UPDATE people SET balance=balance-? WHERE seq=?;";
        String setGoodsTotal = "UPDATE goods SET g_store=g_store-1 WHERE g_name=?;";

        try {
            preparedStatement = connection.prepareStatement(userName);
            preparedStatement.setInt(1, user);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userName = resultSet.getString(1);
            }

            preparedStatement = connection.prepareStatement(pay);
            preparedStatement.setDouble(1, price);
            preparedStatement.setInt(2, user);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(setGoodsTotal);
            preparedStatement.setString(1, goodName);
            preparedStatement.executeUpdate();

            System.out.println(">> "+userName+" got something!");
            JDBCUtil.commitTransaction(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(">> "+userName+" got nothing...");
            JDBCUtil.rollBackTransaction(connection);
        } finally {
            JDBCUtil.release(resultSet, connection, preparedStatement);
        }

    }
}