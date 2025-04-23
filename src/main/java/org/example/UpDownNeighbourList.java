package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UpDownNeighbourList {

    // Data members
    private ArrayList<Integer> UpNeighbours;
    private ArrayList<Integer> DownNeighbours;
    private ArrayList<Integer> allNeighboursUnique;

    // Constructor
    public UpDownNeighbourList() {
        this.UpNeighbours = new ArrayList<>();
        this.DownNeighbours = new ArrayList<>();
    }

    // Methods
    public ArrayList<Integer> getUpNeighbours() {
        return this.UpNeighbours;
    }

    public ArrayList<Integer> getDownNeighbours() {
        return this.DownNeighbours;
    }

    public ArrayList<Integer> getAllNeighbours() {
        // Task: Return all unique neighbours.

        ArrayList<Integer> allNeighbours = new ArrayList<>();
        allNeighbours.addAll(UpNeighbours);
        allNeighbours.addAll(DownNeighbours);

        // Sets remove duplicates automatically
        Set<Integer> allNeighboursUnique = new HashSet<>(allNeighbours);

        // Convert back to ArrayList with duplicates removed
        allNeighbours = new ArrayList<>(allNeighboursUnique);
        return allNeighbours;
    }

    public void setAllNeighboursUnique() {
        ArrayList<Integer> allNeighbours = new ArrayList<>();
        allNeighbours.addAll(UpNeighbours);
        allNeighbours.addAll(DownNeighbours);

        // Sets remove duplicates automatically
        Set<Integer> allNeighboursUnique = new HashSet<>(allNeighbours);

        // Convert back to ArrayList with duplicates removed
        this.allNeighboursUnique = new ArrayList<>(allNeighboursUnique);
    }

    public void printNeighbourLists() {
        setAllNeighboursUnique();
        System.out.print(this.allNeighboursUnique);
    }
}
