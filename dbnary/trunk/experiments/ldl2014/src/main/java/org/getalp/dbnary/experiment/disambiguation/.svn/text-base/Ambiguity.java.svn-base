package org.getalp.dbnary.experiment.disambiguation;


import java.util.List;
import java.util.Set;

public interface Ambiguity extends Comparable<Ambiguity> {
    public String getGloss();

    public String getId();

    public void addDisambiguation(final String method, final Disambiguable d);

    public List<Disambiguable> getDisambiguations(String method);

    public Disambiguable getBestSolution(String method);
	List<Disambiguable> getBestSolutions(String method);    

    public Set<String> getMethods();

    public String toString(String method);

    public String toStringVote();

}
