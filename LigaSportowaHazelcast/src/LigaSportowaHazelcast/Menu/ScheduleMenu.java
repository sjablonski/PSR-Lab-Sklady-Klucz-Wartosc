package LigaSportowaHazelcast.Menu;

import LigaSportowaHazelcast.*;
import LigaSportowaHazelcast.Enums.ScheduleCRUDMenu;
import LigaSportowaHazelcast.Enums.ScheduleStatus;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ScheduleMenu {
    private final TextIO textIO = TextIoFactory.getTextIO();
    private final TextTerminal<?> terminal = textIO.getTextTerminal();
    private final ClientConfig clientConfig = HConfig.getClientConfig();
    private final HazelcastInstance client = HazelcastClient.newHazelcastClient( clientConfig );

    public ScheduleMenu() throws UnknownHostException {
        ScheduleCRUDMenu choice;
        do {
            terminal.println();
            choice = textIO.newEnumInputReader(ScheduleCRUDMenu.class)
                    .read("=== Schedule Menu ===");

            switch(choice) {
                case Create:    this.create();      break;
                case ReadAll:   this.readAll();     break;
                case ReadById:  this.readById();    break;
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

            Map<String, League> leagues = client.getMap( "leagues" );
            if(leagues.size() == 0) {
                terminal.println("Leagues is empty");
                return;
            }

            League league = leagues.get(schedule.getIdLeague());
            if(league != null) {
                schedule.setLeagueName(league.getName());

                Map<String, Schedule> mapLeague = client.getMap("schedules");
                String key = UUID.randomUUID().toString().substring(0,4);
                mapLeague.put(key, schedule);

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
                leagues.replace(schedule.getIdLeague(), league);
                terminal.println("Created schedule: " + key );
            } else {
                this.terminal.println("Not found");
            }

        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readAll() {
        try {
            Map<String, Schedule> schedules = client.getMap( "schedules" );
            if(schedules.size() == 0) {
                terminal.println("Schedule is empty");
                return;
            }
            for(Map.Entry<String, Schedule> e : schedules.entrySet()){
                terminal.println(String.format("ID: %s\n%s", e.getKey(), e.getValue()));
            }
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void readById() {
        try {
            String id = textIO.newStringInputReader().read("ID schedule");
            Map<String, Schedule> schedules = client.getMap( "schedules" );
            if(schedules.size() == 0) {
                terminal.println("Leagues is empty");
                return;
            }
            Schedule schedule = schedules.get(id);
            terminal.println(String.format("ID: %s\n%s", id, schedule.toString()));
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void update() {
        try {
            String id = textIO.newStringInputReader().read("ID to update");
            Map<String, Schedule> schedules = client.getMap( "schedules" );
            if(schedules.size() == 0) {
                terminal.println("Schedules is empty");
                return;
            }
            Schedule schedule = schedules.get(id);
            if(schedule != null) {
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
                schedules.replace(id, schedule);
                terminal.println("Updated schedule: " + id);
            }
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }

    private void delete() {
        try {
            String id = textIO.newStringInputReader().read("ID to delete");
            Map<String, Schedule> schedules = client.getMap( "schedules" );
            if(schedules.size() == 0) {
                terminal.println("Schedules is empty");
                return;
            }
            schedules.remove(id);
            terminal.println("Deleted schedule: " + id);
        } catch (Exception ex) {
            this.terminal.println(ex.getMessage());
        }
    }
}
