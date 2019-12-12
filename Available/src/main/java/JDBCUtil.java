import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static sun.plugin.javascript.navig.JSType.URL;

/*
 *  获取和释放 jdbc 连接、开启和回滚事务的 jdbc 工具类
 * */
public class JDBCUtil {

//    private static final String driver = "com.mysql.jdbc.Driver";
//    private static String URL = "jdbc:mysql://192.168.1.201:3306/poctest?useSSL=false";
//    private static String user = "root";
//    private static String password = "root";

    private static Properties properties = new Properties();
    private static DataSource dataSource;
    private static String propertiesPath=Do.propertiesPath;

    //加载DBCP配置文件
    static {
        try {
            FileInputStream is = new FileInputStream(propertiesPath);
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    获取 jdbc 连接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //    释放 jdbc 连接、结果集、语句等
    public static void release(ResultSet resultSet, Connection connection, PreparedStatement preparedStatement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //    开始事务
    public static void beginTransaction(Connection cnn) {
        if (cnn != null) {
            try {
                if (cnn.getAutoCommit()) {
                    cnn.setAutoCommit(false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //    提交事务
    public static void commitTransaction(Connection cnn) {
        if (cnn != null) {
            try {
                if (!cnn.getAutoCommit()) {
                    cnn.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //     回滚事务
    public static void rollBackTransaction(Connection cnn) {
        if (cnn != null) {
            try {
                if (!cnn.getAutoCommit()) {
                    cnn.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
