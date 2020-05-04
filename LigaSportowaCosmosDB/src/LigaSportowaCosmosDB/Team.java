package LigaSportowaCosmosDB;

public class Team implements Comparable {
    private String id;
    private String name;
    private int played;
    private int win;
    private int draw;
    private int lost;
    private int positive;
    private int negative;
    private int points;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getPlayed() { return played; }

    public void setPlayed(int played) { this.played = played; }

    public int getWin() { return win; }

    public void setWin(int win) { this.win = win; }

    public int getDraw() { return draw; }

    public void setDraw(int draw) { this.draw = draw; }

    public int getLost() { return lost; }

    public void setLost(int lost) { this.lost = lost; }

    public int getPositive() { return positive; }

    public void setPositive(int positive) { this.positive = positive; }

    public int getNegative() { return negative; }

    public void setNegative(int negative) { this.negative = negative; }

    public int getPoints() { return points; }

    public void setPoints(int points) { this.points = points; }

    @Override
    public int compareTo(Object object) {
        int compare=((Team)object).getPoints();
        return compare-this.points;
    }
}
