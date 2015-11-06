package de.markusdamm.pokerapp.data;



public class Player implements Comparable<Player> {


    private boolean gender;
    private String name;
    private boolean selected, selectable;
    private int id;

    public String getName() {
        return name;
    }

    public Player(String name, int id, int gender){
        this.name = name;
        this.selected = false;
        this.id = id;
        this.selectable = true;
        this.gender = Gender.toBool(gender);
    }

    public void setSelectable(boolean selectable){
        this.selectable = selectable;
    }

    public void toggleSelected(){
        selected = !selected;
    }

    public boolean isSelectable(){
        return selectable;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String toString(){
        return name;
    }


    public void setGender(boolean gender){
        this.gender = gender;
    }

    public boolean getGender(){
        return gender;
    }

    public int getGenderAsInt(){
        return Gender.toInt(gender);
    }

    @Override
    public int compareTo(Player another) {
        return this.name.compareTo(another.getName());
    }
}