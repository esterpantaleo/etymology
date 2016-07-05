/**
 * *
 * PluginInfo.java
 * Created on 28 févr. 2010 15:11:27
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.kernel.broker.predicates;

import org.getalp.blexisma.kernel.broker.Language;
import org.getalp.blexisma.kernel.broker.Role;

import org.cougaar.core.mts.MessageAddress;
import org.cougaar.util.UnaryPredicate;

/**
 * @author Didier SCHWAB
 *
 */
public class PluginInfo implements UnaryPredicate{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public int role;
    public int language;
    public MessageAddress agentAddress;
    
    public PluginInfo(int language, int role, MessageAddress agentAddress){
	
	this.role = role;
	this.language = language;
	this.agentAddress = agentAddress;
    }

    
    public boolean execute(Object o) {
	// TODO Auto-generated method stub
	return o instanceof PluginInfo;
    }
    
    public String toString(){
	
	StringBuilder rep = new StringBuilder(500);
	
	rep.append("language ");
	rep.append(Language.convert(language));
	rep.append(" role ");
	rep.append(Role.convert(role));
	rep.append(" agentAddress ");
	rep.append(agentAddress);
	
	return rep.toString();
	
    }
}
