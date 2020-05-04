package LigaSportowaCosmosDB.Menu;

import LigaSportowaCosmosDB.Enums.ScheduleCRUDMenu;
import LigaSportowaCosmosDB.Enums.ScheduleStatus;
import LigaSportowaCosmosDB.League;
import LigaSportowaCosmosDB.Schedule;
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

public class ScheduleMenu {
    private final TextIO textIO = TextIoFactory.getTextIO();
    private final TextTerminal<?> terminal = textIO.getTextTerminal();
    private final Gson gson = new Gson();

    public ScheduleMenu() {
        ScheduleCRUDMenu choice;
        do {
            choice = textIO.newEnumInputReader(ScheduleCRUDMenu.class)
                    .read("=== Schedule Menu ===");

            switch(choice) {
                case Create:    this.create();      break;
                case ReadAll:   this.readAll();     break;
                case ReadById:  this.readById();    break;
                case ReadOnlyHomeFixturesByTeam:
                    this.readCustom("Fixtures", "homeTeamId"); break;
                case ReadOnlyAwayFixturesByTeam:
                    this.readCustom("Fixtures", "awayTeamId"); break;
                case ReadOnlyHomeResultsByTeam:
                    this.readCustom("Results", "homeTeamId"); break;
                case ReadOnlyAwayResultsByTeam:
                    this.readCustom("Results", "awayTeamId"); break;
                case Update:    this.update();      break;
                case Delete:    this.delete();      break;
                default: break;
            }
        } while (choice != ScheduleCRUDMenu.Back);
    }

