package bd;

import java.sql.Connection;

public class BaseDAO {
    protected Connection conn;
    protected ConexaoDAO conexaoDAO;

    public BaseDAO() {
        this.conexaoDAO = new ConexaoDAO();
    }
}
