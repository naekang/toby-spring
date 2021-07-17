package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setId("naekang1");
        user.setName("김진호");
        user.setPassword("haul1!");

        NConnectionMaker connectionMaker = new NConnectionMaker();
        UserDao userDao = new UserDao(connectionMaker);
        userDao.add(user);

        System.out.println(user.getId() + "등록 성공");

        User user2 = userDao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + "조회 성공");
    }
}
