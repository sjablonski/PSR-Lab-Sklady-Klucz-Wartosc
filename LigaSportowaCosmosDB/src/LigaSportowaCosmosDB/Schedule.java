package LigaSportowaCosmosDB;

import LigaSportowaCosmosDB.Enums.ScheduleStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Schedule implements IDocument {
    private final String id;
    private String idLeague;
    private String leagueName;
    private ScheduleStatus status;
    private String date;
    private String homeTeamId;
    private String awayTeamId;
    private int homeTeamScore;
    private int awayTeamScore;

    public Schedule() { this.id = UUID.randomUUID().toString().substring(0,4);}

    public String getId() { return id; }

    public String getIdLeague() { return idLeague; }

    public void setIdLeague(String idLeague) { this.idLeague = idLeague; }

    public String getLeagueName() { return leagueName; }

    public void setLeagueName(String leagueName) { this.leagueName = leagueName; }

    public ScheduleStatus getStatus() { return status; }

    public void setStatus(ScheduleStatus status) { this.status = status; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getHomeTeamId() { return homeTeamId; }

    public void setHomeTeamId(String homeTeamId) { this.homeTeamId = homeTeamId; }

    public String getAwayTeamId() { return awayTeamId; }

    public void setAwayTeamId(String awayTeamId) { this.awayTeamId = awayTeamId; }

    public int getHomeTeamScore() { return homeTeamScore; }

    public void setHomeTeamScore(int homeTeamScore) { this.homeTeamScore = homeTeamScore; }

    public int getAwayTeamScore() { return awayTeamScore; }

    public void setAwayTeamScore(int awayTeamScore) { this.awayTeamScore = awayTeamScore; }

}
