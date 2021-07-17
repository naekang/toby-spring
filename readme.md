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
  - 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 ₩
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
