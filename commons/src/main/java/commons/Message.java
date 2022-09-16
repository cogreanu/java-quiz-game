package commons;

public class Message {

    private String type;
    private Object content;

    public Message() {
    }

    public Message(String type, Object content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
