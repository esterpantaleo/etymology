package org.getalp.dbnary;

import java.io.Serializable;

public class OffsetValue implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -570466055812338894L;
    /**
	 * @uml.property  name="start"
	 */
    public long start;
	/**
	 * @uml.property  name="length"
	 */
	public int length;

	public OffsetValue(long start, int length) {
		this.start = start;
		this.length = length;
	}

    public String toString() {
        return "" + start + "/" + length;
	}
}
