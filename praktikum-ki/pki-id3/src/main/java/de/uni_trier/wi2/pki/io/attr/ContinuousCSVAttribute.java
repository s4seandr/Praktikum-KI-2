package de.uni_trier.wi2.pki.io.attr;

import javafx.css.CssMetaData;

/**
 * TODO: Generalize
 */

public class ContinuousCSVAttribute implements CSVAttribute{

    public double value;

    public ContinuousCSVAttribute( double value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) { this.value = (double) value; }

    @Override
    public Object getValue() { return this.value; }

    @Override
    public int compareTo(Object o) {
        if(o instanceof ContinuousCSVAttribute) {
            if (this.value < (double) ((CSVAttribute) o).getValue() ){
                return -1;
            }
            if (this.value == (double) ((CSVAttribute) o).getValue() ){
                return 0;
            }
            if (this.value > (double) ((CSVAttribute) o).getValue() ){
                return 1;
            }
        }else {
            throw new ClassCastException();
        }
        return -1;
    }

    @Override
    public Object clone() {
        return null;
    }
}
