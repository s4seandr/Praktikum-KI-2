package de.uni_trier.wi2.pki.io.attr;

/**
 * TODO: Generalize
 */

public class ContinuousCSVAttribute implements CSVAttribute{

    private double value;

    public ContinuousCSVAttribute( double value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) { this.value = (double) value; }

    @Override
    public Object getValue() { return value; }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public Object clone() {
        return null;
    }
}
