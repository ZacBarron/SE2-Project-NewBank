package newbank.server;

public class PostalAddress {
    private String firstLine;
    private String secondLine;
    private String town;
    private String county;
    private String postCode;

    public void setAddress(String firstLine, String secondLine, String town, String county, String postCode) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.town = town;
        this.county = county;
        this.postCode = postCode;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String toString() {
        return String.format("%s\n%s\n%\n%s\n%s\n", firstLine, secondLine, town, county, postCode);
    }

}