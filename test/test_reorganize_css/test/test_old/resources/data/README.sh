cat sparql.descendants.csv | awk '{print "{source:" $1 ", target:" $2 ", type:\"resolved\"},\n{source:" $2 ", target:" $3 ", type:\"resolved\"},\n{source:" $3 ", target:" $4 ", type:\"resolved\"},\n{source:" $4 ", target:" $5 ", type:\"resolved\"},"}' |sort |uniq > data/dolh1.data

cat sparql.edited.csv | awk -F ',' '{print "{source:" $1 ", target:\"" $2 ", type:\"derives_from\"},"}' > sparql.edited.data
