package org.rampTraffic.algorithms;

/*
* AlgorithmFactory implements the factory pattern where, depending on the algorithm
* specified, AlgorithmFactory returns an instance of that algorithm.
* The factory pattern is used to alleviate extension of additionally implemented algorithms
* */

public class AlgorithmFactory {

    // Data members


    // Constructors


    // Methods
    public static MAPFAlgorithm getAlgorithm(String algorithm) {
        algorithm = algorithm.toLowerCase();
        return switch (algorithm) {
            case ("astar") -> new Astar();
            case ("icts") -> new ICTS();
            case ("cbs") -> new CBS();
            case ("cbswp") -> new CBSwP();
            default -> throw new IllegalArgumentException("Tried to invoke unknown algorithm: " + algorithm);
        };
    }
}
