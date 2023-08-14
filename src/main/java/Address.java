import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Address {
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private Geo geo;
}
