package categoryParser;

import sqlConnector.SQLConnector;

import java.sql.ResultSet;
import java.util.ArrayList;

public class CategoryParser {

    private ArrayList<CatKeywordPair> pairs;

    public CategoryParser() {
        readDatabase();
        this.pairs = new ArrayList<>();
    }

    private void readDatabase() {
        SQLConnector sql = new SQLConnector();
        ResultSet rs = sql.select("SELECT * FROM CategoryParser ORDER BY catID, keyword");
        try {
            while(rs.next())
                pairs.add(new CatKeywordPair(rs.getString("catID"), rs.getString("keyword")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isKeywordRecognized(String keyword) {
        for(int i = 0; i < pairs.size(); i++)
            if(pairs.get(i).keyword().toLowerCase().contains(keyword.toLowerCase()))
                return true;
        return false;
    }

    public String getKeywordFromCategory(String category) {
        for(int i = 0; i < pairs.size(); i++)
            if(pairs.get(i).category().toLowerCase().equals(category.toLowerCase()))
                return pairs.get(i).keyword();
        return null;
    }

    public void addKeywordFromID(String category, String keyword) {
        pairs.add(new CatKeywordPair(category, keyword));
        new SQLConnector().update("INSERT INTO `simpleBudget`.`CategoryParser` (`category`, `keyword`) VALUES " +
                "('" + category + "', '" + keyword + "')");

    }
}
