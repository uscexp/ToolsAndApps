
package haui.util;

import java.io.*;

/** A simple tokenizer that treats space, tab, new line, line feed as white space and '"' chars can quote
 strings. */

public class Tokenizer
{
	public Tokenizer (Reader in)
	{
		m_Tokenizer = new StreamTokenizer(in);
		m_Tokenizer.resetSyntax();
		m_Tokenizer.wordChars(0, 255);
		m_Tokenizer.quoteChar('"');
		m_Tokenizer.whitespaceChars(' ', ' ');
		m_Tokenizer.whitespaceChars('\t', '\t');
		m_Tokenizer.whitespaceChars('\n', '\n');
		m_Tokenizer.whitespaceChars('\r', '\r');
	}
	
	public Tokenizer (String in)
	{
		this (new StringReader(in));
	}
	
	public boolean hasMoreTokens ()
	{
		if (m_Tokenizer.ttype == StreamTokenizer.TT_EOF)
			return false;
		
		try {m_Tokenizer.nextToken();}
		catch (IOException e) { }
		
		return m_Tokenizer.ttype != StreamTokenizer.TT_EOF;
	}
	
	public String nextToken ()
	{
		return m_Tokenizer.sval;
	}
	
	private StreamTokenizer m_Tokenizer;
}
