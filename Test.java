import java.net.URL;

public class Test {
    public static void main(String[] args) throws Exception {
        new URL("https://repo.maven.apache.org/maven2/").openStream();
        System.out.println("Connection successful");
    }
}