    private void create() {
        try {
            Schedule schedule = new Schedule();

            schedule.setStatus(
                    textIO.newEnumInputReader(ScheduleStatus.class)
                            .read("Status")
            );

            schedule.setIdLeague(
                    textIO.newStringInputReader()
                            .read("ID League")
            );

            schedule.setHomeTeamId(
                    textIO.newStringInputReader()
                            .read("Home team ID")
            );

            schedule.setAwayTeamId(
                    textIO.newStringInputReader()
                            .read("Away team ID")
            );
            schedule.setDate(textIO.newStringInputReader()
                    .withDefaultValue("10-10-2010 10:10")
                    .withMinLength(1)
                    .read("Date")
            );

            if(schedule.getStatus() == ScheduleStatus.Results) {
                schedule.setHomeTeamScore(
                        textIO.newIntInputReader()
                                .read("Home team score")
                );

                schedule.setAwayTeamScore(
                        textIO.newIntInputReader()
                                .read("Away team score")
                );
            }

            Document doc = SqlApi.getDocumentById("SportsLeagueDB", "LeagueCollection", schedule.getIdLeague());
            if(doc != null) {

                schedule.setLeagueName(String.valueOf(doc.get("name")));
                SqlApi.createDocumentIfNotExists("SportsLeagueDB", "ScheduleCollection", schedule);

                League league = gson.fromJson(doc.toJson(),League.class);
                Collection<Team> teams = league.getTeams();
                int result = schedule.getHomeTeamScore() - schedule.getAwayTeamScore();
                teams.forEach(el -> {
                    if(el.getId().equals(schedule.getHomeTeamId())) {
                        if(result > 0) {
                            el.setWin(el.getWin() + 1);
                            el.setPoints(el.getPoints() + 3);
                        } else if (result < 0) {
                            el.setLost(el.getLost() + 1);
                        } else {
                            el.setDraw(el.getDraw() + 1);
                            el.setPoints(el.getPoints() + 1);
                        }
                        el.setPlayed(el.getPlayed() + 1);
                        el.setPositive(el.getPositive() + schedule.getHomeTeamScore());
                        el.setNegative(el.getNegative() + schedule.getAwayTeamScore());
                    } else if(el.getId().equals(schedule.getAwayTeamId())) {
                        if(result > 0) {
                            el.setLost(el.getLost() + 1);
                        } else if (result < 0) {
                            el.setWin(el.getWin() + 1);
                            el.setPoints(el.getPoints() + 3);
                        } else {
                            el.setDraw(el.getDraw() + 1);
                            el.setPoints(el.getPoints() + 1);
                        }
                        el.setPlayed(el.getPlayed() + 1);
                        el.setPositive(el.getPositive() + schedule.getAwayTeamScore());
                        el.setNegative(el.getNegative() + schedule.getHomeTeamScore());
                    }
                });
                SqlApi.replaceDocument("SportsLeagueDB", "LeagueCollection", league);
            } else {
                this.terminal.println("Not found");
            }

        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readAll() {
        try {
            FeedResponse<Document> results = SqlApi.executeSimpleQuery("SportsLeagueDB", "ScheduleCollection", new String[]{"*"}, null);
            ArrayList<Schedule> schedules = new ArrayList<>();
            for (Document result : results.getQueryIterable()) {
                schedules.add(gson.fromJson(result.toJson(),Schedule.class));
            }

            schedules.forEach(this::printSchedule);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readById() {
        try {
            String id = textIO.newStringInputReader().read("ID schedule");
            Document doc = SqlApi.getDocumentById("SportsLeagueDB", "ScheduleCollection", id);
            if(doc != null) {
                Schedule schedule = gson.fromJson(doc.toJson(),Schedule.class);
                printSchedule(schedule);
            } else {
                this.terminal.println("Not found");
            }
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readCustom(String status, String who) {
        try {
            String id = textIO.newStringInputReader().read("ID team");
            String where = String.format("c.status = '%s' AND c.%s = '%s'", status, who, id);
            FeedResponse<Document> results = SqlApi.executeSimpleQuery("SportsLeagueDB", "ScheduleCollection", new String[]{"*"}, where);
            ArrayList<Schedule> schedules = new ArrayList<>();
            for (Document result : results.getQueryIterable()) {
                schedules.add(gson.fromJson(result.toJson(),Schedule.class));
            }

            schedules.forEach(this::printSchedule);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void update() {
        try {
            String id = textIO.newStringInputReader().read("ID to update");
            Document doc = SqlApi.getDocumentById("SportsLeagueDB", "ScheduleCollection", id);
            if(doc != null) {
                Schedule schedule = gson.fromJson(doc.toJson(),Schedule.class);

                schedule.setStatus(
                        textIO.newEnumInputReader(ScheduleStatus.class)
                                .withDefaultValue(schedule.getStatus())
                                .read("Status")
                );

                schedule.setIdLeague(
                        textIO.newStringInputReader()
                                .withDefaultValue(schedule.getIdLeague())
                                .read("ID League")
                );

                schedule.setHomeTeamId(
                        textIO.newStringInputReader()
                                .withDefaultValue(schedule.getHomeTeamId())
                                .read("Home team ID")
                );

                schedule.setAwayTeamId(
                        textIO.newStringInputReader()
                                .withDefaultValue(schedule.getAwayTeamId())
                                .read("Away team ID")
                );
                schedule.setDate(textIO.newStringInputReader()
                        .withDefaultValue(schedule.getDate())
                        .withMinLength(1)
                        .read("Date")
                );

                if(schedule.getStatus() == ScheduleStatus.Results) {
                    schedule.setHomeTeamScore(
                            textIO.newIntInputReader()
                                    .withDefaultValue(schedule.getHomeTeamScore())
                                    .read("Home team score")
                    );

                    schedule.setAwayTeamScore(
                            textIO.newIntInputReader()
                                    .withDefaultValue(schedule.getAwayTeamScore())
                                    .read("Away team score")
                    );
                }
                SqlApi.replaceDocument("SportsLeagueDB", "ScheduleCollection", schedule);
            }
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void delete() {
        try {
            String id = textIO.newStringInputReader()
                    .read("ID to delete");
            SqlApi.deleteDocument("SportsLeagueDB", "ScheduleCollection", id);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void printSchedule(Schedule s) {
        String scheduleTableFormat = "| %-6s | %-10s |    | %-10s | %-6s |";

        this.terminal.println();
        this.terminal.println(String.format("ID schedule: %s", s.getId()));
        this.terminal.println(String.format("League name: %s", s.getLeagueName()));
        this.terminal.println(String.format("Status: %s, Date: %s", s.getStatus(), s.getDate()));
        this.terminal.println("+--------+------------+----+------------+--------+");
        this.terminal.println("| HomeID | Home Score | vs | Away Score | AwayID |");
        this.terminal.println("+--------+------------+----+------------+--------+");
        this.terminal.println(String.format(scheduleTableFormat, s.getHomeTeamId(), s.getHomeTeamScore(), s.getAwayTeamScore(), s.getAwayTeamId()));
        this.terminal.println("+--------+------------+----+------------+--------+");
        this.terminal.println();
    }
}
