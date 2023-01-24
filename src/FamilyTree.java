import java.io.Serializable;

public class FamilyTree implements Serializable {
    private static final long serialVersionUID = 1;

    public FamilyTree() {
        this.root = null;
    }

    private FamilyMember root;

    public void setRoot(FamilyMember newRoot) {
        this.root = newRoot;
    }

    public boolean hasRoot() {
        return this.root != null;
    }

    public FamilyMember getRoot() {
        return this.root;
    }
}
