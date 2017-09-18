package paperscout.data;

import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;

public class Reference {
    String _text; //Raw text of reference e.g., [13] A. Sabelfeld and D. Sands, “Declassification: Dimensions and principles,” J. Comput. Secur., vol. 17, no. 5, Oct. 2009.
    String _identifier; //e.g., [13] -> _identifier = 13
    String _title; //e.g., Declassification: Dimensions and principles

    Reference(BibEntry e) {
        _text = e.getText();
        _title = e.getFirstFieldValue(BibEntryFieldType.TITLE);
        _identifier = e.getKey();
    }

    public String getText() { return _text; }

    public String getTitle() {
        return _title;
    }

    public String getIdentifier() {
        return _identifier;
    }
}
