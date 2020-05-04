package LigaSportowaHazelcast;

import LigaSportowaHazelcast.Enums.MainMenu;
import LigaSportowaHazelcast.Menu.LeagueMenu;
import LigaSportowaHazelcast.Menu.ScheduleMenu;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import static LigaSportowaHazelcast.Enums.MainMenu.Exit;

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
            Config config = HConfig.getConfig();
            Hazelcast.newHazelcastInstance(config);

            do {
                this.textIO.getTextTerminal().println();
                choice = textIO.newEnumInputReader(MainMenu.class).read("=== Menu ===");
                switch (choice) {
                    case League: new LeagueMenu(); break;
                    case Schedule: new ScheduleMenu(); break;
                    default: break;
                }
            } while (choice != Exit);
        } catch (Exception ex) {
            this.textIO.getTextTerminal().println("Error: Operation failed");
        }
    }

    public static void main(String[] args) {
        try {
            Program p = new Program();
            p.mainMenu();
        } catch (Exception e) {
            System.out.println(String.format("GetStarted failed with %s", e));
        }
    }
}