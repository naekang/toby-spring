package springbook.sandbox;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;

import static org.assertj.core.api.Assertions.*;

public class SingletonTest {

    @Test
    public void testSingletonSpringContext(){
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        assertThat(context.getBean("userDao", UserDao.class)).isEqualTo(context.getBean("userDao", UserDao.class));
    }
}