package locstring.generator.actions;

import java.io.File;

public interface LocstringSuggestions {

    String getLocstringFilePathSuggestion(File currentFile);

    String getLocstringFileNameSuggestion(File currentFile);

    String getLocstringKeySuggestion(File currentFile, String valueText);

}
