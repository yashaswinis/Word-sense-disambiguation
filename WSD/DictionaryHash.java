package com.cornell.edu;

	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import edu.smu.tspell.wordnet.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.washington.cs.knowitall.morpha.MorphaStemmer;

public class DictionaryHash 
{
    //Dictionary word extraction goes here
	public Map<WordSense, List<String>> dictionaryMap;
	public Map<Integer, Float> secondSensePredictionMap;
	static List<String> Context = new ArrayList<String>();
	List<Float> probabilityScore = new ArrayList<Float>();
	public String correctSense;
	public int noOfLines;
	float probabilityTotal;
	public float accuracy;
	public static String targetword = null;
	public DictionaryHash(String dictionaryPath) throws ParserConfigurationException, SAXException, IOException 
	{
		dictionaryMap = new HashMap<WordSense, List<String>>();
		File dictionaryFile = new File(dictionaryPath);
		//BufferedWriter br1 = new BufferedWriter(new FileWriter("results_dict.txt"));
		int entryNo=0;
		
		WordSense[] wordsenseArray = new WordSense[700];
		List<String> def= new ArrayList<String>();
		String WordNetList;
		List<String> DictMinusStopWords= new ArrayList<String>();
		List<String> DictLemmatizedWords= new ArrayList<String>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document dictXml = builder.parse(dictionaryFile);
				
		NodeList allEntries = dictXml.getElementsByTagName("lexelt");
		//XML dictionary parsing
		for (int x = 0; x < allEntries.getLength(); x++) 
		{
			wordsenseArray[entryNo]= new WordSense();
			Element wordElement = (Element) allEntries.item(x);
            
			wordsenseArray[entryNo].word = (String)wordElement.getAttribute("item");
		
			String[] words1= (wordsenseArray[entryNo].word).split("\\.");
			NounSynset nounSynset;
			VerbSynset verbSynset;
			Synset[] synsets; 
			List<String> synonym= new ArrayList<String>();
			
			System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");   
		    WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		     if(words1[1].contains("n")){
		    	 synsets = database.getSynsets(words1[0],SynsetType.NOUN);	  
		     }else {
		    	 synsets = database.getSynsets(words1[0],SynsetType.VERB);
		     }
		     //Add synset/hyper/hyponyms and glossary to target word
		    for (int i = 0; i < synsets.length; i++) { 
		        if(words1[1].contains("n")) {
		        	String synlisttrack = "\0";
		        	String hyperlisttrack = "\0";
		        	String hypolisttrack = "\0";
		        	String merolisttrack = "\0";
		        	String merolisttrack1 = "\0";
		        	String topiclisttrack ="\0";
		        	String usagelisttrack ="\0";
		        	
		        	nounSynset = (NounSynset)(synsets[i]);
		        	String syn1 = nounSynset.getDefinition();
		        	String[] syn =nounSynset.getWordForms();
		        	NounSynset[] hypo = nounSynset.getHyponyms();
		        	NounSynset[] hyper = nounSynset.getHypernyms();
		        	NounSynset[] mero = nounSynset.getSubstanceMeronyms();
		        	NounSynset[] mero1 =  nounSynset.getPartMeronyms();
		        	NounSynset[] topic =  nounSynset.getTopics();
		        	NounSynset[] usage =  nounSynset.getUsages();
		        	
		        	for(String synlist:syn){
		        		    synlisttrack += " "+synlist;
		        	}
		    
		        	for(NounSynset hyperlist:hyper){
		        		hyperlisttrack += " "+hyperlist;
		        		hyperlisttrack += " "+hyperlist.getDefinition();
		    	    }
		        	
		        	for(NounSynset hypolist:hypo){
		        		hypolisttrack += " "+hypolist;
	        		    hypolisttrack += " "+hypolist.getDefinition();
	        	    }
		        	
		        	for(NounSynset merolist:mero){
		        		merolisttrack += " "+merolist;
	        		    merolisttrack += " "+merolist.getDefinition();
	        		}
		        	
		        	for(NounSynset merolist1:mero1){
		        		merolisttrack1 += " "+merolist1;
	        		    merolisttrack1 += " "+merolist1.getDefinition();
	        		}
		        	
		        	for(NounSynset topiclist:topic){
		        		
	        		    topiclisttrack += " "+topiclist;
	        		    topiclisttrack += " "+topiclist.getDefinition();
	        		}
		        	for(NounSynset usagelist:usage){
		        		
		        		usagelisttrack += " "+usagelist;
		        		usagelisttrack += " "+usagelist.getDefinition();
	        		}
		        	String addStrings =syn1+synlisttrack+hyperlisttrack+hypolisttrack+merolisttrack+merolisttrack1+topiclisttrack+usagelisttrack;
		        	synonym.add(addStrings);
		        } else {
		        	String synlisttrack = "\0";
		        	String hyperlisttrack = "\0";
		        	String typolisttrack = "\0";
		        	String entaillisttrack = "\0";
		        	String outcomelisttrack = "\0";
		            verbSynset = (VerbSynset)(synsets[i]);
		            String syn1 = verbSynset.getDefinition();
		            String[] syn =verbSynset.getWordForms();
		            VerbSynset[] typo = verbSynset.getTroponyms();
		        	VerbSynset[] hyper = verbSynset.getHypernyms();
		        	VerbSynset[] entail = verbSynset.getEntailments();
		        	VerbSynset[] outcome = verbSynset.getOutcomes();
		        	
		        	for(String synlist:syn){
		        		    synlisttrack += " "+synlist;
		        			
		        	}
		        	for(VerbSynset hyperlist:hyper){
		        		hyperlisttrack += " "+hyperlist;
		        		hyperlisttrack += " "+hyperlist.getDefinition();
	        			
	        	    }
		        	for(VerbSynset typolist:typo){
		        		typolisttrack += " "+typolist;
	        		    typolisttrack += " "+typolist.getDefinition();
	        			
	        	    }
		        	
		        	for(VerbSynset entailmentlist:entail){
		        		
	        		    entaillisttrack += " "+entailmentlist;
	        		    entaillisttrack += " "+entailmentlist.getDefinition();
	        	    }
		        	for(VerbSynset outcomelist:outcome){
	        		    outcomelisttrack += " "+outcomelist;
	        		    outcomelisttrack += " "+outcomelist.getDefinition();
	        	    }
		        	
		        	String addStrings =syn1+synlisttrack+hyperlisttrack+typolisttrack+entaillisttrack+outcomelisttrack;
		        	synonym.add(addStrings);
		            
		        }
		        
		    }
			NodeList senseList = wordElement.getElementsByTagName("sense");
			
			for (int j = 0; j < senseList.getLength(); j++) {
				def.clear();
				
				wordsenseArray[entryNo]=new WordSense();
				
				Element sense1 = (Element) senseList.item(j);
			
				String definition = (String)sense1.getAttribute("gloss");
				wordsenseArray[entryNo].word = wordElement.getAttribute("item");
				wordsenseArray[entryNo].sense = sense1.getAttribute("id");
				WordNetList = sense1.getAttribute("wordnet");
				String wordnetidlist[] = null ;
				
				if(WordNetList!=null && !WordNetList.isEmpty()){
					wordnetidlist = WordNetList.split(",");	
				}
				
				int id;
			    if(wordnetidlist!=null){
					for( String word: wordnetidlist){
					    id=Integer.parseInt(word);
						def.add(synonym.get(id-1));
					}
				}
				
				StringTokenizer st = new StringTokenizer(definition); 
				while (st.hasMoreTokens()) {
					def.add(st.nextToken());
				}
				
				
				DictMinusStopWords=RemoveStopWords(def);
				DictLemmatizedWords=Lemmatize(DictMinusStopWords);
				
				dictionaryMap.put(wordsenseArray[entryNo], DictLemmatizedWords);
				entryNo++;	
			    
			}
		    
		}
	    /*
     	WordSense word_temp=new WordSense();
		List<String> list_temp;
		for (Map.Entry<WordSense, List<String>> entry : dictionaryMap.entrySet()) {
			System.out.println("");
			System.out.println("");
		    word_temp=entry.getKey();
		    list_temp=entry.getValue();
		    }	*/
	  
	}
	
