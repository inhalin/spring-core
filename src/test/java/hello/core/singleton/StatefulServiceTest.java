package hello.core.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.*;

class StatefulServiceTest {

    ApplicationContext ac;
    StatefulService statefulService1;
    StatefulService statefulService2;

    @BeforeEach
    void beforeEach() {
        ac = new AnnotationConfigApplicationContext(TestConfig.class);
        statefulService1 = ac.getBean(StatefulService.class);
        statefulService2 = ac.getBean(StatefulService.class);
    }

    @Test
    void statefulServiceSingleton() {
        // ThreadA: userA 10000원 주문
        statefulService1.order("userA", 10000);
        // ThreadB: userB 20000원 주문
        statefulService2.order("userB", 20000);

        int price = statefulService1.getPrice();
        System.out.println("price = " + price);

        // 실패하는 테스트
        assertThat(statefulService1.getPrice()).isNotEqualTo(10000);
    }

    @Test
    void statelessServiceSingleton() {
        int userAPrice = statefulService1.orderFix("userA", 10000);
        int userBPrice = statefulService2.orderFix("userB", 20000);

        assertThat(userAPrice).isEqualTo(10000);
        assertThat(userBPrice).isEqualTo(20000);
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }

}