package LigaSportowaCosmosDB;

import LigaSportowaCosmosDB.Menu.LeagueMenu;
import LigaSportowaCosmosDB.Menu.ScheduleMenu;
import LigaSportowaCosmosDB.Sql.SqlApi;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import LigaSportowaCosmosDB.Enums.MainMenu;

import static LigaSportowaCosmosDB.Enums.MainMenu.Exit;

public class Program {

    private final TextIO textIO;

    public Program() {
        textIO = TextIoFactory.getTextIO();
        textIO.getTextTerminal().getProperties().setPaneWidth(800);
    }

    private void mainMenu() {
        MainMenu choice;

        try {
            this.textIO.getTextTerminal().println("Loading...");

            SqlApi.createDatabaseIfNotExists("SportsLeagueDB");
            SqlApi.createDocumentCollectionIfNotExists("SportsLeagueDB", "LeagueCollection");
            SqlApi.createDocumentCollectionIfNotExists("SportsLeagueDB", "ScheduleCollection");

            do {
                choice = textIO.newEnumInputReader(MainMenu.class).read("=== Menu ===");
                switch (choice) {
                    case League: new LeagueMenu(); break;
                    case Schedule: new ScheduleMenu(); break;
                    default: break;
                }
            } while (choice != Exit);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
            this.textIO.getTextTerminal().println("Error: Operation failed");
        }
    }

    public static void main(String[] args) {
        try {
            Program p = new Program();
            p.mainMenu();
        } catch (Exception e) {
            System.out.println(String.format("DocumentDB GetStarted failed with %s", e));
        }
    }
}