	public void ReadData(String filename) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		BufferedWriter br1 = new BufferedWriter(new FileWriter("results.csv"));
		String sCurrentLine,testWord =null;
		noOfLines=0;
	    //Retrieve the context words list surrounding the target word
		int contextid;
		
		while ((sCurrentLine = br.readLine()) != null) {
				 noOfLines=noOfLines+1;
				 List<String> ContextWords = new ArrayList<String>();
				 ContextWords = PatternMatch(sCurrentLine);   
	             Pattern p = Pattern.compile("^(\\w+\\.\\w)\\s+\\|\\s+(\\d+)\\s+\\|\\s+(.*)");
				 Matcher m;
				 
				 m = p.matcher(sCurrentLine);
				 if (m.find()) {
					testWord=m.group(1);
					targetword = m.group(1);
					correctSense=m.group(2);
				  }
				 
				 //Exclude stop words in the context list
				  Context = RemoveStopWords(ContextWords);
				 //Lemmatize the words in the context list
				 Context = Lemmatize(Context);
			   	 System.out.println("Context is "+ Context);
			   	 //POSTag the words
			   	 //Context = POSTag(Context);
			   	 //Keep only 4 words - Window size - 2 
			   	 Context = ReturnContextWords(Context);
			   	 //Add definitions
			   	 Context = AddDefntoContextWord(Context);
			   	 Context = RemoveStopWords(Context);
				 Context = Lemmatize(Context);
				 
				 contextid=FindContext(testWord,Context);
				 
				 br1.write(contextid +"\n");
			     		   	 
		}//End while
		
