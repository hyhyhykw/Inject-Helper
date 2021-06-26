package com.inject.plugin.form;


import com.inject.plugin.iface.OnCheckBoxStateChangedListener;
import com.inject.plugin.model.Element;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class Entry extends JPanel {

    protected EntryList mParent;
    protected Element mElement;
    protected ArrayList<String> mGeneratedIDs;
    protected OnCheckBoxStateChangedListener mListener;
    // ui
    protected JCheckBox mCheck;
    protected JLabel mType;
    protected JLabel mID;
    protected JCheckBox mEvent;

    protected JCheckBox mLongClickEvent;

    protected JCheckBox mCheckChangeEvent;

    protected JCheckBox mOnTextChangeEvent;
    protected JCheckBox mBeforeTextChangeEvent;
    protected JCheckBox mAfterTextChangeEvent;

    protected JCheckBox mOnPageChangeEvent;
    protected JCheckBox mOnPageScrollEvent;
    protected JCheckBox mOnPageStateChangeEvent;

    protected JTextField mName;
    protected Color mNameDefaultColor;
    protected Color mNameErrorColor = new JBColor(new Color(0x880000), new Color(0x880000));

    public JCheckBox getCheck() {
        return mCheck;
    }

    public void setListener(final OnCheckBoxStateChangedListener onStateChangedListener) {
        this.mListener = onStateChangedListener;
    }

    public Entry(EntryList parent, Element element, ArrayList<String> ids) {
        mElement = element;
        mParent = parent;
        mGeneratedIDs = ids;

        mCheck = new JCheckBox();
        mCheck.setPreferredSize(new Dimension(40, 26));
        if (!mGeneratedIDs.contains(element.getFullID(true))) {
            mCheck.setSelected(mElement.used);
        } else {
            mCheck.setSelected(false);
        }
        mCheck.addChangeListener(new CheckListener());

        mEvent = new JCheckBox();
        mEvent.setPreferredSize(new Dimension(100, 26));

        mLongClickEvent = new JCheckBox();
        mLongClickEvent.setPreferredSize(new Dimension(100, 26));

        mCheckChangeEvent = new JCheckBox();
        mCheckChangeEvent.setPreferredSize(new Dimension(100, 26));
        mOnTextChangeEvent = new JCheckBox();
        mOnTextChangeEvent.setPreferredSize(new Dimension(100, 26));
        mBeforeTextChangeEvent = new JCheckBox();
        mBeforeTextChangeEvent.setPreferredSize(new Dimension(100, 26));
        mAfterTextChangeEvent = new JCheckBox();
        mAfterTextChangeEvent.setPreferredSize(new Dimension(100, 26));
        mOnPageChangeEvent = new JCheckBox();
        mOnPageChangeEvent.setPreferredSize(new Dimension(100, 26));
        mOnPageScrollEvent = new JCheckBox();
        mOnPageScrollEvent.setPreferredSize(new Dimension(100, 26));
        mOnPageStateChangeEvent = new JCheckBox();
        mOnPageStateChangeEvent.setPreferredSize(new Dimension(100, 26));

        mType = new JLabel(mElement.name);
        mType.setPreferredSize(new Dimension(100, 26));

        mID = new JLabel(mElement.id);
        mID.setPreferredSize(new Dimension(100, 26));

        mName = new JTextField(mElement.fieldName, 10);
        mNameDefaultColor = mName.getBackground();
        mName.setPreferredSize(new Dimension(100, 26));
        mName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // empty
            }

            @Override
            public void focusLost(FocusEvent e) {
                syncElement();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 54));
        add(mCheck);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mType);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mID);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));

        add(mLongClickEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mCheckChangeEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mOnTextChangeEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mBeforeTextChangeEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mAfterTextChangeEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mOnPageChangeEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mOnPageScrollEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mOnPageStateChangeEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));

        add(mName);
        add(Box.createHorizontalGlue());

        checkState();
    }

    public Element syncElement() {
        mElement.used = mCheck.isSelected();
        mElement.isClick = mEvent.isSelected();
        mElement.fieldName = mName.getText();

        mElement.isLongClick = mLongClickEvent.isSelected();

        mElement.isCheckChange = mCheckChangeEvent.isSelected();

        mElement.isTextChange = mOnTextChangeEvent.isSelected();
        mElement.isBeforeTextChange = mBeforeTextChangeEvent.isSelected();
        mElement.isAfterTextChange = mAfterTextChangeEvent.isSelected();

        mElement.isOnPageChange = mOnPageChangeEvent.isSelected();
        mElement.isOnPageScroll = mOnPageScrollEvent.isSelected();
        mElement.isOnPageState = mOnPageStateChangeEvent.isSelected();

        if (mElement.checkValidity()) {
            mName.setBackground(mNameDefaultColor);
        } else {
            mName.setBackground(mNameErrorColor);
        }

        return mElement;
    }

    private void checkState() {
        if (mCheck.isSelected()) {
            mType.setEnabled(true);
            mID.setEnabled(true);
            mName.setEnabled(true);
        } else {
            mType.setEnabled(false);
            mID.setEnabled(false);
            mName.setEnabled(false);
        }

        mCheckChangeEvent.setEnabled(mElement.canUseCheck());

        mAfterTextChangeEvent.setEnabled(mElement.canUseTextChange());
        mOnTextChangeEvent.setEnabled(mElement.canUseTextChange());
        mBeforeTextChangeEvent.setEnabled(mElement.canUseTextChange());

        mOnPageChangeEvent.setEnabled(mElement.canUsePage());
        mOnPageScrollEvent.setEnabled(mElement.canUsePage());
        mOnPageStateChangeEvent.setEnabled(mElement.canUsePage());

        if (mListener != null) {
            mListener.changeState(mCheck.isSelected());
        }
    }

    // classes

    public class CheckListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent event) {
            checkState();
        }
    }

}