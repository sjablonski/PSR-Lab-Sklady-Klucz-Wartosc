package LigaSportowaCosmosDB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class League implements IDocument{
    private String id;
    private String name;
    private String discipline;
    private String country;
    private int numberOfTeams;
    private Collection<Team> teams;

    public League() { this.id = UUID.randomUUID().toString().substring(0,4);}

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

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
}
