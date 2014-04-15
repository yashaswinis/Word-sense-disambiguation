package com.cornell.edu;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class dict
{
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		try
		{
		DictionaryHash hash= new DictionaryHash("C:\\Users\\rems\\Documents\\NLP\\p2\\dictionary.xml");
		hash.ReadData("C:\\Users\\rems\\Documents\\NLP\\p2\\test_data.data");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
