package com.example.model.mysql;

import com.example.model.word.Word;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;

public class MysqlDatabaseConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dictionary?characterEncoding=utf8";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "12345678";
    Connection connection = null;

    public MysqlDatabaseConnector() {
    }

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            System.out.println("Connection successful!");
        } catch (Exception getConnectionException) {
            System.out.println("Connection failed!");
            getConnectionException.printStackTrace();
        }

        return this.connection;
    }

    /**
     * Find word with Pattern.
     *
     * @param wordPattern is the Pattern
     * @return Array of found word
     */
    public ArrayList<String> findWordPattern(String wordPattern) {
        ArrayList listofword = new ArrayList();

        try {
            this.getConnection();
            Statement statement = this.connection.createStatement();
            ResultSet resultset = statement.executeQuery(
                    "select * from tbl_edict where word like '" + wordPattern + "%' limit 20");
            if (!resultset.next()) {
                System.out.println("No result");
                return null;
            }

            resultset.beforeFirst();

            while(resultset.next()) {
                listofword.add(resultset.getString("word"));
            }

            this.connection.close();
        } catch (Exception findWordPatternException) {
            findWordPatternException.printStackTrace();
        }

        return listofword;
    }

    /**
     * Find word.
     *
     * @param s is the word to find
     * @return word found
     */
    public Word findWord(String s) {
        Word wordFound = new Word();

        try {
            this.getConnection();
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from tbl_edict where word = '" + s + "'");
            if (!resultSet.next()) {
                System.out.println("No result");
                return null;
            }

            resultSet.beforeFirst();

            while(resultSet.next()) {
                wordFound.setWord_target(resultSet.getString("word"));
                wordFound.setWord_explain(parseDataFromDatabase(resultSet.getString("detail")));
            }

            this.connection.close();
        } catch (Exception findWordException) {
            findWordException.printStackTrace();
        }

        return wordFound;
    }

    /**
     * Add word to the database.
     *
     * @param word is the word to add
     * @return boolean to check if okay
     */
    public boolean addWord(Word word) {
        String insertQuery = "insert into tbl_edict(word, detail) values(?,?)";
        Word wordFound = this.findWord(word.getWord_target());
        boolean flag = false;
        if (wordFound != null) {
            System.out.println("Database contain this word");
            return flag;
        } else {
            try {
                this.getConnection();
                PreparedStatement preparedStatement = this.connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, word.getWord_target());
                preparedStatement.setString(2, "<Q>" + word.getWord_explain() + "</Q>");

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected >= 1) {
                    System.out.println("Add new word successfully");
                    flag = true;
                } else {
                    System.out.println("Add new word failed");
                }

                this.connection.close();
                return flag;
            } catch (Exception addWordException) {
                addWordException.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Delete word from the database.
     *
     * @param word is the word to delete
     * @return boolean to check if okay
     */
    public boolean deleteWord(String word) {
        String deleteQuery = "delete from tbl_edict where word = ?";
        Word wordFound = this.findWord(word);
        boolean flag = false;
        if (wordFound == null) {
            System.out.println("Database not contain this word");
            return flag;
        } else {
            try {
                this.getConnection();
                PreparedStatement preparedStatement = this.connection.prepareStatement(deleteQuery);
                preparedStatement.setString(1, word);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected >= 1) {
                    System.out.println("Delete word successfully");
                    flag = true;
                } else {
                    System.out.println("Delete word failed");
                }

                this.connection.close();
                return flag;
            } catch (Exception deleteWordException) {
                deleteWordException.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Edit word from database.
     *
     * @param word is the word to edit
     * @return boolean to check if okay
     */
    public boolean editWord(Word word) {
        String updateQuery = "update tbl_edict set detail = ? where word = ? ";
        Word word1 = this.findWord(word.getWord_target());
        boolean flag = false;
        if (word1 == null) {
            System.out.println("Database does not contain this word!");
            return flag;
        } else {
            try {
                this.getConnection();
                PreparedStatement statement = this.connection.prepareStatement(updateQuery);
                statement.setString(1, "<Q>" + word.getWord_explain() + "</Q>");
                statement.setString(2, word.getWord_target());

                int rowAffected = statement.executeUpdate();
                if (rowAffected >= 1) {
                    System.out.println("Update word successfully!");
                    flag = true;
                } else {
                    System.out.println("Update word failed!");
                }

                this.connection.close();
                return flag;
            } catch (Exception editWordException) {
                editWordException.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Add word to favorite List.
     *
     * @param word is the word to add
     * @return boolean to check if okay
     */
    public boolean addWordToFavoriteList(String word) {
        this.getConnection();
        boolean flag = false;
        String updateQuery = "update tbl_edict set isFavorite = ? where word = ? ";

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, word);

            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected >= 1) {
                System.out.println("Add word to favorite list successfully");
                flag = true;
            } else {
                System.out.println("Add word to favorite list failed");
            }

            this.connection.close();
            return flag;
        } catch (Exception addWordFavoriteListException) {
            addWordFavoriteListException.printStackTrace();
            return false;
        }
    }

    /**
     * Remove word from favorite List.
     *
     * @param word is the word to remove
     * @return boolean to check if okay
     */
    public boolean removeWordFromFavoriteList(String word) {
        this.getConnection();
        boolean flag = false;
        String updateQuery = "update tbl_edict set isFavorite = ? where word = ? ";

        try {
            PreparedStatement statement = this.connection.prepareStatement(updateQuery);
            statement.setInt(1, 0);
            statement.setString(2, word);
            int rowAffected = statement.executeUpdate();
            if (rowAffected >= 1) {
                System.out.println("Remove word from favorite list successfully");
                flag = true;
            } else {
                System.out.println("Remove word from favorite list failed");
            }

            this.connection.close();
            return flag;
        } catch (Exception removeWordFavoriteListException) {
            removeWordFavoriteListException.printStackTrace();
            return false;
        }
    }

    /**
     * Get List of favorite word.
     *
     * @return ArrayList of favorite Word
     */
    public ArrayList<Word> getListFavoriteWord() {
        ArrayList favoriteWord = new ArrayList();

        try {
            this.getConnection();
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from tbl_edict where isFavorite = '1'");
            if (!resultSet.next()) {
                System.out.println("No result");
                return null;
            }

            resultSet.beforeFirst();

            while(resultSet.next()) {
                Word word = new Word();
                word.setWord_target(resultSet.getString("word"));
                word.setWord_explain(resultSet.getString("detail"));
                favoriteWord.add(word);
            }

            this.connection.close();
        } catch (Exception getListFavoriteWordException) {
            getListFavoriteWordException.printStackTrace();
        }

        return favoriteWord;
    }

    /**
     * Word_explain from database is in xml type.
     * Need to parse Word_explain from xml type to definition (word_explain)
     *
     * @param xml is the word_explain input
     * @return String of definition after parsing
     */
    public String parseDataFromDatabase(String xml) {
        String parse_data = "";
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(xml));
            Document document = documentBuilder.parse(inputSource);
            NodeList nodeList = document.getElementsByTagName("Q").item(0).getChildNodes();

            for(int i = 0; i < nodeList.getLength(); i++) {
                parse_data = parse_data + nodeList.item(i).getTextContent() + "\n";
            }
        } catch (Exception parseDataFromDatabaseException) {
            parseDataFromDatabaseException.printStackTrace();
        }
        return parse_data;
    }

    public static void main(String[] args) {
        MysqlDatabaseConnector mysqlDatabaseConnector = new MysqlDatabaseConnector();
        System.out.println(mysqlDatabaseConnector.findWord("abandons").getWord_explain());
    }
}