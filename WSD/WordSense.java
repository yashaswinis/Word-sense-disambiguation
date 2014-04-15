package com.cornell.edu;

import java.lang.Object;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class WordSense 
{
	public String word;
	public String sense;
	
	public WordSense()
	{
		word="";
		sense="";
	}
	public String GetWord()
	{
		return this.word;
	}
	public String GetSense()
	{
		return this.sense;
	}
	
	public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(word).
            append(sense).
            toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof WordSense))
            return false;

        WordSense rhs = (WordSense) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            append(word, rhs.word).
            append(sense, rhs.sense).
            isEquals();
    }
}