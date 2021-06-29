package com.inject.plugin.form;


import com.inject.plugin.iface.OnCheckBoxStateChangedListener;
import com.inject.plugin.model.Element;
import com.intellij.ui.JBColor;
import com.intellij.ui.popup.OurHeavyWeightPopup;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
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
    protected JCheckBox mTouchEvent;

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

        JDialog eventDialog = new JDialog();
        eventDialog.setTitle("Choose events for " + element.fieldName);
        eventDialog.setPreferredSize(new Dimension(300, 250));

        JPanel eventContainer = new JPanel();
        eventContainer.setLayout(new BoxLayout(eventContainer, BoxLayout.Y_AXIS));
        JButton event = new JButton();

        event.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventDialog.setVisible(true);
            }
        });

        mEvent = new JCheckBox("onClick");
        mLongClickEvent = new JCheckBox("onLongClick");
        mTouchEvent = new JCheckBox("onTouch");

        mCheckChangeEvent = new JCheckBox("onCheckChange");
        mOnTextChangeEvent = new JCheckBox("onTextChange");
        mBeforeTextChangeEvent = new JCheckBox("beforeTextChange");
        mAfterTextChangeEvent = new JCheckBox("afterTextChange");
        mOnPageChangeEvent = new JCheckBox("onPageChange");
        mOnPageScrollEvent = new JCheckBox("onPageScroll");
        mOnPageStateChangeEvent = new JCheckBox("onPageScrollState");

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


        add(event);
        event.setText("chooseEvent");
        add(Box.createRigidArea(new Dimension(10, 0)));

        eventContainer.add(mEvent);
        eventContainer.add(mLongClickEvent);
        eventContainer.add(mTouchEvent);
        eventContainer.add(mCheckChangeEvent);
        eventContainer.add(mOnTextChangeEvent);
        eventContainer.add(mBeforeTextChangeEvent);
        eventContainer.add(mAfterTextChangeEvent);

        mOnTextChangeEvent.setEnabled(element.canUseTextChange());
        mBeforeTextChangeEvent.setEnabled(element.canUseTextChange());
        mAfterTextChangeEvent.setEnabled(element.canUseTextChange());

        eventContainer.add(mOnPageChangeEvent);
        eventContainer.add(mOnPageScrollEvent);
        eventContainer.add(mOnPageStateChangeEvent);

        mOnPageChangeEvent.setEnabled(element.canUsePage());
        mOnPageScrollEvent.setEnabled(element.canUsePage());
        mOnPageStateChangeEvent.setEnabled(element.canUsePage());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        JPanel jPanel = new JPanel();
        jPanel.add(eventContainer, constraints);

        eventDialog.add(jPanel);
        eventDialog.pack();
        eventDialog.setLocationRelativeTo(null);

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

        mElement.isOnTouch = mTouchEvent.isSelected();


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
