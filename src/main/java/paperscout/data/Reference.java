package paperscout.data;

public class Reference {
    String _text;
    String _identifier; //e.g., [2] -> _identifier = 2

    Reference(String text) {
        _text = text;
    }

    public String getText() { return _text; }
}
