/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Politician {
    
    private String name;
    private String party;
    private long votes;
    private int rank;

    public Politician(String name, String party) {
        this.name = name;
        this.party = party;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }
    
    public void setParty(String party) {
        this.party = party;
    }

    public long getVotes() {
        return votes;
    }
    
    public void setVotes(long votes) {
        this.votes = votes;
    }

    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    @Override
    public String toString() {
        return rank + ") " + name + " (" + party + ") " + " - " + votes;
    }
    
    public String toCsv() {
        return name + "," + party + "," + votes;
    }
    
    public Politician(String csv) {
        String array[] = csv.split(",");
        this.name = array[0];
        this.party = array[1];
        this.votes = Long.valueOf(array[2]);
    }
}
