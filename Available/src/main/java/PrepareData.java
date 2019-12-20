import java.sql.*;

public class PrepareData {

    public static void prepareTable() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement drop_table = null;
        PreparedStatement pre_table = null;
        String sql_dropTable = "DROP TABLE\n" +
                "IF\n" +
                "\tEXISTS people;\n";
        String sql_createTable =
                "CREATE TABLE people (\n" +
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
                        "index index_seq(seq ASC)\n" +
                        ");";
        try {
            drop_table = connection.prepareStatement(sql_dropTable);
            drop_table.executeUpdate();
            pre_table = connection.prepareStatement(sql_createTable);
            pre_table.executeUpdate();
            System.out.println(">> 创建表成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(null, null, drop_table);
            JDBCUtil.release(null, connection, pre_table);
        }
    }

    public static void createIndex() {
        Connection connection = JDBCUtil.getConnection();
        String sql1 = "create index idx_birth on people(birthday);";
        String sql2 = "create index idx_country on people(country);";
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        try {
            preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            System.out.println(">> 创建索引成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(null, null, preparedStatement1);
            JDBCUtil.release(null, connection, preparedStatement2);
        }
    }

    public static void dropIndex() {
        Connection connection = JDBCUtil.getConnection();
        String sql1 = "drop index idx_birth on people;";
        String sql2 = "drop index idx_country on people;";
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        try {
            preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            System.out.println(">> 删除索引成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(null, null, preparedStatement1);
            JDBCUtil.release(null, connection, preparedStatement2);
        }
    }


    public static int getCount() {
        int count = 0;
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "select count(1) from people;";
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            System.out.println(">> 当前已有记录数: " + count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(resultSet, connection, preparedStatement);
        }
        return count;
    }

    public static int getMaxSeq() {
        int seq = 0;
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "select max(seq) from people;";
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                seq = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(resultSet, connection, preparedStatement);
        }
        return seq;
    }

    public static void getAvgBalance() {
        Double avg_balance = 0.00;
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "select avg(balance) from people;";
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                avg_balance = resultSet.getDouble(1);
            }
            System.out.println(">> 人均余额：" + avg_balance);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(resultSet, connection, preparedStatement);
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
            resultSet = metaData.getColumns(null, null, "people", "balance");
            if (resultSet.next()) {
                sql = "update people set balance=10000.00;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.executeUpdate();
            } else {
                sql1 = "alter table people add column balance decimal(10,2) default 10000.00;";
                sql2 = "update people set balance=10000.00;";
                statement = connection.createStatement();
                statement.addBatch(sql1);
                statement.addBatch(sql2);
                statement.executeBatch();
            }
            System.out.println(">> 列初始化成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(resultSet, connection, preparedStatement);
        }
    }

    public static void deleteColumn() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "alter table people drop column balance;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            System.out.println(">> 删除列成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(null, connection, preparedStatement);
        }
    }

    public static ResultSet getGlobalStatus(){
        ResultSet resultSet=null;
        Connection connection=JDBCUtil.getConnection();
        PreparedStatement preparedStatement=null;
        String sql="SHOW GLOBAL STATUS \n" +
                "WHERE\n" +
                "\tvariable_name IN ( 'questions', 'com_commit', 'com_rollback', 'com_select', 'com_insert', 'com_delete', 'com_update', 'uptime' );";
        try {
            preparedStatement=connection.prepareStatement(sql);
            resultSet=preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static void prepareGoods(){
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql_dropTable = "DROP TABLE\n" +
                "IF\n" +
                "\tEXISTS goods;";
        String sql_createTable ="CREATE TABLE goods ( g_name VARCHAR ( 20 ) PRIMARY KEY, g_store INT UNSIGNED, g_price DECIMAL ( 10, 2 ) );";
        String sql_insert="INSERT INTO goods\n" +
                "VALUES\n" +
                "\t( 'something', 100, 99.98 );";
        try {
            preparedStatement = connection.prepareStatement(sql_dropTable);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(sql_createTable);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(sql_insert);
            preparedStatement.executeUpdate();
            System.out.println(">> 商品表初始化成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.release(null, connection, preparedStatement);
        }
    }

}
