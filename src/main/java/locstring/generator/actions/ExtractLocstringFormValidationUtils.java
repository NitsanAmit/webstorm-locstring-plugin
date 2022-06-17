package locstring.generator.actions;

public class ExtractLocstringFormValidationUtils {

    public static boolean isFormInputValid(IExtractLocstringForm locstringForm) {
        String keyText = locstringForm.getKey();
        String valueText = locstringForm.getValue();
        String commentText = locstringForm.getComment();
        String fileSelection = locstringForm.getFileSelection();
        if (valueText.isBlank()) {
            locstringForm.valueError("Value can't be empty!");
            return false;
        }
        if (commentText.isBlank()) {
            locstringForm.commentError("Comment can't be empty!");
            return false;
        }
        if (keyText.isBlank()) {
            locstringForm.keyError("Key can't be empty!");
            return false;
        }
        if (!keyText.trim().matches("[A-Za-z_]*")){
            locstringForm.keyError("Key must only contain letters and underscores");
            return false;
        }
        if (keyText.endsWith("_") || keyText.startsWith("_")) {
            locstringForm.keyError("Key must start and end with a letter");
            return false;
        }
        if (fileSelection.isBlank() || !fileSelection.endsWith(".locstring")) {
            locstringForm.fileSelectionError("Invalid file path");
            return false;
        }
        return true;
    }
}
