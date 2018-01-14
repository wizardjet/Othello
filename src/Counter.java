import java.io.Serializable;

public abstract class Counter implements Serializable{

    protected String description;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
