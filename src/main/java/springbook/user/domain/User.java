package springbook.user.domain;

public class User {
    String Id;
    String name;
    String password;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Id, name, password를 선언한 후 (cmd + N) 단축키를 이용해 getter 및 setter 생성

}
