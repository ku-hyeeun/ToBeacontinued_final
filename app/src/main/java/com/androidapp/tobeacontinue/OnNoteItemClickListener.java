package com.androidapp.tobeacontinue;

import android.view.View;

interface OnNoteItemClickListener {
    public void OnItemClick(NoteAdapter.ViewHolder holder, View view, int position);
}
