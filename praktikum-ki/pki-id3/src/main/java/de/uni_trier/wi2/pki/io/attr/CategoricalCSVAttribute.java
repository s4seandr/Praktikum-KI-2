package de.uni_trier.wi2.pki.io.attr;

import java.util.StringTokenizer;

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
        return null;
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
}
