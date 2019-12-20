public class Do {

    static String propertiesPath;

    public static void setPropertiesPath(String propertiesPath) {
        Do.propertiesPath = propertiesPath;
    }

    public static void main(String[] args) {


//        setPropertiesPath("config/dbcp.properties");
        setPropertiesPath(args[0]);
        Options.menu();
    }
}
