package br.com.rodolfo.trabalho.models;

/**
 * IntervalosCoeficiente
 */
public class IntervalosCoeficiente {

    private String coeficiente;
    private double lower_c;
    private double upper_c;

    public IntervalosCoeficiente() {}

    public IntervalosCoeficiente(String coeficiente, double lower_c, double upper_c) {
        this.coeficiente = coeficiente;
        this.lower_c = lower_c;
        this.upper_c = upper_c;
    }

    public String getCoeficiente() {
        return this.coeficiente;
    }

    public void setCoeficiente(String coeficiente) {
        this.coeficiente = coeficiente;
    }

    public double getLower_c() {
        return this.lower_c;
    }

    public void setLower_c(double lower_c) {
        this.lower_c = lower_c;
    }

    public double getUpper_c() {
        return this.upper_c;
    }

    public void setUpper_c(double upper_c) {
        this.upper_c = upper_c;
    }

    @Override
    public String toString() {
        return "{" +
            " coeficiente='" + getCoeficiente() + "'" +
            ", lower_c='" + getLower_c() + "'" +
            ", upper_c='" + getUpper_c() + "'" +
            "}";
    }

}