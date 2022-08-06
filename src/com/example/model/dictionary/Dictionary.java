package com.example.model.dictionary;

import com.example.model.word.Word;

import java.util.ArrayList;

public class Dictionary {
    private ArrayList<Word> words;

    public ArrayList<Word> getWords() {
        return words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    public Dictionary(ArrayList<Word> words) {
        this.words = words;
    }

    public Dictionary() {
    }

}
