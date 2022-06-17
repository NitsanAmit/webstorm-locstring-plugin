package locstring.generator.actions;

public interface IExtractLocstringForm {

    String getKey();

    String getValue();

    String getComment();

    String getFileSelection();

    void keyError(String errorMessage);

    void valueError(String errorMessage);

    void commentError(String errorMessage);

    void fileSelectionError(String errorMessage);

}
