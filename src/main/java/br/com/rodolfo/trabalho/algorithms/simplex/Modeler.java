package br.com.rodolfo.trabalho.algorithms.simplex;

import br.com.rodolfo.trabalho.algorithms.simplex.enums.Constraint;

/**
 * Modeler
 */
public class Modeler {

    private double[][] a; // tableaux
    private int numberOfConstraints; // number of constraints
    private int numberOfOriginalVariables; // number of original variables

    public Modeler(double[][] constraintLeftSide, double[] constraintRightSide, Constraint[] constraintOperator,
            double[] objectiveFunction) {
        numberOfConstraints = constraintRightSide.length;
        numberOfOriginalVariables = objectiveFunction.length;
        a = new double[numberOfConstraints + 1][numberOfOriginalVariables + numberOfConstraints + 1];

        // initialize constraint
        for (int i = 0; i < numberOfConstraints; i++) {
            for (int j = 0; j < numberOfOriginalVariables; j++) {
                a[i][j] = constraintLeftSide[i][j];
            }
        }

        for (int i = 0; i < numberOfConstraints; i++)
            a[i][numberOfConstraints + numberOfOriginalVariables] = constraintRightSide[i];

        // initialize slack variable
        for (int i = 0; i < numberOfConstraints; i++) {
            int slack = 0;
            switch (constraintOperator[i]) {
            case greatherThan:
                slack = -1;
                break;
            case lessThan:
                slack = 1;
                break;
            default:
            }
            a[i][numberOfOriginalVariables + i] = slack;
        }

        // initialize objective function
        for (int j = 0; j < numberOfOriginalVariables; j++)
            a[numberOfConstraints][j] = objectiveFunction[j];
    }

    public double[][] getTableaux() {
        return a;
    }

    public int getNumberOfConstraint() {
        return numberOfConstraints;
    }

    public int getNumberOfOriginalVariable() {
        return numberOfOriginalVariables;
    }
}