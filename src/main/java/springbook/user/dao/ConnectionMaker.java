package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

// 인터페이스을 통해 DB Connection 관심사 분리 및 추상화
public interface ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
