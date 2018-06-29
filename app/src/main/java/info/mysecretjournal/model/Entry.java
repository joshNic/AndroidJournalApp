package info.mysecretjournal.model;

public class Entry {
    public static final String TABLE_NAME = "entries";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ENTRY_TITLE = "entry_title";
    public static final String COLUMN_ENTRY_BODY = "entry_body";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ENTRY_TITLE + " TEXT,"
                    + COLUMN_ENTRY_BODY + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";
    private int id;
    private String entryTitle;
    private String entryBody;
    private String timestamp;

    // constructor
    public Entry() {
    }
    public Entry(int id, String entryTitle, String entryBody, String timestamp) {
        this.id = id;
        this.entryTitle = entryTitle;
        this.entryBody = entryBody;
        this.timestamp = timestamp;
    }

    public String getEntryTitle() {
        return entryTitle;
    }

    public void setEntryTitle(String entryTitle) {
        this.entryTitle = entryTitle;
    }

    public String getEntryBody() {
        return entryBody;
    }

    public void setEntryBody(String entryBody) {
        this.entryBody = entryBody;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
