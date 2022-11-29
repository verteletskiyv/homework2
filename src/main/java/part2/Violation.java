package part2;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

@JsonAutoDetect
public class Violation {
    @JsonAlias("date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date dateTime;

    @JsonAlias("first_name")
    private String firstName;

    @JsonAlias("last_name")
    private String lastName;

    private ViolationType type;

    @JsonAlias("fine_amount")
    private Double fineAmount;

    private Violation() {}

    public Date getDateTime() {
        return dateTime;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ViolationType getType() {
        return type;
    }

    public double getFineAmount() {
        return fineAmount;
    }
}