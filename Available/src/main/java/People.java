import com.github.javafaker.Faker;

import java.util.Date;

public class People {
    String id;
    String name;
    int sex;
    Date birthday;
    String country;
    String addr;
    String phone;
    String university;
    String company;

    public People(Faker faker) {
        this.id = faker.idNumber().invalid();
        this.name = faker.name().fullName();
        this.sex = faker.random().nextInt(0, 1);
        this.birthday = faker.date().birthday();
        this.country = faker.country().name();
        this.addr = faker.address().fullAddress();
        this.phone = faker.phoneNumber().phoneNumber();
        this.university = faker.university().name();
        this.company = faker.company().name();
    }
}
