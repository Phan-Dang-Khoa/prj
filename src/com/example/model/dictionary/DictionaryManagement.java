package com.example.model.dictionary;

import com.example.model.word.Word;

import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class DictionaryManagement {

    public void insertFromCommandline(Dictionary dictionary) {
        System.out.print("Enter the number of word input: ");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        for(int i = 0; i < n; i++) {
            System.out.print("Word "+ i +" target:");
            String word_target = scanner.nextLine();
            System.out.print("Word "+ i +" explain:");
            String word_explain = scanner.nextLine();
            Word word = new Word(word_target, word_explain);
            addWordToDictionary(dictionary, word);
        }
        scanner.close();
    }

    public ArrayList<Word> insertFromFile(String path) {
        ArrayList<Word> words = new ArrayList<>();
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            while ((line = bufferedReader.readLine()) != null) {
                String tok[] = line.split("\t");
                String target = tok[0];
                String explain = tok[1];
                Word word = new Word(target, explain);
                words.add(word);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }

    public void addWordToDictionary(Dictionary dictionary, Word newWord) {
        if(dictionary.getWords().indexOf(newWord) == -1) {
            dictionary.getWords().add(newWord);
        } else {
            System.out.println("This word has exist");
        }
        return;
    }

    public void removeWordFromDictionary(Dictionary dictionary, Word word) {
        if(dictionary.getWords().indexOf(word)==-1)
        {
            return;
        } else {
            dictionary.getWords().remove(word);
        }
    }

    public Word dictionaryLookup(Dictionary dictionary, String w) {
        for (Word word: dictionary.getWords()) {
            if (word.getWord_target().equalsIgnoreCase(w)) {
                return word;
            }
        }
        return null;
    }

    public void modifyDictionary(Dictionary dictionary) {
        System.out.println("Enter your command: \n 1. add \n 2. remove \n 3. edit \n");
        Scanner cmdScanner = new Scanner(System.in);
        Scanner wtScanner = new Scanner(System.in);
        Scanner weScanner = new Scanner(System.in);

        int cmd = cmdScanner.nextInt();

        System.out.print("Enter word target:");
        String word_target = wtScanner.nextLine();

        if(cmd == 1) {
            System.out.print("Enter explain:");
            String word_explain = weScanner.nextLine();
            Word word = new Word(word_target, word_explain);
            addWordToDictionary(dictionary, word);
        } else if (cmd == 2) {
            Word word = dictionaryLookup(dictionary, word_target);
            if(word != null) {
                removeWordFromDictionary(dictionary, word);
            }
        } else if (cmd == 3) {
            Word word = dictionaryLookup(dictionary, word_target);
            if (word != null) {
                System.out.print("Enter explain:");
                String word_explain = weScanner.nextLine();
                word.setWord_explain(word_explain);
            }
            System.out.println("Word not found");
        }
        cmdScanner.close();
        wtScanner.close();
        weScanner.close();
        return;
    }

    public void dictionaryExportToFile(Dictionary dictionary, String path) {
        try {
            File outFile = new File(path);
            Writer writer = new FileWriter(outFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (Word word: dictionary.getWords()) {
                bufferedWriter.write(word.getWord_target() + "\t" + word.getWord_explain() + "\n");
            }
            bufferedWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}