import com.github.javafaker.Faker;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class InsertThread extends Thread {

    private CountDownLatch countDownLatch;
    private Semaphore semaphore;

    public InsertThread(Semaphore semaphore, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            //获取许可
            semaphore.acquire();
            insertRecord(new People(new Faker()));
            //访问完后，释放
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 倒数器减1
            countDownLatch.countDown();
        }

    }

    public void insertRecord(People people) throws InterruptedException {
        int exeFlag = 0;
        String sexName = null;
        if (people.sex == 0) {
            sexName = "female";
        } else {
            sexName = "male";
        }
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO available_test ( id, fullname, sex, birthday, country, addr, phone, university, company )\n" +
                "VALUES\n" +
                "\t( ?, ?, ?, ?, ?, ?, ?, ?, ? );";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, people.id);
            preparedStatement.setString(2, people.name);
            preparedStatement.setString(3, sexName);
            preparedStatement.setDate(4, Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(people.birthday)));
            preparedStatement.setString(5, people.country);
            preparedStatement.setString(6, people.addr);
            preparedStatement.setString(7, people.phone);
            preparedStatement.setString(8, people.university);
            preparedStatement.setString(9, people.company);
            exeFlag = preparedStatement.executeUpdate();
            if (exeFlag == 1) {
                System.out.println("id:" + people.id + " 插入成功！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("id:" + people.id + " 插入失败,重新插入……");
            JDBCUtil.release(null, connection, preparedStatement);
            insertRecord(new People(new Faker()));
        } finally {
            JDBCUtil.release(null, connection, preparedStatement);
        }
    }
}
