package com.halalrishtey.models

import android.view.View

data class ProfileCardData(
    var data: User,
    var showBtnInterestListener: View.OnClickListener,
    var messageBtnListener: View.OnClickListener,
    var isUserShortlisted: Boolean
)