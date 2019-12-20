import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class TransferThread extends Thread {

    private CountDownLatch countDownLatch;
    private Semaphore semaphore;

    public TransferThread(Semaphore semaphore, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            //获取许可
            semaphore.acquire();
            trandfer();
            //访问完后，释放
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 倒数器减1
            countDownLatch.countDown();
        }
    }

    private void trandfer() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String count = "select count(1) from people;";
        ResultSet resultSet = null;
        int limit = 0;
        try {
            preparedStatement = connection.prepareStatement(count);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                limit = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JDBCUtil.beginTransaction(connection);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String amount_str = decimalFormat.format(new Random().nextDouble() * 10000.00);
        Double transfer_amount = Double.valueOf(amount_str);

        int user1 = (int) (Math.random() * limit) + 1;
        String userName1="select fullname from people where seq=?;";
        String pay = "UPDATE people SET balance=balance-? WHERE seq=?;";

        int user2 = (int) (Math.random() * limit) + 1;
        while (user1 == user2) {
            user2 = (int) (Math.random() * limit) + 1;
        }
        String userName2="select fullname from people where seq=?;";
        String receive = "UPDATE people SET balance=balance+? WHERE seq=?;";

        try {
            preparedStatement=connection.prepareStatement(userName1);
            preparedStatement.setInt(1,user1);
            resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                userName1=resultSet.getString(1);
            }

            preparedStatement=connection.prepareStatement(userName2);
            preparedStatement.setInt(1,user2);
            resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                userName2=resultSet.getString(1);
            }

            preparedStatement = connection.prepareStatement(pay);
            preparedStatement.setDouble(1, transfer_amount);
            preparedStatement.setInt(2, user1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(receive);
            preparedStatement.setDouble(1, transfer_amount);
            preparedStatement.setInt(2, user2);
            preparedStatement.executeUpdate();
            JDBCUtil.commitTransaction(connection);
            System.out.println(">> "+userName1+" 成功向 "+userName2+" 转账："+transfer_amount+" 元");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(">> "+userName1+" 向 "+userName2+" 转账失败，事务回滚...");
            JDBCUtil.rollBackTransaction(connection);
        } finally {
            JDBCUtil.release(resultSet, connection, preparedStatement);
        }

    }
}
