package com.whis.ui.customComposables

import android.text.TextUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomCheckBox(
    title: String,
    modifier: Modifier = Modifier,
    value: Boolean = false,
    isvalidate: Boolean = false,
    iserror: Boolean = false,
    onValueChange: (Boolean) -> Unit
) {
    var isvalid by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    isError = if(iserror){
        true
    }else{
        if (isvalidate) {
            !isvalid
        } else {
            false
        }
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .clickable {
                onValueChange(!value)
                if (isvalidate) {
                    isvalid = !value
                }
            }) {
            Checkbox(
                checked = value,
                onCheckedChange = { it ->
                    onValueChange(it)
                    if (isvalidate) {
                        isvalid = !value
                    }
                }
            )
            CustomText(
                title,
                modifier = Modifier
                    .padding(start = 2.dp),
            )
        }
        if (isError) {
            CustomText(
                "Please select $title",
                color = Color.Red,
                txtsize = 9.sp,
            )
        }
    }

}