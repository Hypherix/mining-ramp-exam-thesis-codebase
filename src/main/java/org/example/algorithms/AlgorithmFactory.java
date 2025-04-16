package org.example.algorithms;

/*
* AlgorithmFactory implements the factory pattern where, depending on the by string
* specified algorithm, AlgorithmFactory returns an instance of that algorithm.
* The factory pattern is used to alleviate extension of further algorithms
*
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
            default -> throw new IllegalArgumentException("Tried to use unknown algorithm: " + algorithm);
        };
    }
}
