package com.picopy;

import java.util.Collection;

public class StringList extends ArrayList<String> {

    public StringList() {
        super();
    }

    public StringList(Collection<String> c) {
        super(c);
    }

    public String [] toArray() { 
        String [] strings = new String[ this.size() ];

        super.toArray( strings );

        return strings;
    }
}
