package com.picopy;

import java.util.Collection;

public class ArrayList<T> extends java.util.ArrayList<T>{

	private static final long serialVersionUID = -2280116744144438187L;

	public ArrayList() {
		super();
	}

	public ArrayList(Collection<? extends T> c) {
		super(c);
	}

	public ArrayList(int initialCapacity) {
		super(initialCapacity);
	}
	
	public final int getSize() {
		return this.size() ; 
	}
	
	public final T get( int index ) {		
		if( 0 > index ) {
			index = this.size() - index ;
		}
		
		if( 0 > index ) {
			return null ; 
		}
		
		return super.get( index );
	}

}
