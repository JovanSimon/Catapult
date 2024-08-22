package com.example.rma_projekat_1.userDetails.edit.mappers

import com.example.rma_projekat_1.userDetails.edit.model.EditUiModel
import com.example.rma_projekat_1.users.db.UserData

fun UserData.asEditUiModel(): EditUiModel {
    return EditUiModel(
        name = this.name,
        lastname = this.lastName,
        nickname = this.nickName,
        email = this.email
    )
}