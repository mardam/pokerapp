package de.markusdamm.pokerapp.data;

public class Location{


    private String name;

    public Location(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return this.name;
    }

}