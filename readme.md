# 1장 오브젝트와 의존관계
---

- 자바의 객체지향 프로그래밍 -> 스프링이 스프링에서 가장 가치를 두는 것

##### 오브젝트에 대한 관심
- 오브젝트간 관계, 사용, 소멸 전 과정
- 오브젝트의 설계 -> 객체지향 설계
    - 재활용 가능한 설계방법 = 디자인 패턴
    - 깔끔한 구조로 개선 = 리팩토링
    - 오브젝트가 정상적으로 동작하는지 = 단위 테스트
    
##### 스프링이 제공하는 것
- 오브젝트의 효과적인 설계, 구현, 사용 및 개선의 기준
- 객체지향 기술과 설계, 구현에 관한 실용적인 전략
- 검증된 베스트 프랙티스

> 오브젝트의 설계와 구현, 동작원리에 집중!


## 1.1 초난감 DAO
---
> DAO(Data Access Object): DB를 사용해 데이터를 조회하거나 조작하는 기능을 하게 만든 오브젝트

> 자바빈(JavaBean): 비주얼 툴에서 조작 가능한 컴포넌, 일반적으로 클래스를 의미
> , 디폴트 생성자 / 프로퍼티 

- JDBC를 이용하는 작업의 일반적인 순서
  1. DB연결을 위한 `connection`을 가져옴
  2. SQL을 담은 `Statement`를 만듦
  3. 만들어진 `Statement`를 실행
  4. 조회의 경우 SQL 쿼리의 실행 결과를 `ResultSet`으로 받아 정보를 저장할 `Object`에 옮겨줌
  5. 작업중에 생성된 `Connection`, `Statement`, `ResultSet`과 같은 리소스는 작업을 마친 후 반드시 닫아줌
  6. JDBC API가 만들어내는 예외를 잡아서 직접 처리하거나, 메소드에 `throws`를 선언해서 예외하 발생하면 메소드 밖으로 던지게 함
  
##### 이 클래스를 테스트 해볼 수 있는 방법은?
  - DAO의 기능을 사용하는 웹 어플리케이션을 만들어 서버에 올리고, 웹 브라우저를 통해 DAO 기능 사용해보기
  -> But 너무 부담이 큼
  - [main()을 이용한 DAO 테스트 코드 작성](src/main/java/springbook/user/dao/UserDaoTest.java)
    1. User 오브젝트 생성 & 프로퍼티에 값을 넣고 add() 메소드를 이용해 DB에 등록
    2. `Connection` 설정과 코드에 이상이 없으면 main() 메소드는 종료


## 1.2 DAO의 분리
- 변수나 오브젝트 필드의 값은 그대로지만 오브젝트에 대한 설계와 이를 구현한 코드가 변함 = 소프트웨어는 끊임없이 변함
- 미래를 어떻게 대비할 것인가를 항상 염두! -> `분리와 확장을 고려한 설계`
- 관심이 한 곳에 집중되도록 해야함 -> `관심사의 분리`

##### UserDao의 관심사항
- add() 매소드와 get() 메소드의 중복 코드를 확인하여 하나의 메소드로 추출
- DB 종류와 접속 방법이 바뀌었을 경우 getConnection() 메소드만 수정하면 됨

##### 변경사항에 대한 검증: 리팩토링과 테스트
- 중복된 코드를 추출하는 과정처럼 기능에는 영향이 없이 코드의 구조를 간결하게 변화하는 과정 = 리팩토링

##### DB 커넥션 만들기의 독립
- UserDao 소스코드를 제공하지 않고도 고객이 원하는 DB 커넥션 생성 방식을 적용할 수 있을까?
- 상속을 통한 확장
  - DB 커넥션 연결이라는 관심을 상속을 통해 서브클래스로 분리
  - UserDao: add(), get(), getConnection()
  - NUserDao: getConnection()
  - DUserDao: getConnection()
- 템플릿 메소드 패턴(Template Method Pattern): 슈퍼클래스의 기능을 확장할 때 사용하는 가장 대표적인 방법
  - 슈퍼클래스에 기본 로직흐름을 만들고, 서브클래스에서 각 메소드를 필요에 맞게 구현하는 방식
  - 전체적으로 동일하면서 부분적으로 다른 구문으로 구성된 메소드의 코드 중복을 최소화하기에 유용
- 팩토리 메소드 패턴(Factory Method Pattern)
  - 서브클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것


  
