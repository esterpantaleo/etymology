#ETYMOLOGY
##################from excerpt extract languages
cd a;
(grep -o '[A-Z][a-z]*' excerpt.txt; 
grep -o '[A-Z][a-z]*[ |$][A-Z][a-z]*' excerpt.txt;
grep -o '[A-Z][a-z]*[ |$][A-Z][a-z]*[ |$][A-Z][a-z]*' excerpt.txt;
grep -o '[A-Z][a-z]*[ |$][A-Z][a-z]*[ |$][A-Z][a-z]*[ |$][A-Z][a-z]*' excerpt.txt) > unfiltered_languages

#manually filter file unfiltered_languages

WIKTIONARY ENGLISH ETYMOLOGY
#
cat ester | sed -n "/Edit section: Etymology/,/<\/p>/p" | sed -e 's/<[^><]*>//g' | grep -v tymology > ester_etymology


WIKTIONARY FRENCH ETIMOLOGY
https://fr.wiktionary.org/wiki/romain
#extract etymology definition from wiktionary

cat romain | sed -n "/titreetym/,/<\/dd>/p" | sed -e 's/<[^><]*>//g' | grep -v tymologie > romain_etymologie


WIKTIONARY ITALIAN ETYMOLOGY 
https://it.wiktionary.org/wiki/latino
cat latino | sed -n '/title="derivazione">Derivazione/,/<\/p>/p' |  sed -e 's/<[^><]*>//g' | grep -v Derivazione | grep -v modifica > latino_etimologia

#extract all italics inside etymology def (remove new lines)
cat virgulto | sed -n '/title="derivazione">Derivazione/,/<\/p>/p'  | awk -v FS="(<i>|<\/i>)" -v OFS="\n" '{print $2,$4,$6,$8,$10}' | sed -e 's/<[^><]*>//g' | grep -v  "^$"


----------------------------------

FRENCH ETYMOLOGY http://www.cnrtl.fr/portailindex/ETYM//A
#extract name tags from web page
wget http://www.cnrtl.fr/portailindex/ETYM//A > A.1
cat A.1 | grep "Liste des formes" | grep -Po '".*?"' | grep Etymologie | awk '{print $3}' | sed 's/.$//' | grep a  | grep -v "^a$" > french_excerpt.txt
-------------------------------------
