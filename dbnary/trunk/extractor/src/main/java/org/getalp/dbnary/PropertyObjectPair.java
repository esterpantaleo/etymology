package org.getalp.dbnary;

import java.util.AbstractMap.SimpleImmutableEntry;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.HashMap;

public class PropertyObjectPair extends SimpleImmutableEntry<Property, RDFNode> {
	private static HashMap<Property, HashMap<RDFNode, PropertyObjectPair>> instances = new HashMap<Property, HashMap<RDFNode, PropertyObjectPair>>();
	private PropertyObjectPair(Property p, RDFNode o) {
		super(p, o);
	}

	public Resource getResource() {
		return getValue().asResource();
	}

	public Literal getLiteral() {
		return getValue().asLiteral();
	}

	public static PropertyObjectPair get(Property p, RDFNode o) {
		HashMap<RDFNode, PropertyObjectPair> objects = instances.get(p);

		if (objects == null) {
		    synchronized (PropertyObjectPair.class) {
			    objects = instances.get(p);
			    if (objects == null) {
				    objects = new HashMap<RDFNode, PropertyObjectPair>();
				    instances.put(p, objects);
			    }
		    }
	    }

	    PropertyObjectPair po = objects.get(o);

		if (po == null) {
		    synchronized (PropertyObjectPair.class) {
			    po = objects.get(o);
			    if (po == null) {
				    po = new PropertyObjectPair(p, o);
				    objects.put(o, po);
			    }
		    }
	    }

	    return po;
	}

    @Override
    public String toString() {
        return new StringBuffer().append(this.getKey().getLocalName()).append("-->")
                .append(this.getResource().getLocalName()).toString();
    }
}
