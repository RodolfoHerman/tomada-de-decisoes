package br.com.rodolfo.trabalho.algorithms.sequence;

/**
 * Sequence
 */
public interface Sequence {

    /**
     * Returns a {@code N x D} matrix of real numbers in the range {@code [0,
     * 1]}.
     * 
     * @param N the number of sample points
     * @param D the dimension of each sample point
     * @return a {@code N x D} matrix of real numbers in the range {@code [0,
     *         1]}
     */
    public double[][] generate(int N, int D);

}