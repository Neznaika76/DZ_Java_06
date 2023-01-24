import java.io.Serializable;
import java.util.ArrayList;


public class FamilyMember implements Serializable {

    @Override
    public String toString() {
        String s = null;
        if (this.gender == Gender.МУЖСКОЙ) {
            s = "♂ ";
        }
        else if (this.gender == Gender.ЖЕНСКИЙ) {
            s = "♀ ";
        }
        s += this.getFirstName() + " " + this.getLastName();
        if (this.has(Attribute.ДЕВИЧЬЯ_ФАМИЛИЯ)) {
            s += " (" + this.getMaidenName() + ")";
        }
        return s;
    }

    public FamilyMember(String firstName, String lastName, Gender gender, Address address, String lifeDescription) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.maidenName = "";
        this.setGender(gender);
        this.setAddress(address);
        this.setLifeDescription(lifeDescription);

        this.mother = null;
        this.father = null;
        this.spouse = null;
        this.children = new ArrayList<>();
    }

    private String firstName;
    private String lastName;
    private String maidenName;
    private Gender gender;
    private Address address;
    private String lifeDescription;
    private final String nameRegex = "^[\\p{L} .'-]+$";

    private FamilyMember mother;
    private FamilyMember father;
    private FamilyMember spouse;
    private ArrayList<FamilyMember> children;

    public enum Attribute {
        ОТЕЦ,
        МАТЬ,
        ДЕТИ,
        СУПРУГ,
        ДЕВИЧЬЯ_ФАМИЛИЯ,
        РОДИТЕЛИ,
    }

    public enum RelativeType {
        ОТЕЦ,
        МАТЬ,
        РЕБЁНОК,
        СУПРУГ,
    }

    public enum Gender {
        МУЖСКОЙ,
        ЖЕНСКИЙ,
    }

    public String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        if (firstName.trim().matches(nameRegex)) {
            this.firstName = firstName.trim();
        }
        else {
            throw new IllegalArgumentException("Недопустимое имя");
        }

    }

    public String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        if (lastName.trim().matches(nameRegex)) {
            this.lastName = lastName.trim();
        }
        else {
            throw new IllegalArgumentException("Недопустимая фамилия");
        }
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        if (maidenName.trim().matches(nameRegex)) {
            if (this.gender == Gender.ЖЕНСКИЙ) {
                this.maidenName = maidenName.trim();
            }
            else {
                throw new IllegalArgumentException("Девичьи фамилии только для женщин");
            }

        }
        else if (maidenName.isEmpty()) {
            this.maidenName = "";
        }
        else {
            throw new IllegalArgumentException("Недопустимая девичья фамилия");
        }
    }

    public Gender getGender() {
        return gender;
    }

    public final void setGender(Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public final void setAddress(Address address) {
        this.address = address;
    }

    public String getLifeDescription() {
        return lifeDescription;
    }

    public final void setLifeDescription(String lifeDescription) {
        this.lifeDescription = lifeDescription;

    }

    public void addChild(FamilyMember child) {

        if (this.gender == Gender.МУЖСКОЙ) {
            if (!child.has(Attribute.ОТЕЦ)) {
                child.setFather(this);
            }
            if (this.has(Attribute.СУПРУГ)) {
                if (!child.has(Attribute.МАТЬ)) {
                    child.setMother(this.getSpouse());
                }
            }
        }
        else if (this.gender == Gender.ЖЕНСКИЙ) {
            if (!child.has(Attribute.МАТЬ)) {
                child.setMother(this);
            }
            if (this.has(Attribute.СУПРУГ)) {
                if (!child.has(Attribute.ОТЕЦ)) {
                    child.setFather(this.getSpouse());
                }
            }
        }
        if (!this.getChildren().contains(child)) {
            this.getChildren().add(child);
        }
    }

    public int numChildren() {
        return this.getChildren().size();
    }

    public FamilyMember getMother() {
        return mother;
    }

    public void setMother(FamilyMember mother) {
        if (!this.has(Attribute.МАТЬ)) {
            if (mother.getGender() == Gender.ЖЕНСКИЙ) {
                if (!mother.getChildren().contains(this)) {
                    mother.getChildren().add(this);
                }
                this.mother = mother;


            }
            else {
                throw new IllegalArgumentException("Мать может быть только женского пола");
            }

        }
        else {
            throw new IllegalArgumentException("Мать уже добавлена");
        }

    }

    public FamilyMember getFather() {
        return father;
    }

    public void setFather(FamilyMember father) {
        if (!this.has(Attribute.ОТЕЦ)) {
            if (father.getGender() == Gender.МУЖСКОЙ) {
                if (!father.getChildren().contains(this)) {
                    father.getChildren().add(this);
                }
                this.father = father;

            }
            else {
                throw new IllegalArgumentException("Отец может быть только мужчиной");
            }

        }
        else {
            throw new IllegalArgumentException("Отец уже добавлен");
        }

    }

    public FamilyMember getSpouse() {
        return spouse;
    }

    public void setSpouse(FamilyMember spouse) {
        if (!this.has(Attribute.СУПРУГ)) {
            if (spouse.getGender() != this.getGender()) {
                spouse.setChildren(this.getChildren());
                this.spouse = spouse;
                if (!this.getSpouse().has(Attribute.СУПРУГ)) {
                    spouse.setSpouse(this);
                }

            }
            else {
                throw new IllegalArgumentException("Супруг может быть только противоположного пола");
            }
        }
        else {
            throw new IllegalArgumentException("Супруг уже существует");
        }
    }

    public ArrayList<FamilyMember> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<FamilyMember> children) {
        this.children = children;
    }

    public boolean has(FamilyMember.Attribute type) {
        switch (type) {
            case ОТЕЦ:
                return this.getFather() != null;
            case ДЕТИ:
                return !this.getChildren().isEmpty();
            case МАТЬ:
                return this.getMother() != null;
            case СУПРУГ:
                return this.getSpouse() != null;
            case ДЕВИЧЬЯ_ФАМИЛИЯ:
                return !this.getMaidenName().isEmpty();
            case РОДИТЕЛИ:
                return this.has(Attribute.ОТЕЦ) || this.has(Attribute.МАТЬ);
        }
        return false;
    }

    public void addRelative(FamilyMember.RelativeType type, FamilyMember member) {
        switch (type) {
            case ОТЕЦ:
                this.setFather(member);
                return;
            case РЕБЁНОК:
                this.addChild(member);
                return;
            case МАТЬ:
                this.setMother(member);
                return;
            case СУПРУГ:
                this.setSpouse(member);
                return;
        }
    }
}