## 1.3 DAO의 확장
- 지금까지 데이터 엑세스 로직을 어떻게 만들 것인지, DB 연결을 어떤 방법으로 할 것인지 두 개의 관심을 기준으로 분리
- 두 관심은 변화의 성격이 다름

##### 다른 방식의 분리
- 본격적으로 독립시키며 손쉽게 확장할 수 있는 방식
- `SimpleConnectionMaker` 클래스를 만들고 UserDao가 이용할 수 있는 방식
  - `UserDao` 코드가 `SimpleConnectionMaker` 클래스에 종속되어 있으므로 UserDao 코드 수정 없이 DB 커넥션 변경할 방법이 없음
  - `SimpleConnectionMaker`의 `makeNewConnection`을 이름만 변경해도 `UserDao`를 변경해야함
  
##### 인터페이스의 도임
- 추상화란?
  - 어떤 것들의 공통적인 성격을 뽑아내어 `인터페이스`로 분리하는 작업

- 인터페이스
  - 기능만 정의
  - 어떻게 할지는 자신을 구현한 클래스가 담당
  - 자신을 구현한 클래스에 대한 구체적인 정보는 모두 감춤
  - 인터페이스를 사용하는 코드쪽에서는 추상화한 통로만 이해하면 됨
  
- 여전히 `UserDao`코드 내에 N사인지 D사 코드가 남아있음

##### 관계설정 책임의 분리
- 어떤 `ConnectionMaker` 구현 클래스를 사용할지 결정!
- 2개의 오브젝트 A, B 존재 A가 B 오브젝트의 기능 사용
  - B 오브젝트 = 서비스 오브젝트
  - A 오브젝트 = 클라이언트 오브젝트
- `UserDao`와 `ConnectionMaker` 구현 클래스 관계를 결정해주는 기능을 분리해서 두기에 가장 적절한 곳
  - `UserDao`의 클라이언트 오브젝트
  
##### 원칙과 패턴
- `개방 폐쇄 원칙(OCP, Open-Closed Priciple)`
  - 클래스나 모듈은 확장에는 열려있고 변경에는 닫혀있어야 함
  - `UserDao`는 DB 연결 방법 기능 확장에 열려있지만 자신의 코드는 그런 변화에 영향을 받지않고 유지 가능
