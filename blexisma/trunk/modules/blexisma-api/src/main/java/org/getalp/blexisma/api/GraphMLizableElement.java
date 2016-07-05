package org.getalp.blexisma.api;

public interface GraphMLizableElement {

    /**
     * Retrieve the number of attributes that are used to GraphMLize all nodes of this class.
     * 
     * @return the number of attributes
     */
    public int getNumberOfAttributes();
    
    /**
     * Retrieves the name of attribute n° i for all nodes of this class.
     * 
     * @param i the number of the requested attribute.
     * @return the name of the i<sup>th</sup> attribute.  
     */
    public String getAttributeNameForId(int i);
    
    /**
     * Retrieves the type of attribute n° i for all nodes of this class.
     * 
     * @param i the number of the requested attribute.
     * @return the name of the i<sup>th</sup> attribute.  
     */
    public String getAttributeTypeForId(int i);

    /**
     * Retrieves the default value of attribute n° i for all nodes of this class.
     * 
     * @param i the number of the requested attribute.
     * @return the default value of the i<sup>th</sup> attribute or null if no default value is given.  
     */
    public String getAttributeDefaultForId(int i);

    /**
     * Retrieves the value of attribute n° i for this node.
     * 
     * @param i the number of the requested attribute.
     * @return the name of the i<sup>th</sup> attribute or null if no information is associated to this attribute.  
     */
    public String getAttributeValueForId(int i);
    
}
