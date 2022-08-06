package model.dictionary;

import model.word.Word;

import java.util.ArrayList;
import java.util.Scanner;

public class DictionaryCommandline {
	Dictionary dictionary;
    DictionaryManagement dictionaryManagement;
    
    public Dictionary getDictionary() {
        return dictionary;
    }
    
    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }
    
    public DictionaryManagement getDictionaryManagement() {
        return dictionaryManagement;
    }

    public void setDictionaryManagement(DictionaryManagement dictionaryManagement) {
        this.dictionaryManagement = dictionaryManagement;
    }

    public DictionaryCommandline() {
    	
    }
    
    public DictionaryCommandline(Dictionary dictionary, DictionaryManagement dictionaryManagement) {
        this.dictionary = dictionary;
        this.dictionaryManagement = dictionaryManagement;
    }
    
	public void showAllWords() {
        ArrayList<Word> words = dictionary.getWords();
        System.out.println("No\t" + "|English\t\t" + "|Vietnamese");
        for (int i = 0; i < words.size(); i++) {
            System.out.println(i + 1 + "\t|" + words.get(i).getWord_target()
                    +"\t\t\t|"+ words.get(i).getWord_explain());
        }
        System.out.println();
    }
	
	public void dictionaryBasic() {
        dictionaryManagement.insertFromCommandline(dictionary);
        this.showAllWords();
    }
	
	public void dictionaryAdvanced() {
		String inFile = "D:\\OOP\\eclipse-workspace\\Dictionary\\src\\model\\data\\dictionaries.txt";
        dictionary.setWords(dictionaryManagement.insertFromFile(inFile));
        showAllWords();
        
        System.out.print("Enter word to lookup: ");
        Scanner scanner = new Scanner(System.in);
		String word_target = scanner.nextLine();
        Word word = dictionaryManagement.dictionaryLookup(dictionary, word_target);
        scanner.close();
        
        if (word != null) {
        	System.out.println(word.getWord_explain());
        	return;
        }
        System.out.println("Word not found");
    }

    public ArrayList dictionarySearcher(String x) {
        ArrayList<Word> words = dictionary.getWords();
        ArrayList<String> arr = new ArrayList<>();
        for(int i = 0; i< words.size(); i++) {
            if(words.get(i).getWord_explain().contains(x)) {
                arr.add(words.get(i).getWord_explain());
            }
        }
        return arr;
    }
	
	public static void main(String[] args) {
		String inFile = "D:\\OOP\\eclipse-workspace\\Dictionary\\src\\model\\data\\dictionaries.txt";
		
		Dictionary dictionary = new Dictionary();
		DictionaryManagement dictionaryManagement = new DictionaryManagement();
		dictionary.setWords(dictionaryManagement.insertFromFile(inFile));
		DictionaryCommandline dictionaryCommandline = new DictionaryCommandline(dictionary, dictionaryManagement);
		
		dictionaryManagement.modifyDictionary(dictionary);
		dictionaryManagement.dictionaryExportToFile(dictionary, "D:\\OOP\\eclipse-workspace\\Dictionary\\src\\model\\data\\dictionaries.txt");;
		dictionaryCommandline.dictionaryAdvanced();
    }
}
