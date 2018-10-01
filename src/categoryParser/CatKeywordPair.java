package categoryParser;

public class CatKeywordPair {

    private String category;
    private String keyword;

    public CatKeywordPair(String category, String keyword) {
        this.category = category;
        this.keyword = keyword;
    }

    public String category() {
        return category;
    }

    public String keyword() {
        return keyword;
    }

    public String toString() {
        return "[" + category + ", " + keyword + "]";
    }
}
