package com.inject.plugin.form;

import com.inject.plugin.iface.OnCheckBoxStateChangedListener;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class EntryHeader extends JPanel {

    protected JCheckBox mAllCheck;
    protected JLabel mType;
    protected JLabel mID;
    protected JLabel mEvent;
//    protected JLabel mLongEvent;
//    protected JLabel mCheckChangeEvent;
//    protected JLabel mOnTextChange;
//    protected JLabel mBeforeChange;
//    protected JLabel mAfterChange;
//    protected JLabel mOnPageChange;
//    protected JLabel mOnPageScroll;
//    protected JLabel mOnPageState;

    protected JLabel mName;
    protected OnCheckBoxStateChangedListener mAllListener;

    public void setAllListener(final OnCheckBoxStateChangedListener onStateChangedListener) {
        this.mAllListener = onStateChangedListener;
    }

    public EntryHeader() {
        mAllCheck = new JCheckBox();
        mAllCheck.setPreferredSize(new Dimension(40, 26));
        mAllCheck.setSelected(false);
        mAllCheck.addItemListener(new AllCheckListener());

        mType = new JLabel("Element");
        mType.setPreferredSize(new Dimension(100, 26));
        mType.setFont(new Font(mType.getFont().getFontName(), Font.BOLD, mType.getFont().getSize()));

        mID = new JLabel("ID");
        mID.setPreferredSize(new Dimension(100, 26));
        mID.setFont(new Font(mID.getFont().getFontName(), Font.BOLD, mID.getFont().getSize()));

        mEvent = new JLabel("Event");
        mEvent.setPreferredSize(new Dimension(100, 26));
        mEvent.setFont(new Font(mEvent.getFont().getFontName(), Font.BOLD, mEvent.getFont().getSize()));

//        mLongEvent = new JLabel("OnLongClick");
//        mLongEvent.setPreferredSize(new Dimension(100, 26));
//        mLongEvent.setFont(new Font(mLongEvent.getFont().getFontName(), Font.BOLD, mLongEvent.getFont().getSize()));
//
//        mCheckChangeEvent = new JLabel("OnCheckChanged");
//        mCheckChangeEvent.setPreferredSize(new Dimension(100, 26));
//        mCheckChangeEvent.setFont(new Font(mCheckChangeEvent.getFont().getFontName(), Font.BOLD, mCheckChangeEvent.getFont().getSize()));
//
//        mOnTextChange = new JLabel("OnTextChange");
//        mOnTextChange.setPreferredSize(new Dimension(100, 26));
//        mOnTextChange.setFont(new Font(mOnTextChange.getFont().getFontName(), Font.BOLD, mOnTextChange.getFont().getSize()));

//        mBeforeChange = new JLabel("BeforeTextChange");
//        mBeforeChange.setPreferredSize(new Dimension(100, 26));
//        mBeforeChange.setFont(new Font(mBeforeChange.getFont().getFontName(), Font.BOLD, mBeforeChange.getFont().getSize()));
//
//        mAfterChange = new JLabel("AfterTextChange");
//        mAfterChange.setPreferredSize(new Dimension(100, 26));
//        mAfterChange.setFont(new Font(mAfterChange.getFont().getFontName(), Font.BOLD, mAfterChange.getFont().getSize()));

//        mOnPageChange = new JLabel("OnPageChange");
//        mOnPageChange.setPreferredSize(new Dimension(100, 26));
//        mOnPageChange.setFont(new Font(mOnPageChange.getFont().getFontName(), Font.BOLD, mOnPageChange.getFont().getSize()));

//        mOnPageScroll = new JLabel("OnPageScroll");
//        mOnPageScroll.setPreferredSize(new Dimension(100, 26));
//        mOnPageScroll.setFont(new Font(mOnPageScroll.getFont().getFontName(), Font.BOLD, mOnPageScroll.getFont().getSize()));
//
//        mOnPageState = new JLabel("OnPageState");
//        mOnPageState.setPreferredSize(new Dimension(100, 26));
//        mOnPageState.setFont(new Font(mOnPageState.getFont().getFontName(), Font.BOLD, mOnPageState.getFont().getSize()));

        mName = new JLabel("Variable Name");
        mName.setPreferredSize(new Dimension(100, 26));
        mName.setFont(new Font(mName.getFont().getFontName(), Font.BOLD, mName.getFont().getSize()));

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createRigidArea(new Dimension(1, 0)));
        add(mAllCheck);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mType);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mID);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));

//        add(mLongEvent);
//        add(Box.createRigidArea(new Dimension(10, 0)));
//        add(mCheckChangeEvent);
//        add(Box.createRigidArea(new Dimension(10, 0)));
//        add(mOnTextChange);
//        add(Box.createRigidArea(new Dimension(10, 0)));
//        add(mBeforeChange);
//        add(Box.createRigidArea(new Dimension(10, 0)));
//        add(mAfterChange);
//        add(Box.createRigidArea(new Dimension(10, 0)));
//        add(mOnPageChange);
//        add(Box.createRigidArea(new Dimension(10, 0)));
//        add(mOnPageScroll);
//        add(Box.createRigidArea(new Dimension(10, 0)));
//        add(mOnPageState);
//        add(Box.createRigidArea(new Dimension(10, 0)));

        add(mName);
        add(Box.createHorizontalGlue());
    }

    public JCheckBox getAllCheck() {
        return mAllCheck;
    }

    // classes

    private class AllCheckListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (mAllListener != null) {
                mAllListener.changeState(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        }
    }
}