- [SOLID 5원칙](https://naekang.tistory.com/151)
- 높은 응집도와 낮은 결합도
  - 높은 응집도: 한 모듈 내부 처리 요소들 간 기능적 연관도
  - 낮은 결합도: 모듈간의 상호 의존도 및 연관 관계
- 전략 패턴(Strategy Pattern)
  - 자신의 기능 맥락에서, 필요에 따라 변경한 알고리즘을 인터페이스를 통해 통째로 외부로 분리시키고, 이름 구현한 구체적인 알고리즘 클래스를 필요에 따라 바꿔서 사용할 수 있게 하는 디자인 패턴
  
## 1.4 제어의 역전(IoC)

##### 오브젝트 팩토리
- `UserDaoTest`
  - 기능이 잘 동작하는지 테스트 하는 책임
  - 기존 `UserDao`가 직접 담당하던 기능
  
- 팩토리(Factory)
  - 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 
  - `UserDao`와 `ConnectionMaker`는 각각 애플리케이션의 핵심적인 데이터 로직과 기술 로직 담당
  - `DaoFactory`는 이런 애플리케이션의 오브젝트들을 구성하고 관계를 정의하는 책임을 맡고 있음
  - DAO 여러개일 경우
    ```java
    public UserDao DaoFactory {
        public UserDao userDao() {
            return new UserDao(new DConnectionMaker());
        }
        public AccountDao accountDao() {
            return new AccountDao(new DConnectionMaker());
        }
        public MessageDao messageDao() {
            return new MessageDao(new DConnectionMaker());
        }
    }
    ```
  - `new DConnectionMaker()`의 중복
    ```java
    public UserDao DaoFactory {
        public UserDao userDao() {
            return new UserDao(connectionMaker());
        }
        public AccountDao accountDao() {
            return new AccountDao(connectionMaker());
        }
        public MessageDao messageDao() {
            return new MessageDao(connectionMaker());
        }
        public ConnectionMaker connectionMaker() {
            return new DConnectionMaker();
        }
    }
    ```
    
##### 제어권 이전을 통한 제어관계 역전
- 제어 흐름의 구조가 뒤바뀌는 것
- 일반적
  - main() 메소드 시작지점 -> 오브젝트 생성 -> 관계 설정 -> 호출
- 제어의 역전
  - 모든 제어권한을 자신이 아닌 다른 대상에 위임
  - 모든 오브젝트가 위임받은 제어 권한을 갖는 특별한 오브젝트에 의해 결정되고 만들어짐
- [프레임워크 vs 라이브러리](https://naekang.tistory.com/154)


## 1.5 스프링의 IoC
- `Bean(빈)`
  - 스프링이 IoC 방식으로 관리하는 오브젝트
  - 스프링이 직접 제어권을 갖고 생성과 제어를 담당하는 오브젝트

- `Bean Factory(빈 팩토리)`
  - 스프링의 IoC를 담당하는 핵심 컨테이너
  - 빈의 등록, 생성, 조회, 그 외 부가적인 빈을 관리하는 기능
  
- `Application Context(애플리케이션 컨텍스트)`
  - 빈 팩토리를 확장한 IoC 컨테이너
  - 스프링이 제공하는 각종 부가 서비스 추가
  - 애플리케이션 컨텍스트가 구현해야 하는 기본 인터페이스 지칭
  
##### 애플리케이션 컨텍스트
- DaoFactory에 대응되는 것이 스프링의 ApplicationContext
- ApplicationContext 장점
  - 클라이언트가 구체적인 팩토리 클래스를 알 필요 없음
  - 종합 IoC서비스 제공
  - 빈을 검색하는 다양한 방법 제공
    - `getBean()`
  
##### 스프링 IoC의 용어 정리
- 빈(Bean)
  - 스프링이 IoC 방식으로 관리하는 오브젝트
  - 스프링이 직접 생성과 제어를 담당하는 오브젝트

- 빈 팩토리(Bean Factory)
  - 스프링의 IoC를 담당하는 핵심 컨테이너
  - 빈을 등록하고, 생성하고, 조회하고 돌려주고, 그 외 부가적인 빈을 관리
  - `BeanFactory`: 빈 팩토리가 구현하고 있는 가장 기본적인 인터페이스의 이름
  
- 애플리케이션 컨텍스트(Application Context)
  - 빈 팩토리를 확장한 IoC 컨테이너
  - `ApplicationContext`: 애플리케이션 컨텍스트가 구현해야 하는 기본 인터페이스
  
- 설정정보 / 설정 메타정보(Configuration Metadata)
  - 애플리케이션 컨텍스트 또는 빈 팩토리가 IoC를 적용하기 위해 사용하는 메타정보
  
- 컨테이너(Container)
  - IoC 방식으로 빈을 관리한다는 의미에서 컨테이너 또는 IoC 컨테이너라고 함
  
- 스프링 프레임워크(Spring Framework)
  - 스프링이 제공하는 모든 기능을 통틀어 말함

## 1.6 싱글톤 레지스트리와 오브젝트 스코프
```
**오브젝트의 동일성과 동등성(Java)**
- 동일성은 == 연산자
- 동등성은 equals() 메소드
- 동일성은 결국 하나의 오브젝트만 존재하나 두개의 오브젝트 레퍼런스 변수만 가지고 있는 상태
- 동등성은 두 개의 다른 오브젝트가 메모리상에 존재하지만 로직상의 정의에 따라 오브젝트 정보가 같다고 판단
```

- Singleton Registry(싱글톤 레지스트리)
  - 기본적으로 별다른 설정이 없으면 내부에서 생성하는 빈 오브젝트를 모두 싱글톤으로 만듦
  
- 서버 애플리케이션과 싱글톤
  - 스프링은 하나의 요청을 처리하기 위해 계층형 구조로 이루어짐
  - 서비스 오브젝트 개념 도입
  - 싱글톤 패턴(Singleton Pattern)
    - 어떤 클래스를 애플리케이션 내에서 제한된 인스턴스 개수, 이름처럼 주로 하나만 존재하도록 강제하는 패턴
  
- 싱글톤 패턴의 한계
  - private 생성자를 갖고 있기 때문에 상속할 수 없음
  - 싱글톤은 테스트가 힘듦
  - 서버환경에서는 싱글톤이 하나만 만들어지는 것을 보장하지 못함
  - 싱글톤의 사용은 전역 상태를 만들 수 있기 때문에 바람직하지 못함
  
- 싱글톤 레지스트리 
  - 스프링 컨테이너는 싱글톤을 생성하고, 관리하고, 공급하는 싱글톤 관리 컨테이너
  - private 생성자 불필요
  - 상속 가능
  - 평범한 자바 클래스로 싱글톤을 만듦으로 테스트 용이
  - 애플리케이션 컨텍스트 내에서 하나만 만들어지는 것이 보장됨
  - 싱글톤은 전역상태이므로 bean에 상태값을 갖지 않도록 유의
  
##### 싱글톤과 오브젝트의 상태
- 싱글톤이 멀티스레드 환경에서 서비스 형태의 오브젝트로 사용되는 경우 stateless(무상태) 방식으로 만들어져야 함
- 무상태 방식에서 각 요청에 대한 정보나, DB, 서버 리소스로부터 생성한 정보를 다루는 방법
  - 파라미터
  - 메서드 안 생성되는 로컬 변수
  - 리턴값
  
##### 스프링 빈의 스코프
- 빈의 스코프(scope): 빈이 생성되고, 존재하고, 적용되는 범위
- 스코프 종류
  - `prototype`: 컨테이너에 빈을 요청할 때마다 매번 새로운 오브젝트를 만들어줌
  - `request`: 웹을 통해 새로운 HTTP 요청이 생길 때마다 생성
  - `session`: 웹의 세션과 스코프가 유사

## 1.7 의존관계 주입(DI)

>DI(Dependency Injection): 오브젝트 레퍼런스를 외부로부터 주입받고 이를 통해 오브젝트와 다이내믹하게 의존관계가 만들어지는 것

- DaoFactory처럼 객체를 생성하고 관계를 맺어주는 등의 작업을 담당하는 기능을 일반화한 것이 스프링의 IoC 컨테이너
- `의존관계 주입`
  - 일어나는 방법에 초점을 맞춘 것
  
##### 런타임 의존관계 설정
- `의존관계`
  - 누가 누구에게 의존하는 관계
  - 의존관계는 방향성이 존재함
  
- `UserDao`의 의존관계
  - `UserDao`가 `ConnectionMaker`에 의존하는 형태
  - `ConnectionMaker`의 변화는 `UserDao`에도 영향을 줌
  - `ConnectionMaker` 인터페이스를 구현한 클래스의 변화는 `UserDao`에 영향을 주지 않음
  - 인터페이스에 대해서만 의존관계 형성 - 서로의 관계를 느슨하게 = 낮은 결합도
  - 의존 오브젝트(Dependent Object): 실제 사용대상 ex) NConnectionMaker, DConnectionMaker
  - 의존관계 주입의 세가지 조건
    - 클래스 모델이나 코드에는 런타임 시점의 의존관계가 드러나지 않음. 그러기 위해서는 인터페이스에만 의존하고 있어야 함
    - 런타임 시점의 의존관계는 컨테이너나 팩토리 같은 제3의 존재가 결정
    - 의존관계는 사용할 오브젝트에 대한 레퍼펀스를 외부에서 주입해줌으로써 만들어
  
##### 의존관계 검색과 주입
- `Dependency Lookup(의존관계 검색)`
  - 외부로부터 주입이 아닌 스스로 검색
  - 자신이 필요로 하는 의존 오브젝트를 능동적으로 찾음
  - 자신이 어떤 클래스의 오브젝트를 이용할지 결정하지 않음
  - 런타임 시 의존관계를 맺을 오브젝트를 결정하는 것과 오브젝트의 생성 작업은 외부 컨테이너에 IoC로 맡김

- DaoFactory를 이용하는 생성자
  ```java
  public UserDao() {
    DaoFactory daoFactory = new DaoFactory();
    this.connectionMaker = daoFactory.connectionMaker();
  }
  ```
  
- 의존관계 검색을 이용하는 UserDao 생성자
  ```java
  public UserDao() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplication(DaoFactory.class);
    this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
  }
  ```
  
- 의존관계 검색 vs 의존관계 주입
  - 의존관계 검색에서는 검색하는 오브젝트는 자신이 스프링 빈일 필요가 없음
  - 의존관계 주입에서는 UserDao와 ConnectionMaker 사이에 DI가 적용되려면 UserDao도 반드시 컨테이너가 만드는 빈 오브젝트여야함
  
##### 의존관계 주입의 응용
- 기능 구현의 교환
  - DI의 설정정보에 해당하는 DaoFactory만 다르게 만들어두면 개발, 운영 시에 각각 다른 런타임 오브젝트에 의존관계를 갖게함
  
- 부가 기능 추가
  - DAO가 DB를 얼마나 많이 사용하는지 확인하는 방법
    - DAO와 DB커넥션을 만드는 오브젝트 사이에 연결횟수를 카운팅하는 오브젝트 하나 더 추가