		//Finding the accuracy for soft score - Need to pass the validation data or else correct sense will be zero
		for (Float value : probabilityScore) {
			System.out.println("Value is " + value);
			probabilityTotal = probabilityTotal + value; 
		}
		//System.out.println("Probabilty total is "+ probabilityTotal);
		//System.out.println("number of lines is"+ noOfLines);
		accuracy=(float)probabilityTotal/(float)noOfLines * 100;
		System.out.println("Soft score Accuracy is "+accuracy);
		br1.close();
	 }
    
	public int FindContext(String testWord, List<String> Context) throws IOException
	{
		Map<Integer, Integer> weightMap = null;
		Map.Entry<Integer, Integer> maxEntry=null;
		Map.Entry<Integer, Float> maxEntry2=null;
		weightMap = new HashMap<Integer, Integer>();
		WordSense wordsensetemp = new WordSense();
		List<String> DictList = new ArrayList<String>();
		List<String> compareList = new ArrayList<String>();
		List<String> IntersectList = new ArrayList<String>();
		int size, consecutiveCount, Contextsize, DictListSize;
		String ContextString1,DictListString1,IntersectListString1, CompareListString1;
		int total=0,weight=0;
		
		for(int i=1;;i++)
		{
			consecutiveCount=0;
			wordsensetemp.word=testWord;
			wordsensetemp.sense=String.valueOf(i);
			
			DictList=dictionaryMap.get(wordsensetemp);
		
			if(DictList==null)
			{
		
				for (Integer value : weightMap.values()) {
					 total = total + value; 
				}
				for (Map.Entry<Integer, Integer> entry : weightMap.entrySet()) {
					System.out.println("");
					System.out.println("");
				    System.out.println("Key Word is "+entry.getKey());
				    System.out.println("Value is "+entry.getValue());
					
				}	
				//Logic for soft scoring
				
				//System.out.println("Correct Sense is "+correctSense);
			 	//System.out.println("Total is  "+total);
				
				//System.out.println("Weightmap value is "+weightMap.get(Integer.parseInt(correctSense)));
				/*weight=weightMap.get(Integer.parseInt(correctSense));
				float avg=(float)weight/(float)total;
				//System.out.println("Avg  is "+avg);
				probabilityScore.add(avg);*/
				break;
			}
			
			IntersectList=intersect(DictList,Context);
			
			//DictListString=DictList.toString();
			//System.out.println("DictListString is "+DictListString);
			//ContextString=Context.toString();
			//System.out.println("ContextString is "+ContextString);
			StringBuilder ContextString = new StringBuilder();
			StringBuilder DictListString = new StringBuilder();
			
			
			Contextsize=Context.size();
			for(int m=0; m<Contextsize; m++)
			{
				ContextString.append(Context.get(m));
			}
			ContextString1=ContextString.toString();
			//System.out.println("ContextString is "+ContextString1);
			
			DictListSize=DictList.size();
			
			for(int n=0; n<DictListSize; n++)
			{
				DictListString.append(DictList.get(n));
			}
			DictListString1=DictListString.toString();
			
			size=IntersectList.size();
			for(int j=0; j<size-1; j++){
				StringBuilder CompareListString = new StringBuilder();
				CompareListString1=null;
				compareList.clear();
				
				CompareListString.append(IntersectList.get(j));
				CompareListString.append(IntersectList.get(j+1));
				CompareListString1=CompareListString.toString();
				if(DictListString1.contains(CompareListString1) && ContextString1.contains(CompareListString1)){
					consecutiveCount=consecutiveCount+2;
				}
			}
			
			weightMap.put(i, IntersectList.size());
			//Logic for extension 2
			/*float value=((IntersectList.size()+consecutiveCount)/DictListSize);
			 secondSensePredictionMap.put(i,value);*/
		}
		
		/*
		for (Entry<Integer, Float> entry : secondSensePredictionMap.entrySet()) {
			System.out.println("");
			System.out.println("");
		    System.out.println("Key Word is "+entry.getKey());
		    System.out.println("Value is "+entry.getValue());
			
		}	
		
		for (Map.Entry<Integer, Float> entry : secondSensePredictionMap.entrySet())
		{
		    if (maxEntry2 == null || entry.getValue().compareTo(maxEntry2.getValue()) > 0)
		    {
		    	//System.out.println("came inside");
		        maxEntry2 = entry;
		        
		    }
		}
		
		System.out.println("Second best sense is: "+maxEntry2.getKey());
		*/
		for (Map.Entry<Integer, Integer> entry : weightMap.entrySet())
		{
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
		    {
		    	//System.out.println("came inside");
		        maxEntry = entry;
		        
		    }
		}
		System.out.println("The maximum in the hash is"+ maxEntry.getKey());
     	return maxEntry.getKey();
			
	}
	
	private List<String> intersect(List<String> A, List<String> B) {
	    List<String> rtnList = new LinkedList<>();
	    for(String dto : A) {
	        if(B.contains(dto)) {
	            rtnList.add(dto);
	        }
	    }
	    return rtnList;
	}

	static List<String> PatternMatch(String sCurrentLine) {
		
		List<String> tokensVal = new ArrayList<String>();
		List<String> context = new ArrayList<String>();
		
		//Retrieve the example 
		Pattern p = Pattern.compile("^(\\w+\\.\\w)\\s+\\|\\s+(\\d+)\\s+\\|\\s+(.*)");
		//Retrieve the context words surrounding the target word
		Pattern pContext = Pattern.compile("(.*)\\%%(.*)\\%%(.*)");
		Matcher m,mContext;
		
		m = p.matcher(sCurrentLine);
		if (m.find()) {
			tokensVal.add(m.group(3));			
		}			
		
		for(String tokenContext:tokensVal){
  		  mContext = pContext.matcher(tokenContext); 
		  if(mContext.find()){
			  String tw = "deadbeef";
			  context.add(mContext.group(1)+tw+ mContext.group(3));
		  }		
		}
		System.out.println("In PM, context is"+ context);
		return context;
	 }
	
	 static List<String> RemoveStopWords(List<String> ContextWords){
		String token;
		List<String> ContextWords1 = new ArrayList<String>();
		for(String word:ContextWords){
		 //System.out.println("word is "+ word);
		 StringTokenizer tokenizer = new StringTokenizer(word);
		 //System.out.println("tokenizer is "+ tokenizer.toString());
		 while (tokenizer.hasMoreTokens()) {
			 token = tokenizer.nextToken();
			 
			 if(!weka.core.Stopwords.isStopword(token)){
				 //	 System.out.println("stop word is "+ token);
				 String[] words = token.replaceAll("(\\p{P})|(\\``)|(\\`)|(\\d+)|(\\$)|(Verb|Noun|Adverb|Adjective)", " ").toLowerCase().split("\\s+");
		   	     for(String word1:words){
		   	    	 //		System.out.println("After removing punctuations"+ word1);
				    if(!word1.matches("^(\\w)||^(\\s)||^(\\w\\w)")){
				    	ContextWords1.add(word1);	
				    }
				 }	 //end for
			  } //end inner if
		   } //end while
		}//end main for
	
		return ContextWords1;
		
	 }
	
	static List<String> Lemmatize(List<String>ContextWords) {
		List<String> ContextWords1 = new ArrayList<String>();
		for(String word:ContextWords){
			new MorphaStemmer();
	 	    String morpha = MorphaStemmer.morpha(word,false);
			ContextWords1.add(morpha);
		}
	
		return ContextWords1;
		
	}
	static List<String> POSTag(List<String> ContextWord){
    	MaxentTagger tagger = new MaxentTagger("C:\\Users\\rems\\Documents\\NLP\\p2\\stanford-postagger-2014-01-04\\stanford-postagger-2014-01-04\\models\\english-left3words-distsim.tagger");
    	List<String> List = new ArrayList<String>(); 
    	// The tagged string
    	for(String token:ContextWord){
    		List.add(tagger.tagString(token));
    	}    	 
       	return List;
    }
	
    static List<String> ReturnContextWords(List<String> ContextWord){
    	List<String> List1 = new ArrayList<String>();
    	List<String> List2 = new ArrayList<String>();
    	List<String> context = new ArrayList<String>();
    	Boolean deadbeef = false;
    	for(String context1:ContextWord){
    		if(context1.contains("deadbeef")){
    			deadbeef = true;
    		}
    		else if(!deadbeef){
    			List1.add(context1);
    		} else{
    		List2.add(context1);
    		}
    	}
    			
			int len = List1.size()-1;
			int exception = len - 2;
			if( exception < 0) {
				   exception = 0;
			}
		    for(int i=len;i>exception;i--){
		    	 context.add(List1.get(i));
		    }
			    
		    len = List2.size()-1;
		    exception = 2;
			if( len < exception) {
				   exception = len;
			}
		    for(int i=0;i<exception;i++){
		    	 context.add(List2.get(i));
		    }
		
		return context;
    }
    
    static List<String> AddDefntoContextWord(List<String> ContextWord) {
		NounSynset nounSynset;
		VerbSynset verbSynset;
		AdjectiveSynset adjectiveSynset;
		AdverbSynset adverbSynset;
		SynsetType type = null;
		
		Synset[] synsets = null; 
		List<String> def= new ArrayList<String>();
		
		
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");   
	    WordNetDatabase database = WordNetDatabase.getFileInstance();
	    
	    for(String word:ContextWord){
	
	    	def.add(word);
	    	String[] words1= targetword.split("\\.");
			System.out.println("word is "+word);
	         if(words1[1].contains("n")){
	
		    	 synsets = database.getSynsets(word,SynsetType.NOUN);	 
		    	 type = SynsetType.NOUN;
		     }else {
		    	 synsets = database.getSynsets(word,SynsetType.VERB);
		    	 type = SynsetType.VERB;
		     }
	    		        
	    for (int i = 0; i < synsets.length; i++) { 
	             if(type == SynsetType.NOUN) {
	  	        	String synlisttrack = "\0";
	  	        	String hyperlisttrack = "\0";
	  	        	String hypolisttrack = "\0";
	  	        	nounSynset = (NounSynset)(synsets[i]);
	  	        	String syn1 = nounSynset.getDefinition();
	  	        	//System.out.println("Syn is"+syn1);
	  	        	String[] syn =nounSynset.getWordForms();
	  	        	NounSynset[] hypo = nounSynset.getHyponyms();
	  	        	NounSynset[] hyper = nounSynset.getHypernyms();
	  	        	NounSynset[] mero = nounSynset.getSubstanceMeronyms();
		        	NounSynset[] mero1 =  nounSynset.getPartMeronyms();
		        	NounSynset[] topic =  nounSynset.getTopics();
		        	NounSynset[] usage =  nounSynset.getUsages();
		        	String merolisttrack = "\0";
		        	String merolisttrack1 = "\0";
		        	String topiclisttrack ="\0";
		        	String usagelisttrack ="\0";
		        	
	  	        	for(String synlist:syn){
	          		    synlisttrack += " "+synlist;
	          			//System.out.println("Noun syn and word form is "+synlisttrack);
	          	    }
	  	        	for(NounSynset hyperlist:hyper){
	          		    hyperlisttrack += " "+hyperlist;
	          			//System.out.println("Hyper is "+hyperlisttrack);
	          	    }
	  	        	for(NounSynset hypolist:hypo){
	          		    hypolisttrack += " "+hypolist;
	          			//System.out.println("Hypo is "+hypolisttrack);
	          	    }
	  	        	/*for(NounSynset merolist:mero){
		        		merolisttrack += " "+merolist;
	        		    //merolisttrack += " "+merolist.getDefinition();
	        			//System.out.println("Hypo is "+hypolisttrack);
	        	    }
		        	for(NounSynset merolist1:mero1){
		        		merolisttrack1 += " "+merolist1;
	        		    //merolisttrack1 += " "+merolist1.getDefinition();
	        			//System.out.println("Hypo is "+hypolisttrack);
	        	    }
		        	for(NounSynset topiclist:topic){
		        		
	        		    topiclisttrack += " "+topiclist;
	        		    //topiclisttrack += " "+topiclist.getDefinition();
	        			//System.out.println("Hypo is "+hypolisttrack);
	        	    }*//*
		        	for(NounSynset usagelist:usage){
		        		
		        		usagelisttrack += " "+usagelist;
		        		//usagelisttrack += " "+usagelist.getDefinition();
	        			//System.out.println("Hypo is "+hypolisttrack);
	        	    }*/
	  	        	String addStrings = synlisttrack+hyperlisttrack+hypolisttrack+merolisttrack+merolisttrack1+topiclisttrack+usagelisttrack;;
	  	        	def.add(syn1);
	  	            def.add(addStrings);
	  	    
	          } else if(type == SynsetType.VERB){
	        	  String synlisttrack = "\0";
		        	String hyperlisttrack = "\0";
		        	String hypolisttrack = "\0";
		        	String entaillisttrack = "\0";
		        	String outcomelisttrack = "\0";
		        
	            verbSynset = (VerbSynset)(synsets[i]);
	            String syn1 = verbSynset.getDefinition();
	            String[] syn =verbSynset.getWordForms();
	        	VerbSynset[] hypo = verbSynset.getTroponyms();
	        	VerbSynset[] hyper = verbSynset.getHypernyms();
	        	VerbSynset[] entail = verbSynset.getEntailments();
	        	VerbSynset[] outcome = verbSynset.getOutcomes();
	        	
	        	for(String synlist:syn){
      		    synlisttrack += " "+synlist;
      		    //   System.out.println("Noun syn and word form is "+synlisttrack);
      	      }
	        	for(VerbSynset hyperlist:hyper){
      		    hyperlisttrack += " "+hyperlist;
      		   //   System.out.println("Hyper is "+hyperlisttrack);
      	        }
	        	for(VerbSynset hypolist:hypo){
      		     hypolisttrack += " "+hypolist;
      		    // System.out.println("Hypo is "+hypolisttrack);
      	        }
	        	/*for(VerbSynset entailmentlist:entail){
	        		
        		    entaillisttrack += " "+entailmentlist;
        		    
        	    }
	        	for(VerbSynset outcomelist:outcome){
        		    outcomelisttrack += " "+outcomelist;
        		
        	    }*/
	        	//br1.write("Verb syn and word form is "+synlisttrack+"\n");
	        	//br1.write("Hyper is "+hyperlisttrack+"\n");
	        	
	        	       
	        	String addStrings = synlisttrack+hyperlisttrack+hypolisttrack+entaillisttrack+outcomelisttrack;
	        	def.add(syn1);
	            def.add(addStrings);
	           } else if(type == SynsetType.ADJECTIVE){
		        adjectiveSynset = (AdjectiveSynset)(synsets[i]);
		        String syn1 = adjectiveSynset.getDefinition();
		        def.add(syn1);              		   
		      } else if(type == SynsetType.ADVERB){
			    adverbSynset = (AdverbSynset)(synsets[i]);
			    String syn1 = adverbSynset.getDefinition();
			    def.add(syn1);              		   
		      } else {
		    	 def.add((synsets[i]).getDefinition());
		      }
	        } 
	      
	    }		
		return def;
	}

}
	