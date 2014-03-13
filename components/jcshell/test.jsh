
include "inc.jsh";

int main( string args[])
{
	int iArr[][][];
	int n;
	int n1;
	int n2;
	int count = 1;
	int count1 = count;
	int count2 = 3;
	
	--count;
	
	count = incVar( count);
	
	iArr = { { { 1, 2 } } };
	n = 0;
	n1 = 0;
	n2 = 0;
	iArr[0][0][2] = 4;
	iArr[0][0][0] = 5;
	
	switch( count)
	{
	  case 0:
	    write "case 0\n";
	    break;
	    
	  case 1:
	    write "case 1\n";
	    break;
	
	  default:
	    write "default case\n";
	    break;
	}
	
	while( n < count)
	{
	  while( n1 < count1)
	  {
	    while( n2 < count2)
	    {
	      string name = "iArr[" + n + "][" + n1 + "][" + n2 + "]";
	      printInt( name, iArr[n][n1][n2]);
		  //write "Value of iArr[" + n + "][" + n1 + "][" + n2 + "]: " + iArr[n][n1][n2] + "\n";
		  ++n2;
	    }
	    n2 = 0;
		++n1;
	  }
	  n1 = 0;
	  ++n;
	}
	
	write "for loop \n";
	
	for( int i = 0; i < count; ++i)
	{
	  for( int ii = 0; ii < count1; ++ii)
	  {
	    for( int iii = 0; iii < count2; ++iii)
	    {
		  write "Value of iArr[" + i + "][" + ii + "][" + iii + "]: " + iArr[i][ii][iii] + "\n";
		}
	  }
	}
	
	n = 0;
	
	if( n == 0)
	{
	  int b = 10;
	  
	  write "In Block; b: " + b;
	}
	return 0;
}
