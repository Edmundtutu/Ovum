package com.pac.ovum.utils.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

/**
 * A builder class for creating and displaying various types of dialogs in an Android application.
 * This class provides a fluent interface for setting dialog properties such as title, message,
 * buttons, and input fields.
 */
public class DialogBuilder {

    private Context context;
    private String title;
    private String message;
    private String positiveButtonText;
    private String negativeButtonText;
    private DialogInterface.OnClickListener positiveButtonListener;
    private DialogInterface.OnClickListener negativeButtonListener;
    private boolean isInputDialog = false;
    private String inputHint;
    private String inputValue;

    /**
     * Constructs a new DialogBuilder instance.
     *
     * @param context The context in which the dialog should be displayed.
     */
    public DialogBuilder(Context context) {
        this.context = context;
    }

    /**
     * Sets the title of the dialog.
     *
     * @param title The title to be displayed in the dialog.
     * @return The current instance of DialogBuilder for method chaining.
     */
    public DialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the message of the dialog.
     *
     * @param message The message to be displayed in the dialog.
     * @return The current instance of DialogBuilder for method chaining.
     */
    public DialogBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets the text and listener for the positive button.
     *
     * @param text The text to be displayed on the positive button.
     * @param listener The listener to be invoked when the positive button is clicked.
     * @return The current instance of DialogBuilder for method chaining.
     */
    public DialogBuilder setPositiveButton(String text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = listener;
        return this;
    }

    /**
     * Sets the text and listener for the negative button.
     *
     * @param text The text to be displayed on the negative button.
     * @param listener The listener to be invoked when the negative button is clicked.
     * @return The current instance of DialogBuilder for method chaining.
     */
    public DialogBuilder setNegativeButton(String text, DialogInterface.OnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeButtonListener = listener;
        return this;
    }

    /**
     * Configures the dialog to be an input dialog with a hint.
     *
     * @param hint The hint to be displayed in the input field.
     * @return The current instance of DialogBuilder for method chaining.
     */
    public DialogBuilder setInputDialog(String hint) {
        this.isInputDialog = true;
        this.inputHint = hint;
        return this;
    }

    /**
     * Retrieves the value entered in the input dialog.
     *
     * @return The input value as a String, or null if not applicable.
     */
    public String getInputValue() {
        return inputValue;
    }

    /**
     * Displays the dialog with the configured properties.
     */
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        if (isInputDialog) {
            final EditText input = new EditText(context);
            input.setHint(inputHint);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                inputValue = input.getText().toString();
                if (positiveButtonListener != null) {
                    positiveButtonListener.onClick(dialog, which);
                }
            });
        } else {
            builder.setMessage(message)
                    .setPositiveButton(positiveButtonText, positiveButtonListener)
                    .setNegativeButton(negativeButtonText, negativeButtonListener);
        }

        builder.create().show();
    }
}