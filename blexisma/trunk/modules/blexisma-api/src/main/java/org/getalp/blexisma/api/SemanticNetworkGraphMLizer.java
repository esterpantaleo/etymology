package org.getalp.blexisma.api;

import java.io.IOException;

public abstract class SemanticNetworkGraphMLizer {

    public abstract void load(SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement> sm);

    public abstract void dump(SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement> sm) throws IOException;

}
