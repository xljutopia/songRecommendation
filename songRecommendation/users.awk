BEGIN{
	FS=",";
	count=0;
	pre=0;
	current=0;
}
{
	current=$2;
	if(pre!=current){
		count++;
		printf("%d\n" ,$2);
		pre=current;
	}
}
END{
	#print "total: " count
}
