package hello.core.scope;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonWithPrototypeTest {

    @Test
    @DisplayName("싱글톤 빈과 프로토타입 빈을 함께 사용시 나타나는 문제점")
    void singletonClientUsingPrototype1() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(2);
    }

    @Scope("singleton")
    static class ClientBean {

        private final PrototypeBean prototypeBean;

        @Autowired
        public ClientBean(PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }

        public int logic() {
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Test
    @DisplayName("스프링 ObjectProvider를 사용한 해결 방법")
    void singletonClientUsingPrototype2() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBeanSpring.class, PrototypeBean.class);

        ClientBeanSpring clientBean1 = ac.getBean(ClientBeanSpring.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBeanSpring clientBeanSpring = ac.getBean(ClientBeanSpring.class);
        int count2 = clientBeanSpring.logic();
        assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    static class ClientBeanSpring {

        @Autowired
        private ObjectProvider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
            PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Test
    @DisplayName("자바 Provider를 사용한 해결 방법")
    void singletonClientUsingPrototype3() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBeanJava.class, PrototypeBean.class);

        ClientBeanJava clientBean1 = ac.getBean(ClientBeanJava.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBeanJava ClientBeanJava = ac.getBean(ClientBeanJava.class);
        int count2 = ClientBeanJava.logic();
        assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    static class ClientBeanJava {

        @Autowired
        private Provider<PrototypeBean> provider;

        public int logic() {
            PrototypeBean prototypeBean = provider.get();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean {

        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }
    }
}
