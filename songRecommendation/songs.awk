cat mergedSongs.txt | sort -k1|awk '!x[$1]++' >songs.txt
