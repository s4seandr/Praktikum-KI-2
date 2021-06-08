package de.uni_trier.wi2.pki.io.attr;


import java.util.Objects;

/**
 * TODO: Generalize
 */

public class ContinuousCSVAttribute implements CSVAttribute{

    public double value;

    public ContinuousCSVAttribute( double value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (double) value;
    }

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
        return new ContinuousCSVAttribute(this.value);
    }

    @Override
    public String toString(){
        return Double.toString(this.value);
    }

    @Override
    public boolean equals( Object that){
        if (that instanceof ContinuousCSVAttribute) {
            if(this.value == (double) ((ContinuousCSVAttribute) that).getValue() ){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
