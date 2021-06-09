package de.uni_trier.wi2.pki.io.attr;

import java.util.Objects;

public class CategoricalCSVAttribute implements CSVAttribute {
    public String value;

    public CategoricalCSVAttribute(String value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value.toString();
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Object clone() {
        return new CategoricalCSVAttribute(this.value);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CSVAttribute) {
            if (this.value == ((CategoricalCSVAttribute) o).getValue()) {
                return 0;
            }
        }
        return -1;
    }

    @Override
    public String toString(){
        return this.value;
    }

    @Override
    public boolean equals( Object that){
        if (that instanceof CategoricalCSVAttribute) {
            if(this.value.equals(((CategoricalCSVAttribute) that).getValue()) ){
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