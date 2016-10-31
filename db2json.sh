_search=$1
_database="wiktionary_db.txt"
_head=`head -1 $database`
_arr=(`echo $_head | tr "\t" "\n"`)
_field0=${_arr[0]} 
_field1=${_arr[1]}
_field2=${arr[2]}
_field3=${arr[3]}
_field4=(`echo ${arr[4]} |  tr "," "\n"`)

_lines=(`tail -n +2 $_database | awk '{print $2}' | grep -nw $_search | cut -f1 -d:`)
_ids=(`for i in ${_lines[@]}; do
	sed -n "$i"p $_database | awk 'BEGIN{FS="\t"}{print $1"\n"$7}' 
done | sort | uniq`) 
for i in ${_ids}; do
	tail -n +2 $_database | grep 
_lines=(`tail -n +2 $_database | awk '{print $2}' | grep -nw $_search | cut -f1 -d:`)
_connections=(`cat tmp/tmp | awk 'BEGIN{OFS="\n"}{print "\""$1";","\""$5}' | sort`)

	