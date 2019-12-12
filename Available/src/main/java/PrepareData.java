import java.sql.*;

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
                        "seq INT,\n" +
                        "id VARCHAR ( 20 ) PRIMARY KEY,\n" +
                        "fullname VARCHAR ( 30 ),\n" +
                        "sex VARCHAR ( 10 ),\n" +
                        "birthday date,\n" +
                        "country VARCHAR ( 100 ),\n" +
                        "addr VARCHAR ( 100 ),\n" +
                        "phone VARCHAR ( 50 ),\n" +
                        "university VARCHAR ( 50 ),\n" +
                        "company VARCHAR ( 50 ), \n" +
                        "unique index uni_seq(seq ASC)\n" +
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
        } finally {
            System.out.println(">> 创建表成功！");
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
        } finally {
            System.out.println(">> 创建索引成功！");
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
        } finally {
            System.out.println(">> 删除索引成功！");
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
            System.out.println(">> 当前已有记录数: " + count);
            JDBCUtil.release(resultSet, connection, preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static int getMaxSeq() {
        int seq = 0;
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "select max(seq) from available_test;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                seq = resultSet.getInt(1);
            }
            JDBCUtil.release(resultSet, connection, preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seq;
    }

    public static void getAvgBalance() {
        Double avg_balance = 0.00;
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "select avg(balance) from available_test;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                avg_balance = resultSet.getDouble(1);
            }
            System.out.println(">> 人均余额：" + avg_balance);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addColumn() {
        Connection connection = JDBCUtil.getConnection();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        String sql = null;
        String sql1 = null;
        String sql2 = null;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getColumns(null, null, "available_test", "balance");
            if (resultSet.next()) {
                sql = "update available_test set balance=10000.00;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.executeUpdate();
            } else {
                sql1 = "alter table available_test add column balance decimal(10,2) default 10000.00;";
                sql2 = "update available_test set balance=10000.00;";
                statement = connection.createStatement();
                statement.addBatch(sql1);
                statement.addBatch(sql2);
                statement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println(">> 列初始化成功！");
            JDBCUtil.release(resultSet, connection, preparedStatement);
        }
    }

    public static void deleteColumn() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "alter table available_test drop column balance;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println(">> 删除列成功！");
            JDBCUtil.release(null, connection, preparedStatement);
        }
    }

}
