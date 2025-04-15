package org.example;


public class Main {
    public static void main(String[] args) {
        int[] passBays = {2, 4};
        Ramp myRamp = new Ramp(5, 3, 3, passBays);
        myRamp.printAdjList();
    }
}