package com.inject.plugin.iface;

/**
 * Listener for changes of the checkboxes in the dialog.
 *
 * @since 1.5.0
 */
public interface OnCheckBoxStateChangedListener {
    void changeState(boolean checked);
}
