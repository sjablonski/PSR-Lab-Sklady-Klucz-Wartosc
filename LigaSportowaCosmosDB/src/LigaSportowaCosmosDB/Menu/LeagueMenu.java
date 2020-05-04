package LigaSportowaCosmosDB.Menu;

import LigaSportowaCosmosDB.Enums.LeagueCRUDMenu;
import LigaSportowaCosmosDB.League;
import LigaSportowaCosmosDB.Sql.SqlApi;
import LigaSportowaCosmosDB.Team;
import com.google.gson.Gson;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.FeedResponse;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LeagueMenu {
    private final TextIO textIO = TextIoFactory.getTextIO();
    private final TextTerminal<?> terminal = textIO.getTextTerminal();
    Gson gson = new Gson();

    public LeagueMenu() {
        LeagueCRUDMenu choice;
        do {
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

            SqlApi.createDocumentIfNotExists("SportsLeagueDB", "LeagueCollection", league);

        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readAll() {
        FeedResponse<Document> results = SqlApi.executeSimpleQuery("SportsLeagueDB", "LeagueCollection", new String[]{"*"}, null);

        ArrayList<League> leagues = new ArrayList<>();
        for (Document result : results.getQueryIterable()) {
            leagues.add(gson.fromJson(result.toJson(),League.class));
        }

        leagues.forEach(this::printLeague);
    }

    private void readById() {
        try {
            String id = textIO.newStringInputReader()
                    .read("ID league");
            Document doc = SqlApi.getDocumentById("SportsLeagueDB", "LeagueCollection", id);
            if(doc != null) {
                League league = gson.fromJson(doc.toJson(),League.class);
                printLeague(league);
            } else {
                this.terminal.println("Not found");
            }
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readTables() {
        try {
            String id = textIO.newStringInputReader().read("ID league");
            String query = String.format("SELECT r.name, r.played, r.win, r.draw, r.lost, r.positive, r.negative, r.points FROM c JOIN r IN c.teams WHERE c.id = '%s'", id);
            FeedResponse<Document> results = SqlApi.executeQuery("SportsLeagueDB", "LeagueCollection", query);

            ArrayList<Team> teams = new ArrayList<>();
            for (Document result : results.getQueryIterable()) {
                teams.add(gson.fromJson(result.toJson(),Team.class));
            }

            Collections.sort(teams);

            this.printTable(teams);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void update() {
        try{
            String id = textIO.newStringInputReader().read("ID to update");
            Document doc = SqlApi.getDocumentById("SportsLeagueDB", "LeagueCollection", id);
            if(doc != null) {
                League league = gson.fromJson(doc.toJson(),League.class);
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
                SqlApi.replaceDocument("SportsLeagueDB", "LeagueCollection", league);
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
            SqlApi.deleteDocument("SportsLeagueDB", "LeagueCollection", id);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void printLeague(League league) {
        this.terminal.println();
        this.terminal.println(String.format("ID: %s", league.getId()));
        this.terminal.println(String.format("Name: %s", league.getName()));
        this.terminal.println(String.format("Country: %s", league.getCountry()));
        this.terminal.println(String.format("Discipline: %s", league.getDiscipline()));
        this.terminal.println(String.format("Number of teams: %s", league.getNumberOfTeams()));
        this.terminal.println("Table:");
        this.printTable(league.getTeams());
    }

    private void printTable(Collection<Team> teams) {
        int index = 0;
        String leagueTableFormat = "| %-5s | %-21s | %-6s | %-2s | %-2s | %-2s | %-2s | %-2s | %-6s |";
        String txt = teams.iterator().next().getId() != null ? "ID" : "Nr";

        this.terminal.println("+-------+-----------------------+--------+----+----+----+----+----+--------+");
        this.terminal.println("| "+ txt +"    | Club                  | Played | W  | D  | L  | +  | -  | Points |");
        this.terminal.println("+-------+-----------------------+--------+----+----+----+----+----+--------+");
        for(Team t : teams) {
            index++;
            this.terminal.println(String.format(leagueTableFormat,
                    t.getId() != null ? t.getId() : index, t.getName(), t.getPlayed(),
                    t.getWin(), t.getDraw(), t.getLost(),
                    t.getPositive(), t.getNegative(), t.getPoints())
            );
        }
        this.terminal.println("+-------+-----------------------+--------+----+----+----+----+----+--------+");
        this.terminal.println();
    }

}
