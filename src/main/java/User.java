import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class User {

    private int id;
    private String name;
    private String username;
    private String email;
    private Address address;
    private String phone;
    private String website;
    private Company company;
}

@Data
@AllArgsConstructor
class Address {
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private Geo geo;
}

@Data
@AllArgsConstructor
class Geo {
    private String lat;
    private String lng;
}

@Data
@AllArgsConstructor
class Company {
    private String name;
    private String catchPhrase;
    private String bs;
}
