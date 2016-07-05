
for i in `ls $1_$2/*.res`; do

res=`~/Downloads/trec_eval.9.0/trec_eval -m set $1_ref_gold.out $i`
filt_i=`echo "$i" | sed 's/french_results_//g'|sed 's/.res$//g'`
if [ -z "$3" ]; then
	fres=`echo "$res" | egrep "^(set_F.*|num_r.*|set_P.*|set_recall.*)\s+"| awk '{print $3}'`
	str="$filt_i"
	for measure in `echo "$fres"`; do
		str+=",$measure"
	done
	echo "$str"
else
	fres=`echo "$res" | egrep "^$3.*\s+" | awk '{ print $3}'`
	echo "$filt_i,$fres"
fi
done