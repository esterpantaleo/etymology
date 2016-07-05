DB.DBA.VAD_INSTALL('/opt/virtuoso-opensource/share/virtuoso/vad/isparql_dav.vad', 0);
DB.DBA.VAD_INSTALL('/opt/virtuoso-opensource/share/virtuoso/vad/fct_dav.vad', 0);
DB.DBA.VHOST_REMOVE ( lhost=>'*ini*', vhost=>'*ini*', lpath=>'/dbnary' );

DB.DBA.VHOST_DEFINE ( lhost=>'*ini*', vhost=>'*ini*', lpath=>'/dbnary', ppath=>'/DAV/', is_dav=>1,
def_page=>'', vsp_user=>'dba', ses_vars=>0, opts=>vector ('browse_sheet', '', 'url_rewrite', 'http_rule_list_1'),
is_default_host=>0
);

DB.DBA.URLREWRITE_CREATE_RULELIST (
'http_rule_list_1', 1,
vector ('http_rule_1', 'http_rule_2', 'http_rule_3', 'http_rule_4'));

DB.DBA.URLREWRITE_CREATE_REGEX_RULE (
'http_rule_1', 1,
'^/(.*)\$',
vector ('par_1'),
1,
'/sparql?query=DESCRIBE%%20%%3Chttp%%3A%%2F%%2Fkaiko.getalp.org%%2F%U%%3E&format=%U',
vector ('par_1', '*accept*'),
NULL,
'(text/rdf.n3)|(application/rdf.xml)',
2,
303,
''
);


DB.DBA.URLREWRITE_CREATE_REGEX_RULE (
'http_rule_2', 1,
'^/(.*)\$',
vector ('par_1'),
1,
'/describe/?url=http%%3A%%2F%%2Fkaiko.getalp.org%%2F%s',
vector ('par_1'),
NULL,
'(text/html)|(\\*/\\*)',
0,
303,
''
);

DB.DBA.URLREWRITE_CREATE_REGEX_RULE (
'http_rule_3', 1,
'^/dbnary/*\$',
vector (),
0,
'/about-dbnary/lemon/dbnary-doc/index.html',
vector (),
NULL,
'(text/html)|(\\*/\\*)',
0,
303,
''
);

DB.DBA.URLREWRITE_CREATE_REGEX_RULE (
'http_rule_5', 1,
'^/dbnary/*\$',
vector (),
0,
'/about-dbnary/lemon/latest/dbnary.owl',
vector (),
NULL,
'(text/rdf.n3)|(application/rdf.xml)',
0,
303,
''
);
-- Create namespaces for dbnary

DB.DBA.XML_SET_NS_DECL ('lexinfo', 'http://www.lexinfo.net/ontology/2.0/lexinfo#', 2);
DB.DBA.XML_SET_NS_DECL ('lexvo', 'http://lexvo.org/id/iso639-3/', 2);
DB.DBA.XML_SET_NS_DECL ('dcterms', 'http://purl.org/dc/terms/', 2);
DB.DBA.XML_SET_NS_DECL ('lemon', 'http://www.lemon-model.net/lemon#', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary', 'http://kaiko.getalp.org/dbnary#', 2);
DB.DBA.XML_SET_NS_DECL ('olia', 'http://purl.org/olia/olia.owl#', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-fra', 'http://kaiko.getalp.org/dbnary/fra/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-eng', 'http://kaiko.getalp.org/dbnary/eng/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-ita', 'http://kaiko.getalp.org/dbnary/ita/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-rus', 'http://kaiko.getalp.org/dbnary/rus/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-deu', 'http://kaiko.getalp.org/dbnary/deu/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-por', 'http://kaiko.getalp.org/dbnary/por/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-fin', 'http://kaiko.getalp.org/dbnary/fin/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-ell', 'http://kaiko.getalp.org/dbnary/ell/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-tur', 'http://kaiko.getalp.org/dbnary/tur/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-jpn', 'http://kaiko.getalp.org/dbnary/jpn/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-spa', 'http://kaiko.getalp.org/dbnary/spa/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-bul', 'http://kaiko.getalp.org/dbnary/bul/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-pol', 'http://kaiko.getalp.org/dbnary/pol/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-swe', 'http://kaiko.getalp.org/dbnary/swe/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-nld', 'http://kaiko.getalp.org/dbnary/nld/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-lit', 'http://kaiko.getalp.org/dbnary/lit/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-shr', 'http://kaiko.getalp.org/dbnary/shr/', 2);
DB.DBA.XML_SET_NS_DECL ('dbnary-nor', 'http://kaiko.getalp.org/dbnary/nor/', 2);
checkpoint;
commit WORK;
checkpoint;
