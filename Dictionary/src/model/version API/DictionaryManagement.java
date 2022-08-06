import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

public class DictionaryManagement {
    private Connection con = null;

    public void connectDatabase() {
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/oop", "root", "");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String dictionaryLookup (String s) {
        try {
            String sql = "SELECT * FROM `tbl_edict` WHERE word LIKE ' " + s + " ' " ;
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String tem = resultSet.getString("detail");
            int x = tem.lastIndexOf("@", tem.length() / 2);
            int y = tem.lastIndexOf("@");
            tem = tem.substring(x, y);

            String a = tem.replace("<br />- ", " \n");
            a = a.replace("<br />*", "\n" + "\n");
            a = a.replace("<br />=", "\n" + "VD: ");
            a = a.replace("<br />!", " \n");
            a = a.replace("<br />", " \n");
            a = a.replace('+', ':');


            return a;
        } catch (Exception e) {
            return "Not find";
        }
    }

    public String addWord(String s1, String s2) {
        if (!dictionaryLookup(s1).equals("Not find")) {
            return "\"" + s1 + "\"" + " has already" + "\n" + "been in dictionary!";
        } else {
            String sql = "INSERT INTO tbl_edict(word, detail) VALUES(?,?)";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, s1);
                pstmt.setString(2, s2);
                pstmt.executeUpdate();
                return "\"" + s1 + "\"" + " has been added!";
            } catch (SQLException e) {
                return e.getMessage();
            }
        }
    }

    public String deleteWord(String s) {
        if (dictionaryLookup(s).equals("Not find")) return "\"" + s + "\"" + " is not" + "\n" + "in dictionary";
        String sql = "DELETE from tbl_edict WHERE word LIKE '" + s + "'";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.executeUpdate();
            return "\"" + s + "\"" + " has" + "\n" + "been removed";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String editWord(String old, String new_en, String new_vi) {
        if (dictionaryLookup(old).equals("Not find")) return "\"" + old + "\"" + " is" + "\n" + "not in dictionary";
        try {
            if (new_en.equals("")) {
                String sql = "UPDATE tbl_edict SET detail = ? " + "WHERE word LIKE '" + old + "'";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, new_vi);
                pstmt.executeUpdate();
            }
            else if (new_vi.equals("")) {
                String sql = "UPDATE tbl_edict SET word = ? " + "WHERE word LIKE '" + old + "'";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, new_en);
                pstmt.executeUpdate();
            }
            else {
                String sql = "UPDATE tbl_edict SET word = ? , " + "detail = ? " + "WHERE word LIKE '" + old + "'";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(2, new_vi);
                pstmt.setString(1, new_en);
                pstmt.executeUpdate();
            }
            return "\"" + old + "\"" + " is edited";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public ArrayList<String> listSearch(String s) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String sql = "SELECT * from tbl_edict WHERE word LIKE '" + s + "%' order by LOWER(word) LIMIT 10;";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("word"));
            }
            rs.close();
            return list;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return list;
        }
    }

    public void TextToSpeech(String s) {
        if (!dictionaryLookup(s).equals("Not find")) {
            try {
                System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");
                Voice voice = VoiceManager.getInstance().getVoice("kevin16");
                voice.allocate();
                voice.speak( s );
                voice.deallocate();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String callAPI(String s) {
        String target = "vi";
        String source = "en";
        String tem = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "application/gzip")
                    .header("X-RapidAPI-Key", "bb20d3bb12mshe74cc5bd33c935ap1d8303jsn9bb169f70656")
                    .header("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                    .method("POST", HttpRequest.BodyPublishers.ofString("q=" + s + "!&target=" + target + "&source=" + source))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            int x = response.body().lastIndexOf("!" + "\"}");
            int y = response.body().lastIndexOf(":" + "\"");
            tem = response.body().substring(y + 2, x);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tem;
    }

}
