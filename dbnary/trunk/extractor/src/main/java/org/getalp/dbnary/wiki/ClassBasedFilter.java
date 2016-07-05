package org.getalp.dbnary.wiki;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by serasset on 01/02/16.
 */
public class ClassBasedFilter implements WikiEventFilter {
    private HashSet<Class> allowedClasses = new HashSet<Class>();

    public ClassBasedFilter() {
        super();
    }

    public ClassBasedFilter(Set<Class> allowedClasses) {
        super();
        this.allowedClasses.addAll(allowedClasses);
    }

    public ClassBasedFilter allowTemplates() {
        allowedClasses.add(WikiText.Template.class);
        return this;
    }

    public ClassBasedFilter allowInternalLink() {
        allowedClasses.add(WikiText.InternalLink.class);
        return this;
    }

    public ClassBasedFilter allowLink() {
        allowedClasses.add(WikiText.Link.class);
        return this;
    }

    public ClassBasedFilter allowExternalLink() {
        allowedClasses.add(WikiText.ExternalLink.class);
        return this;
    }

    public ClassBasedFilter allowHTMLComment() {
        allowedClasses.add(WikiText.HTMLComment.class);
        return this;
    }

    public ClassBasedFilter allowNowiki() {
        // TODO: implement nowiki handling
        return this;
    }

    public ClassBasedFilter allowAll() {
        this.allowExternalLink().allowHTMLComment().allowInternalLink().allowNowiki().allowTemplates();
        return this;
    }

    public ClassBasedFilter denyTemplates() {
        allowedClasses.remove(WikiText.Template.class);
        return this;
    }

    public ClassBasedFilter denyInternalLink() {
        allowedClasses.remove(WikiText.InternalLink.class);
        return this;
    }

    public ClassBasedFilter denyExternalLink() {
        allowedClasses.remove(WikiText.ExternalLink.class);
        return this;
    }

    public ClassBasedFilter denyHTMLComment() {
        allowedClasses.remove(WikiText.HTMLComment.class);
        return this;
    }

    public ClassBasedFilter denyNowiki() {
        // TODO: implement nowiki handling
        return this;
    }

    public ClassBasedFilter denyAll() {
        allowedClasses.clear();
        return this;
    }

    @Override
    public boolean apply(WikiText.Token tok) {
        for (Class allowedClass : allowedClasses) {
            if (allowedClass.isInstance(tok)) return true;
        }
        return false;
    }
}
