import com.github.javafaker.Faker;

import java.sql.*;
import java.text.SimpleDateFormat;

public class PrepareData {

    public static void prepareTable() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement drop_table = null;
        PreparedStatement pre_table = null;
        String sql_dropTable = "DROP TABLE\n" +
                "IF\n" +
                "\tEXISTS available_test;\n";
        String sql_createTable =
                "CREATE TABLE available_test (\n" +
                        "id VARCHAR ( 20 ) PRIMARY KEY,\n" +
                        "fullname VARCHAR ( 30 ),\n" +
                        "sex VARCHAR ( 10 ),\n" +
                        "birthday date,\n" +
                        "country VARCHAR ( 100 ),\n" +
                        "addr VARCHAR ( 100 ),\n" +
                        "phone VARCHAR ( 50 ),\n" +
                        "university VARCHAR ( 50 ),\n" +
                        "company VARCHAR ( 50 ) \n" +
                        ");";
        try {
            drop_table = connection.prepareStatement(sql_dropTable);
            drop_table.executeUpdate();
            pre_table = connection.prepareStatement(sql_createTable);
            pre_table.executeUpdate();
            JDBCUtil.release(null, null, drop_table);
            JDBCUtil.release(null, connection, pre_table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createIndex() {
        Connection connection = JDBCUtil.getConnection();
        String sql1 = "create index idx_birth on available_test(birthday);";
        String sql2 = "create index idx_country on available_test(country);";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            JDBCUtil.release(null, null, preparedStatement1);
            JDBCUtil.release(null, connection, preparedStatement2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropIndex() {
        Connection connection = JDBCUtil.getConnection();
        String sql1 = "drop index idx_birth on available_test;";
        String sql2 = "drop index idx_country on available_test;";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            JDBCUtil.release(null, null, preparedStatement1);
            JDBCUtil.release(null, connection, preparedStatement2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static int getCount() {
        int count = 0;
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "select count(1) from available_test;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            System.out.println("当前已有记录数:" + count);
            JDBCUtil.release(resultSet, connection, preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("获取当前记录数失败！");
        }finally {

        }
        return count;
    }

}
