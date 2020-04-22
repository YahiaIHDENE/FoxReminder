package m.incrementrestservice.poulouloureminder.model;

public class User {

    public String id_user;
    public String username;
    public String email;
    public String ImageURL;
    public String status;
    public String search;
    public String count_stat;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String id_user, String username,String email, String ImageURL, String status, String search, String count_stat) {
        this.id_user = id_user;
        this.username = username;
        this.email = email;
        this.ImageURL = ImageURL;
        this.status = status;
        this.search = search;
        this.count_stat = count_stat;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
