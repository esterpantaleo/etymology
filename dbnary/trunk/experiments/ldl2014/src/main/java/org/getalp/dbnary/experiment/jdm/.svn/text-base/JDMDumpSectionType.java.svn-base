package org.getalp.dbnary.experiment.jdm;

/**
 * Created by tchechem on 19/02/14.
 */
public enum JDMDumpSectionType {
    NONE            (""),
    REL_STATS       ("---- Relation stats"),
    NODE_STATS      ("---- Node stats"),
    MF_TERMS        ("---- 50 most frequents terms"),
    REL_TYPES       ("---- RELATION TYPES"),
    NODES           ("-- NODES"),
    RELS            ("-- RELATIONS");

    private String matchString;

    JDMDumpSectionType(String matchString) {
        this.matchString = matchString;
    }

    public static JDMDumpSectionType fromDumpLine(String line, JDMDumpSectionType currentSection){
        for(JDMDumpSectionType s: JDMDumpSectionType.values()){
            if(line.contains(s.getMatchString())){
                return s;
            }
        }
        return currentSection;
    }

    public String getMatchString() {
        return matchString;
    }
}
