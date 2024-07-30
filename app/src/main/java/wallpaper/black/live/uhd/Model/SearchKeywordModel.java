package wallpaper.black.live.uhd.Model;

public class SearchKeywordModel {
    private String keyword;
    private int id;

    public SearchKeywordModel(int id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
