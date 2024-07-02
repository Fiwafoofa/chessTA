import server.Server;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        String domainName;
        if (args.length < 1) {
            System.out.println("Using default localhost:8080");
            domainName = "localhost:8080";
        } else {
            domainName = args[0];
        }
        new Server().run(8080);
        new ChessClient(domainName).run();
    }
}