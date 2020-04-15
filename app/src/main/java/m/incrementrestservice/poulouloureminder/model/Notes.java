package m.incrementrestservice.poulouloureminder.model;

public class Notes {

    public String title;
    public  String date;
    public  String text;
    public String owner;
    public String search;
    public String id_note;

    public Notes() {
    }

    public Notes(String title, String date, String text, String owner,String search,String id_note) {
        this.title = title;
        this.date = date;
        this.text = text;
        this.owner = owner;
        this.search = search;
        this.id_note = id_note;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String gettext() {
        return text;
    }

    public void settext(String text) {
        this.text = text;
    }
}
