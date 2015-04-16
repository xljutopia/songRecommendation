#awk -f count.awk users.txt 20
BEGIN{N=ARGV[2]; print N;}
{system("java -jar songRecommendation_fat.jar " $1 " " N)}
