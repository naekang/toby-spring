package springbook.user.dao;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

class UserDaoTest {

    @Test
    public void main() throws SQLException {
        ApplicationContext ac = new GenericXmlApplicationContext("/applicationContext.xml");
        UserDao userDao = ac.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("nakenag");
        user.setName("김진호");
        user.setPassword("haul1!");
        userDao.add(user);

        User user2 = userDao.get(user.getId());

        assertThat(user2.getName()).isEqualTo(user.getName());
        assertThat(user2.getPassword()).isEqualTo(user.getPassword());
    }

}