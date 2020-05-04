package LigaSportowaHazelcast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class League implements Serializable {
    private String name;
    private String discipline;
    private String country;
    private int numberOfTeams;
    private Collection<Team> teams;

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscipline() { return discipline; }

    public void setDiscipline(String discipline) { this.discipline = discipline; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public int getNumberOfTeams() { return numberOfTeams; }

    public void setNumberOfTeams(int numberOfTeams) { this.numberOfTeams = numberOfTeams; }

    public Collection<Team> getTeams() { return teams; }

    public Team getTeam(int index) {return ((ArrayList<Team>)teams).get(index);}

    public void setTeams(Collection<Team> teams) { this.teams = teams;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Name: %s\n", this.name));
        sb.append(String.format("Country: %s\n", this.country));
        sb.append(String.format("Discipline: %s\n", this.discipline));
        sb.append(String.format("Number of teams: %s\n", this.numberOfTeams));
        sb.append("Table:\n");
        sb.append("+-------+-----------------------+--------+----+----+----+----+----+--------+\n");
        sb.append("| ID    | Club                  | Played | W  | D  | L  | +  | -  | Points |\n");
        sb.append("+-------+-----------------------+--------+----+----+----+----+----+--------+\n");
        for(Team t : teams) {
            sb.append(t.toString());
        }
        sb.append("+-------+-----------------------+--------+----+----+----+----+----+--------+\n");
        return sb.toString();
    }
}
