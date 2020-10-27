package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Description:
 * <p>
 * User: 丰杰
 * Date: 2020-10-01
 * Time: 14:15
 */
public class JdbcTest {

    public static void main(String[] args) throws SQLException {

        ConnectionPoolUtil pool = ConnectionPoolUtil.getInstance();
        pool.initPool();

        Connection connection = pool.getConnection();
        String sql = "select * from paper_program.class";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()){
            System.out.println(resultSet.getString(1));
        }

    }

}
