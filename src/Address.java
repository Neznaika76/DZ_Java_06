import java.io.Serializable;

public class Address implements Serializable {

    @Override
    public String toString() {
        return streetNumber + " " + streetName + ", " + suburb + ", " + postCode;
    }

    public Address(String streetNumber, String streetName, String Suburb, String postCode) {
        this.setStreetNumber(streetNumber);
        this.setStreetName(streetName);
        this.setSuburb(Suburb);
        this.setPostCode(postCode);
    }

    private String streetNumber;
    private String streetName;
    private String suburb;
    private String postCode;

    public String getStreetNumber() { // вернуть the streetNumber
        return streetNumber;
    }

    public final void setStreetNumber(String streetNumber) {
        if (streetNumber.trim().matches("^[a-zA-Zа-яА-ЯёЁ0-9'/№#-_ ]+$") && streetNumber.trim().matches(".*\\d.*")) { // if (streetNumber.trim().matches("^[\\d]+$") && streetNumber.trim().matches(".*\\d.*")) {
            this.streetNumber = streetNumber.trim();
        }
        else {
            throw new IllegalArgumentException("Недопустимый номер дома.");
        }

    }

    public String getStreetName() {
        return streetName;
    }

    public final void setStreetName(String streetName) {
        if (streetName.trim().matches("^[a-zA-Zа-яА-ЯёЁ0-9'/№#-_ ]+$")) {
            this.streetName = streetName.trim();
        }
        else {
            throw new IllegalArgumentException("Недопустимое название улицы.");
        }
    }

    public String getSuburb() {
        return suburb;
    }

    public final void setSuburb(String suburb) {
        if (suburb.trim().matches("^[a-zA-Zа-яА-ЯёЁ0-9'/№#-_ ]+$")) {
            this.suburb = suburb.trim();
        }
        else {
            throw new IllegalArgumentException("Недопустимое название населённого пункта.");
        }
    }

    public String getPostCode() {
        return postCode;
    }

    public final void setPostCode(String postCode) {
        if (postCode.trim().matches("^[a-zA-Zа-яА-ЯёЁ0-9'/№#-_ ]+$")) { //("\\d{4}")
            this.postCode = postCode.trim();
        }
        else {
            throw new IllegalArgumentException("Почтовый индекс должен быть положительным числовым значением и содержать более 4 цифр");
        }
    }
}