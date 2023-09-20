package com.whis.utils

fun checkString(value:Any?,isempty:Boolean=false):String {
    if(value !=null){
        if(value is String){
            if(value.isNotEmpty()){
                return value
            }else{
                if(isempty){
                    return ""
                }else{
                    return "N/A"
                }
            }
        }else{
            if(value.toString().isNotEmpty()){
                return value.toString()
            }else{
                if(isempty){
                    return ""
                }else{
                    return "N/A"
                }
            }
        }
    }else{
        if(isempty){
            return ""
        }else{
            return "N/A"
        }
    }
}