package LigaSportowaHazelcast.Menu;

import LigaSportowaHazelcast.Enums.LeagueCRUDMenu;
import LigaSportowaHazelcast.HConfig;
import LigaSportowaHazelcast.League;
import LigaSportowaHazelcast.Team;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.net.UnknownHostException;
import java.util.*;

public class LeagueMenu {
    private final TextIO textIO = TextIoFactory.getTextIO();
    private final TextTerminal<?> terminal = textIO.getTextTerminal();
    private final ClientConfig clientConfig = HConfig.getClientConfig();
    private final HazelcastInstance client = HazelcastClient.newHazelcastClient( clientConfig );

    public LeagueMenu() throws UnknownHostException {
        LeagueCRUDMenu choice;
        do {
            terminal.println();
            choice = textIO.newEnumInputReader(LeagueCRUDMenu.class)
                    .read("=== League Menu ===");

            switch(choice) {
                case Create:        this.create();      break;
                case ReadAll:       this.readAll();     break;
                case ReadById:      this.readById();    break;
                case ReadTables:    this.readTables();  break;
                case Update:        this.update();      break;
                case Delete:        this.delete();      break;
                default: break;
            }
        } while (choice != LeagueCRUDMenu.Back);
    }

    private void create() {
        try {
            ArrayList<Team> teams = new ArrayList<>();
            League league = new League();

            league.setName(
                    textIO.newStringInputReader()
                            .withMinLength(2)
                            .read("Nazwa")
            );

            league.setDiscipline(
                    textIO.newStringInputReader()
                            .withMinLength(2)
                            .read("Dyscyplina")
            );

            league.setCountry(
                    textIO.newStringInputReader()
                            .withMinLength(2)
                            .read("Kraj")
            );

            league.setNumberOfTeams(
                    textIO.newIntInputReader()
                            .withMinVal(2)
                            .read("Liczba drużyn")
            );

            for(int i=0; i<league.getNumberOfTeams(); i++) {
                teams.add(new Team(String.valueOf(i+1),
                        textIO.newStringInputReader()
                                .withMinLength(2)
                                .read(String.format("Team name %d", i+1)))
                );
            }

            league.setTeams(teams);

            Map<String, League> mapLeague = client.getMap("leagues");
            String key = UUID.randomUUID().toString().substring(0,4);
            mapLeague.put(key, league);
            terminal.println("Created league: " + key );

        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readAll() {
        try {
            Map<String, League> leagues = client.getMap( "leagues" );
            if(leagues.size() == 0) {
                terminal.println("Leagues is empty");
                return;
            }
            for(Map.Entry<String, League> e : leagues.entrySet()){
                terminal.println(String.format("ID: %s\n%s", e.getKey(), e.getValue()));
            }
        } catch (Exception ex) {
            terminal.println(ex.getMessage());
        }
    }

    private void readById() {
        try {
            String id = textIO.newStringInputReader().read("ID league");
            Map<String, League> leagues = client.getMap( "leagues" );
            if(leagues.size() == 0) {
                terminal.println("Leagues is empty");
                return;
            }
            League league = leagues.get(id);
            terminal.println(String.format("ID: %s\n%s", id, league.toString()));
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readTables() {
        try {
            String id = textIO.newStringInputReader().read("ID league");
            Map<String, League> leagues = client.getMap( "leagues" );
            if(leagues.size() == 0) {
                terminal.println("Leagues is empty");
                return;
            }
            ArrayList<Team> teams = (ArrayList<Team>) leagues.get(id).getTeams();
            Collections.sort(teams);

            this.printTable(teams);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void update() {
        try{
            String id = textIO.newStringInputReader().read("ID to update");
            Map<String, League> leagues = client.getMap( "leagues" );
            if(leagues.size() == 0) {
                terminal.println("Leagues is empty");
                return;
            }
            League league = leagues.get(id);
            if(league != null) {
                ArrayList<Team> teams = new ArrayList<>();
                league.setName(
                        textIO.newStringInputReader()
                                .withMinLength(2)
                                .withDefaultValue(league.getName())
                                .read("Nazwa")
                );

                league.setDiscipline(
                        textIO.newStringInputReader()
                                .withMinLength(2)
                                .withDefaultValue(league.getDiscipline())
                                .read("Dyscyplina")
                );

                league.setCountry(
                        textIO.newStringInputReader()
                                .withMinLength(2)
                                .withDefaultValue(league.getCountry())
                                .read("Kraj")
                );

                league.setNumberOfTeams(
                        textIO.newIntInputReader()
                                .withMinVal(2)
                                .withDefaultValue(league.getNumberOfTeams())
                                .read("Liczba drużyn")
                );

                for(int i=0; i<league.getNumberOfTeams(); i++) {
                    teams.add(new Team(String.valueOf(i+1),
                            textIO.newStringInputReader()
                                    .withMinLength(2)
                                    .withDefaultValue(league.getTeam(i).getName())
                                    .read(String.format("Team name %d", i+1)))
                    );
                }

                league.setTeams(teams);
                leagues.replace(id, league);
                terminal.println("Updated league: " + id);
            } else {
                this.terminal.println("Not found");
            }
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void delete() {
        try {
            String id = textIO.newStringInputReader().read("ID to delete");
            Map<String, League> leagues = client.getMap( "leagues" );
            if(leagues.size() == 0) {
                terminal.println("Leagues is empty");
                return;
            }
            leagues.remove(id);
            terminal.println("Deleted league: " + id);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void printTable(Collection<Team> teams) {
        int index = 0;
        String leagueTableFormat = "| %-5s | %-21s | %-6s | %-2s | %-2s | %-2s | %-2s | %-2s | %-6s |";

        this.terminal.println("+-------+-----------------------+--------+----+----+----+----+----+--------+");
        this.terminal.println("| Nr    | Club                  | Played | W  | D  | L  | +  | -  | Points |");
        this.terminal.println("+-------+-----------------------+--------+----+----+----+----+----+--------+");
        for(Team t : teams) {
            index++;
            this.terminal.println(String.format(leagueTableFormat,
                    index, t.getName(), t.getPlayed(),
                    t.getWin(), t.getDraw(), t.getLost(),
                    t.getPositive(), t.getNegative(), t.getPoints())
            );
        }
        this.terminal.println("+-------+-----------------------+--------+----+----+----+----+----+--------+");
        this.terminal.println();
    }

}
