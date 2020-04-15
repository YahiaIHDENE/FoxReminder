package m.incrementrestservice.poulouloureminder.notifications;

public class Sender {
    public Data data;
    String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
